#!/usr/bin/env bash

Check_DebugTools_Install()
{
    Check_Jdk_Install
}

Check_Jdk8_Dcevm_Install() {
    JAVA_CMD="$JAVA_HOME/bin/java"
    output=$("$JAVA_CMD" -XXaltjvm=dcevm -version 2>&1)
    if echo "$output" | grep -q "Dynamic Code Evolution"; then
        return 0
    elif echo "$output" | grep -q "missing.*dcevm"; then
        return 1
    else
        return 1
    fi
}

Check_Jdk11_Dcevm_Install() {
    JAVA_CMD="$JAVA_HOME/bin/java"
    output=$("$JAVA_CMD" -version 2>&1)
    if echo "$output" | grep -q "JBR-11.0.15.10-2043.56-dcevm"; then
        return 0
    else
        return 1
    fi
}

Check_Jdk17_Install() {
    JAVA_CMD="$JAVA_HOME/bin/java"
    output=$("$JAVA_CMD" -version 2>&1)
    if echo "$output" | grep -q "JBR-17.0.14+1-1367.22-fd"; then
        return 0
    else
        return 1
    fi
}

Check_Jdk21_Install() {
    JAVA_CMD="$JAVA_HOME/bin/java"
    output=$("$JAVA_CMD" -version 2>&1)
    if echo "$output" | grep -q "JBR-21.0.6+9-631.42-fd"; then
        return 0
    else
        return 1
    fi
}

Check_Jdk_Install() {
    Get_Java_Major_Ver
    if [[ "$MAJOR_VERSION" = "8" ]]; then
        if Check_Jdk8_Dcevm_Install; then
            Echo_Green "JDK8安装成功"
        else
            Echo_Red "错误: JDK8安装失败"
        fi
    elif [[ "$MAJOR_VERSION" = "11" ]]; then
        if Check_Jdk11_Dcevm_Install; then
            Echo_Green "JDK11安装成功"
        else
            Echo_Red "错误: JDK8安装失败"
        fi
    elif [[ "$MAJOR_VERSION" = "17" ]]; then
        if Check_Jdk17_Install; then
            Echo_Green "JDK17安装成功"
        else
            Echo_Red "错误: JDK17安装失败"
        fi
    elif [[ "$MAJOR_VERSION" = "21" ]]; then
        if Check_Jdk21_Install; then
            Echo_Green "JDK21安装成功"
        else
            Echo_Red "错误: JDK21安装失败"
        fi
    fi
}