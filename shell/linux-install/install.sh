#!/usr/bin/env bash
export PATH=$PATH:/bin:/sbin:/usr/bin:/usr/sbin:/usr/local/bin:/usr/local/sbin:~/bin

# Check if user is root
if [ $(id -u) != "0" ]; then
    echo "错误: 请使用root权限执行该脚本."
    exit 1
fi

cur_dir=$(pwd)

. conf/debug-tools.conf
. include/base.sh
. include/dependencies.sh
. include/selection.sh
. include/jdk.sh
. include/agent.sh
. include/check.sh

Get_Dist_Name

if [ "${DISTRO}" = "unknow" ]; then
    Echo_Red "错误: 获取Linux发行版失败"
    exit 1
fi

clear

DebugTools_Install() {
    Jdk_Selection
    Press_Install
    Print_APP_Ver
    Check_Hosts
    Modify_Source
    Add_Swap
    if [ "$PM" = "yum" ]; then
        Yum_Dependencies
    elif [ "$PM" = "apt" ]; then
        Apt_Dependencies
    fi
    Check_Download
    if [[ "${JdkSelect}" = "1" ]] && ! Check_Jdk8_Dcevm_Install; then
        Install_Jdk_8
    elif [ "${JdkSelect}" = "2" ] && ! Check_Jdk11_Dcevm_Install; then
        Install_Jdk_11
    elif [ "${JdkSelect}" = "3" ] && ! Check_Jdk17_Install; then
        Install_Jdk_17
    elif [ "${JdkSelect}" = "4" ] && ! Check_Jdk21_Install; then
        Install_Jdk_21
    fi
    Install_Debug_Tools
    Check_DebugTools_Install
}

DebugTools_Install 2>&1 | tee /root/debug-tools-install.log

exit