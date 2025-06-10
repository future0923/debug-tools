#!/usr/bin/env bash

Jdk_Info=('JDK8' 'JDK11' 'JDK17' 'JDK21')

Jdk_Selection()
{
#which JDK Version do you want to install?
    if [ -z ${JdkSelect} ]; then
        JdkSelect="1"
        Echo_Yellow "你有4种选择来安装JDK"
        echo "1: 安装 ${Jdk_Info[0]}"
        echo "2: 安装 ${Jdk_Info[1]}"
        echo "3: 安装 ${Jdk_Info[2]}"
        echo "4: 安装 ${Jdk_Info[3]}"
        echo "0: 不安装JDK！"
        read -p "请选择 (1, 2, 3, 4 或者 0): " JdkSelect
    fi

    case "${JdkSelect}" in
    1)
        Echo_Blue "你将安装 ${Jdk_Info[0]}"
        if [ -z ${Jdk8Select} ]; then
            Jdk8Select="1"
            Echo_Yellow "你有2种选择来安装JDK8"
            echo "1: 直接使用我们打包好的JDK (推荐)"
            echo "2: 基于当前 JAVA_HOME 的 JDK 改造"
            read -p "请选择 (1 或者 2): " Jdk8Select
        fi
        ;;
    2)
        echo "使用 JetBrainsRuntime JDK 来构建"
        ;;
    3)
#        if [[ "${JDK_ARCH}" = "x86_64" || "${JDK_ARCH}" = "i686" || "${JDK_ARCH}" = "aarch64" ]]; then
#            echo "You will install ${JDK_ARCH} OS"
#        fi
        echo "使用 JetBrainsRuntime JDK 来构建"
        ;;
    4)
        echo "使用 JetBrainsRuntime JDK 来构建"
        ;;
    0)
        echo "不安装JDK！"
        ;;
    *)
        Echo_Red "没有选择 (1, 2, 3, 4 或者 0)"
        exit 1
    esac
}
