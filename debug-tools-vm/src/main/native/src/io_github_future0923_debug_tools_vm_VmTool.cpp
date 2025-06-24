/**
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
#include <stdio.h>
#include <jni.h>
#include <jni_md.h>
#include <jvmti.h>
#include "io_github_future0923_debug_tools_vm_VmTool.h"


static jvmtiEnv *jvmti;
static jlong tagCounter = 0;

struct LimitCounter {
    // 当前计数
    jint currentCounter;
    // 计数上限
    jint limitValue;

    void init(jint limit) {
        currentCounter = 0;
        limitValue = limit;
    }

    void countDown() {
        currentCounter++;
    }

    bool allow() {
        if (limitValue < 0) {
            return true;
        }
        return limitValue > currentCounter;
    }
};

// 每次 IterateOverInstancesOfClass 调用前需要先 init
static LimitCounter limitCounter = {0, 0};

extern "C"

/*
 * 初始化JVMTI
 */
int init_agent(JavaVM *vm, void *reserved) {
    jint rc;
    //获取 JVMTI 环境，赋值给全局变量 jvmti。
    rc = vm->GetEnv((void **)&jvmti, JVMTI_VERSION_1_2);
    if (rc != JNI_OK) {
        fprintf(stderr, "ERROR: vmtool Unable to create jvmtiEnv, GetEnv failed, error=%d\n", rc);
        return -1;
    }
    // 声明 JVMTI 能力
    jvmtiCapabilities capabilities = {0};
    // 这里启用了 can_tag_objects（对象标记功能）。
    capabilities.can_tag_objects = 1;
    jvmtiError error = jvmti->AddCapabilities(&capabilities);
    if (error) {
        fprintf(stderr, "ERROR: vmtool JVMTI AddCapabilities failed!%u\n", error);
        return JNI_FALSE;
    }

    return JNI_OK;
}

/*
 * 当 Java 进程启动时，加载 JVMTI 代理
 */
extern "C" JNIEXPORT jint JNICALL
Agent_OnLoad(JavaVM *vm, char *options, void *reserved) {
    return init_agent(vm, reserved);
}

/*
 *  当 JVMTI 代理动态附加到 JVM 时，初始化。
 */
extern "C" JNIEXPORT jint JNICALL
Agent_OnAttach(JavaVM* vm, char* options, void* reserved) {
    return init_agent(vm, reserved);
}

/*
 * 当 JNI 库被 System.loadLibrary 加载时，执行初始化
 */
extern "C" JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM* vm, void* reserved) {
    init_agent(vm, reserved);
    return JNI_VERSION_1_6;
}

/*
 * 实现java io.github.future0923.debug.tools.vm.VmTool#forceGc0 native方法
 * 强制jvm进行gc
 */
extern "C"
JNIEXPORT void JNICALL
Java_io_github_future0923_debug_tools_vm_VmTool_forceGc0(JNIEnv *env, jclass thisClass) {
    // 调用 JVM 的 GC（垃圾回收），强制回收内存。
    jvmti->ForceGarbageCollection();
}

extern "C"
jlong getTag() {
    return ++tagCounter;
}

extern "C"
jvmtiIterationControl JNICALL
HeapObjectCallback(jlong class_tag, jlong size, jlong *tag_ptr, void *user_data) {
    jlong *data = static_cast<jlong *>(user_data);
    *tag_ptr = *data;

    limitCounter.countDown();
    if (limitCounter.allow()) {
        return JVMTI_ITERATION_CONTINUE;
    }else {
        return JVMTI_ITERATION_ABORT;
    }
}

/*
 * 实现java io.github.future0923.debug.tools.vm.VmTool#getInstances0 native方法
 * 获取某个class在jvm中当前所有存活实例
 */
extern "C"
JNIEXPORT jobjectArray JNICALL
Java_io_github_future0923_debug_tools_vm_VmTool_getInstances0(JNIEnv *env, jclass thisClass, jclass klass, jint limit) {
    jlong tag = getTag();
    limitCounter.init(limit);
    // 遍历 klass 类型的所有实例。
    jvmtiError error = jvmti->IterateOverInstancesOfClass(klass, JVMTI_HEAP_OBJECT_EITHER,
                                               HeapObjectCallback, &tag);
    if (error) {
        printf("ERROR: JVMTI IterateOverInstancesOfClass failed!%u\n", error);
        return NULL;
    }

    jint count = 0;
    jobject *instances;
    // 获取 所有被标记的实例。
    error = jvmti->GetObjectsWithTags(1, &tag, &count, &instances, NULL);
    if (error) {
        printf("ERROR: JVMTI GetObjectsWithTags failed!%u\n", error);
        return NULL;
    }
    // 返回 Java 数组。
    jobjectArray array = env->NewObjectArray(count, klass, NULL);
    //添加元素到数组
    for (int i = 0; i < count; i++) {
        env->SetObjectArrayElement(array, i, instances[i]);
    }
    jvmti->Deallocate(reinterpret_cast<unsigned char *>(instances));
    return array;
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_future0923_debug_tools_vm_VmTool_sumInstanceSize0(JNIEnv *env, jclass thisClass, jclass klass) {
    jlong tag = getTag();
    limitCounter.init(-1);
    jvmtiError error = jvmti->IterateOverInstancesOfClass(klass, JVMTI_HEAP_OBJECT_EITHER,
                                               HeapObjectCallback, &tag);
    if (error) {
        printf("ERROR: JVMTI IterateOverInstancesOfClass failed!%u\n", error);
        return -1;
    }

    jint count = 0;
    jobject *instances;
    error = jvmti->GetObjectsWithTags(1, &tag, &count, &instances, NULL);
    if (error) {
        printf("ERROR: JVMTI GetObjectsWithTags failed!%u\n", error);
        return -1;
    }

    jlong sum = 0;
    for (int i = 0; i < count; i++) {
        jlong size = 0;
        jvmti->GetObjectSize(instances[i], &size);
        sum = sum + size;
    }
    jvmti->Deallocate(reinterpret_cast<unsigned char *>(instances));
    return sum;
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_future0923_debug_tools_vm_VmTool_getInstanceSize0
        (JNIEnv *env, jclass thisClass, jobject instance) {
    jlong size = -1;
    jvmtiError error = jvmti->GetObjectSize(instance, &size);
    if (error) {
        printf("ERROR: JVMTI GetObjectSize failed!%u\n", error);
    }
    return size;
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_github_future0923_debug_tools_vm_VmTool_countInstances0(JNIEnv *env, jclass thisClass, jclass klass) {
    jlong tag = getTag();
    limitCounter.init(-1);
    jvmtiError error = jvmti->IterateOverInstancesOfClass(klass, JVMTI_HEAP_OBJECT_EITHER,
                                               HeapObjectCallback, &tag);
    if (error) {
        printf("ERROR: JVMTI IterateOverInstancesOfClass failed!%u\n", error);
        return -1;
    }

    jint count = 0;
    error = jvmti->GetObjectsWithTags(1, &tag, &count, NULL, NULL);
    if (error) {
        printf("ERROR: JVMTI GetObjectsWithTags failed!%u\n", error);
        return -1;
    }
    return count;
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_io_github_future0923_debug_tools_vm_VmTool_getAllLoadedClasses0
        (JNIEnv *env, jclass thisClass, jclass kclass) {
    jclass *classes;
    jint count = 0;

    jvmtiError error = jvmti->GetLoadedClasses(&count, &classes);
    if (error) {
        printf("ERROR: JVMTI GetLoadedClasses failed!\n");
        return NULL;
    }

    jobjectArray array = env->NewObjectArray(count, kclass, NULL);
    //添加元素到数组
    for (int i = 0; i < count; i++) {
        env->SetObjectArrayElement(array, i, classes[i]);
    }
    jvmti->Deallocate(reinterpret_cast<unsigned char *>(classes));
    return array;
}