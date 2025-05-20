/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.springframework.context.ApplicationContext

// 返回结果
class ResultDTO {
    // gi的结果
    ApplicationContext[] gi
    // gb的结果
    TestBean[] gb
    // 调用hello方法的执行结果
    String helloMethodResult
    // 当前环境
    String active
    // 应用名
    String applicationName
}

// 要注入TestBean
class TestBean {

    String hello(String name) {
        return "hello " + name
    }
}

def result = new ResultDTO()

// gi或者getInstances 获取jvm实例
def v1 = gi(ApplicationContext.class)
result.gi = v1
gi ApplicationContext.class
getInstances(ApplicationContext.class)
getInstances ApplicationContext.class

def testBean = new TestBean()
// 向spring中注册bean
rb(testBean)
//rb("testBean", testBean)
//registerBean(testBean)
//registerBean("testBean", testBean)


// gb或者getBean 获取spring bean
def v2 = gb(TestBean.class)
result.gb = v2
// 获取注入的testBean，执行hello方法
result.helloMethodResult = gb("testBean")[0].hello("debug tools")
gb TestBean.class
getBean(TestBean.class)
getBean("testBean")
getBean TestBean.class

// 注销bean
urb("testBean")
//unregisterBean("testBean")

// gActive或者getSpringProfilesActive 获取当前spring环境
def v3 = gActive()
result.active = v3
getSpringProfilesActive()

// gsc或getSpringConfig 获取spring配置
def v4 = gsc("spring.application.name")
result.applicationName = v4
gsc "spring.application.name"
getSpringConfig("spring.application.name")
getSpringConfig "spring.application.name"
return result