#!/usr/bin/env bash

Check_Hosts()
{
    if grep -Eqi '^127.0.0.1[[:space:]]*localhost' /etc/hosts; then
        echo "Hosts: 正确."
    else
        echo "127.0.0.1 localhost.localdomain localhost" >> /etc/hosts
    fi
    if [ "${CheckMirror}" != "n" ]; then
        pingresult=`ping -c1 www.baidu.com 2>&1`
        echo "${pingresult}"
        if echo "${pingresult}" | grep -q "unknown host"; then
            echo "DNS 错误"
            echo "将 nameserver 写入 /etc/resolv.conf 配置中..."
            echo -e "nameserver 208.67.220.220\nnameserver 114.114.114.114" > /etc/resolv.conf
        else
            echo "DNS 正确"
        fi
    fi
}

Check_Download()
{
    Echo_Blue "[+] 下载所需文件"
    cd ${cur_dir}/src
    Download_File https://download.debug-tools.cc/${Debug_Tools_Properties_File_Name} ${Debug_Tools_Properties_File_Name}
    Download_File https://download.debug-tools.cc/${Debug_Tools_Agent_File_Name} ${Debug_Tools_Agent_File_Name}
    if [ $? -ne 0 ]; then
        Download_File https://github.com/future0923/debug-tools/releases/latest/download/${Debug_Tools_Agent_File_Name} ${Debug_Tools_Agent_File_Name}
        if [ $? -ne 0 ]; then
            Download_File https://gitee.com/future94/debug-tools/releases/download/latest/${Debug_Tools_Agent_File_Name} ${Debug_Tools_Agent_File_Name}
        fi
    fi
    Download_File https://download.debug-tools.cc/${Debug_Tools_Boot_File_Name} ${Debug_Tools_Boot_File_Name}
    if [ $? -ne 0 ]; then
        Download_File https://github.com/future0923/debug-tools/releases/latest/download/${Debug_Tools_Boot_File_Name} ${Debug_Tools_Boot_File_Name}
        if [ $? -ne 0 ]; then
            Download_File https://gitee.com/future94/debug-tools/releases/download/latest/${Debug_Tools_Boot_File_Name} ${Debug_Tools_Boot_File_Name}
        fi
    fi
    if [[ "${JdkSelect}" = "1" ]] && ! Check_Jdk8_Dcevm_Install; then
        if [[ "${Jdk8Select}" = "1" ]]; then
            Download_File https://download.debug-tools.cc/dcevm-jdk-1.8.0_181/${Jdk_8_File_Name} ${Jdk_8_File_Name}
            if [ $? -ne 0 ]; then
                Download_File https://github.com/future0923/debug-tools/releases/download/dcevm-jdk-1.8.0_181/${Jdk_8_File_Name} ${Jdk_8_File_Name}
            fi
        elif [[ "${Jdk8Select}" = "2" ]]; then
            Get_Java_Major_Ver
            if [[ "$MAJOR_VERSION" != "8" ]]; then
                Echo_Red "Current JDK version is $MAJOR_VERSION，not java 8"
                exit 1
            fi
            Get_Java_Small_Ver
            LIB_JVM_VERSION=""
            if [[ "$SMALL_VERSION" -le 66 ]]; then
                LIB_JVM_VERSION=66
            elif [[ "$SMALL_VERSION" -ge 181 ]]; then
                LIB_JVM_VERSION=181
            else
                LIB_JVM_VERSION=$SMALL_VERSION
            fi
            Download_File https://download.debug-tools.cc/libjvm/libjvm${LIB_JVM_VERSION}.so libjvm${LIB_JVM_VERSION}.so
            if [ $? -ne 0 ]; then
                Download_File https://github.com/future0923/debug-tools/releases/download/libjvm.so/libjvm${LIB_JVM_VERSION}.so libjvm${LIB_JVM_VERSION}.so
            fi
        fi
    elif [[ "${JdkSelect}" = "2" ]] && ! Check_Jdk11_Dcevm_Install; then
        Download_File https://download.debug-tools.cc/jbr-jdk-11-dcevm/${Jdk_11_File_Name} ${Jdk_11_File_Name}
        if [ $? -ne 0 ]; then
            Download_File https://cache-redirector.jetbrains.com/intellij-jbr/${Jdk_11_File_Name} ${Jdk_11_File_Name}
        fi
    elif [[ "${JdkSelect}" = "3" ]] && ! Check_Jdk17_Install; then
        Download_File https://download.debug-tools.cc/jbrsdk-17.0.14-fastdebug/${Jdk_17_File_Name} ${Jdk_17_File_Name}
        if [ $? -ne 0 ]; then
            Download_File https://cache-redirector.jetbrains.com/intellij-jbr/${Jdk_17_File_Name} ${Jdk_17_File_Name}
        fi
    elif [[ "${JdkSelect}" = "4" ]] && ! Check_Jdk21_Install; then
        Download_File https://download.debug-tools.cc/jbrsdk-21.0.6-fastdebug/${Jdk_21_File_Name} ${Jdk_21_File_Name}
        if [ $? -ne 0 ]; then
            Download_File https://cache-redirector.jetbrains.com/intellij-jbr/${Jdk_21_File_Name} ${Jdk_21_File_Name}
        fi
    fi
}

Add_Swap()
{

    Disk_Avail=$(($(df -mP /var | tail -1 | awk '{print $4}' | sed s/[[:space:]]//g)/1024))

    DD_Count='1024'
    if [[ "${MemTotal}" -lt 1024 ]]; then
        DD_Count='1024'
        if [[ "${Disk_Avail}" -lt 5 ]]; then
            Enable_Swap='n'
        fi
    elif [[ "${MemTotal}" -ge 1024 && "${MemTotal}" -le 2048 ]]; then
        DD_Count='2048'
        if [[ "${Disk_Avail}" -lt 13 ]]; then
            Enable_Swap='n'
        fi
    elif [[ "${MemTotal}" -ge 2048 && "${MemTotal}" -le 4096 ]]; then
        DD_Count='4096'
        if [[ "${Disk_Avail}" -lt 17 ]]; then
            Enable_Swap='n'
        fi
    elif [[ "${MemTotal}" -ge 4096 && "${MemTotal}" -le 16384 ]]; then
        DD_Count='8192'
        if [[ "${Disk_Avail}" -lt 19 ]]; then
            Enable_Swap='n'
        fi
    elif [[ "${MemTotal}" -ge 16384 ]]; then
        DD_Count='8192'
        if [[ "${Disk_Avail}" -lt 27 ]]; then
            Enable_Swap='n'
        fi
    fi
    Swap_Total=$(awk '/SwapTotal/ {printf( "%d\n", $2 / 1024 )}' /proc/meminfo)
    if [[ "${Enable_Swap}" = "y" && "${Swap_Total}" -le 512 && ! -s /var/swapfile ]]; then
        echo "添加 swap 文件..."
        [ $(cat /proc/sys/vm/swappiness) -eq 0 ] && sysctl vm.swappiness=10
        dd if=/dev/zero of=/var/swapfile bs=1M count=${DD_Count}
        chmod 0600 /var/swapfile
        echo "开启 Swap..."
        /sbin/mkswap /var/swapfile
        /sbin/swapon /var/swapfile
        if [ $? -eq 0 ]; then
            [ `grep -L '/var/swapfile'    '/etc/fstab'` ] && echo "/var/swapfile swap swap defaults 0 0" >>/etc/fstab
            /sbin/swapon -s
        else
            rm -f /var/swapfile
            echo "添加 swap 失败..."
        fi
    fi
}

Modify_Source()
{
    if [ "${DISTRO}" = "RHEL" ]; then
        if subscription-manager status; then
            Echo_Blue "系统中已存在 RHEL 订阅，无需设置第三方软件源。"
            Get_RHEL_Version
            if echo "${RHEL_Version}" | grep -Eqi "^[89]"; then
                subscription-manager repos --enable codeready-builder-for-rhel-${RHEL_Version}-${DB_ARCH}-rpms
            fi
        else
            RHEL_Modify_Source
        fi
    elif [ "${DISTRO}" = "Ubuntu" ]; then
        Ubuntu_Modify_Source
    elif [ "${DISTRO}" = "CentOS" ]; then
        CentOS6_Modify_Source
        CentOS8_Modify_Source
    fi
}

CentOS6_Modify_Source()
{
    if echo "${CentOS_Version}" | grep -Eqi "^6"; then
        Echo_Yellow "CentOS 6 现在已经停止维护，请使用 vault 仓库。"
        mkdir /etc/yum.repos.d/backup
        mv /etc/yum.repos.d/*.repo /etc/yum.repos.d/backup/
        \cp ${cur_dir}/conf/CentOS6-Base-Vault.repo /etc/yum.repos.d/CentOS-Base.repo
    fi
}

CentOS8_Modify_Source()
{
    if echo "${CentOS_Version}" | grep -Eqi "^8" && [ "${isCentosStream}" != "y" ]; then
        Echo_Yellow "CentOS 8 现在已经停止维护，请使用 vault 仓库。"
        if [ ! -s /etc/yum.repos.d/CentOS8-vault.repo ]; then
            mkdir /etc/yum.repos.d/backup
            mv /etc/yum.repos.d/*.repo /etc/yum.repos.d/backup/
            \cp ${cur_dir}/conf/CentOS8-vault.repo /etc/yum.repos.d/CentOS8-vault.repo
        fi
    fi
}

RHEL_Modify_Source()
{
    Get_RHEL_Version
    if [ "${RHELRepo}" = "local" ]; then
        echo "不要更改 RHEL 仓库，请使用你设置的仓库。"
    else
        echo "RHEL ${RHEL_Ver} 将使用阿里云 CentOS 仓库。"
        if [ ! -s "/etc/yum.repos.d/Centos-${RHEL_Ver}.repo" ]; then
            if command -v curl >/dev/null 2>&1; then
                curl http://mirrors.aliyun.com/repo/Centos-${RHEL_Ver}.repo -o /etc/yum.repos.d/Centos-${RHEL_Ver}.repo
            else
                wget --prefer-family=IPv4 http://mirrors.aliyun.com/repo/Centos-${RHEL_Ver}.repo -O /etc/yum.repos.d/Centos-${RHEL_Ver}.repo
            fi
        fi
        if echo "${RHEL_Version}" | grep -Eqi "^6"; then
            sed -i "s#centos/\$releasever#centos-vault/\$releasever#g" /etc/yum.repos.d/Centos-${RHEL_Ver}.repo
            sed -i "s/\$releasever/${RHEL_Version}/g" /etc/yum.repos.d/Centos-${RHEL_Ver}.repo
        elif echo "${RHEL_Version}" | grep -Eqi "^7"; then
            sed -i "s/\$releasever/7/g" /etc/yum.repos.d/Centos-${RHEL_Ver}.repo
        elif echo "${RHEL_Version}" | grep -Eqi "^8"; then
            sed -i "s#centos/\$releasever#centos-vault/8.5.2111#g" /etc/yum.repos.d/Centos-${RHEL_Ver}.repo
        elif echo "${RHEL_Version}" | grep -Eqi "^9"; then
            [[ -s /etc/yum.repos.d/Centos-9.repo ]] && rm -f /etc/yum.repos.d/Centos-9.repo
            \cp ${cur_dir}/conf/rhel-9.repo /etc/yum.repos.d/Centos-9.repo
        fi
        yum clean all
        yum makecache
    fi
    sed -i "s/^enabled[ ]*=[ ]*1/enabled=0/" /etc/yum/pluginconf.d/subscription-manager.conf
}

Ubuntu_Modify_Source()
{
    if [ "${country}" = "CN" ]; then
        OldReleasesURL='http://mirrors.ustc.edu.cn/ubuntu-old-releases/'
    else
        OldReleasesURL='http://old-releases.ubuntu.com/ubuntu/'
    fi
    CodeName=''
    if grep -Eqi "10.10" /etc/*-release || echo "${Ubuntu_Version}" | grep -Eqi '^10.10'; then
        CodeName='maverick'
    elif grep -Eqi "11.04" /etc/*-release || echo "${Ubuntu_Version}" | grep -Eqi '^11.04'; then
        CodeName='natty'
    elif  grep -Eqi "11.10" /etc/*-release || echo "${Ubuntu_Version}" | grep -Eqi '^11.10'; then
        CodeName='oneiric'
    elif grep -Eqi "12.10" /etc/*-release || echo "${Ubuntu_Version}" | grep -Eqi '^12.10'; then
        CodeName='quantal'
    elif grep -Eqi "13.04" /etc/*-release || echo "${Ubuntu_Version}" | grep -Eqi '^13.04'; then
        CodeName='raring'
    elif grep -Eqi "13.10" /etc/*-release || echo "${Ubuntu_Version}" | grep -Eqi '^13.10'; then
        CodeName='saucy'
    elif grep -Eqi "10.04" /etc/*-release || echo "${Ubuntu_Version}" | grep -Eqi '^10.04'; then
        CodeName='lucid'
    elif grep -Eqi "14.10" /etc/*-release || echo "${Ubuntu_Version}" | grep -Eqi '^14.10'; then
        CodeName='utopic'
    elif grep -Eqi "15.04" /etc/*-release || echo "${Ubuntu_Version}" | grep -Eqi '^15.04'; then
        CodeName='vivid'
    elif grep -Eqi "12.04" /etc/*-release || echo "${Ubuntu_Version}" | grep -Eqi '^12.04'; then
        CodeName='precise'
    elif grep -Eqi "15.10" /etc/*-release || echo "${Ubuntu_Version}" | grep -Eqi '^15.10'; then
        CodeName='wily'
    elif grep -Eqi "16.10" /etc/*-release || echo "${Ubuntu_Version}" | grep -Eqi '^16.10'; then
        CodeName='yakkety'
    elif grep -Eqi "14.04" /etc/*-release || echo "${Ubuntu_Version}" | grep -Eqi '^14.04'; then
        Ubuntu_Deadline trusty
    elif grep -Eqi "17.04" /etc/*-release || echo "${Ubuntu_Version}" | grep -Eqi '^17.04'; then
        CodeName='zesty'
    elif grep -Eqi "17.10" /etc/*-release || echo "${Ubuntu_Version}" | grep -Eqi '^17.10'; then
        CodeName='artful'
    elif grep -Eqi "16.04" /etc/*-release || echo "${Ubuntu_Version}" | grep -Eqi '^16.04'; then
        Ubuntu_Deadline xenial
    elif grep -Eqi "16.10" /etc/*-release || echo "${Ubuntu_Version}" | grep -Eqi '^16.10'; then
        CodeName='yakkety'
    elif grep -Eqi "18.04" /etc/*-release || echo "${Ubuntu_Version}" | grep -Eqi '^18.04'; then
        Ubuntu_Deadline bionic
    elif grep -Eqi "18.10" /etc/*-release || echo "${Ubuntu_Version}" | grep -Eqi '^18.10'; then
        CodeName='cosmic'
    elif grep -Eqi "19.04" /etc/*-release || echo "${Ubuntu_Version}" | grep -Eqi '^19.04'; then
        CodeName='disco'
    elif grep -Eqi "19.10" /etc/*-release || echo "${Ubuntu_Version}" | grep -Eqi '^19.10'; then
        CodeName='eoan'
    elif grep -Eqi "20.10" /etc/*-release || echo "${Ubuntu_Version}" | grep -Eqi '^20.10'; then
        CodeName='groovy'
    elif grep -Eqi "21.04" /etc/*-release || echo "${Ubuntu_Version}" | grep -Eqi '^21.04'; then
        CodeName='hirsute'
    elif grep -Eqi "21.10" /etc/*-release || echo "${Ubuntu_Version}" | grep -Eqi '^21.10'; then
        CodeName='impish'
    elif grep -Eqi "22.10" /etc/*-release || echo "${Ubuntu_Version}" | grep -Eqi '^22.10'; then
        CodeName='kinetic'
    elif grep -Eqi "23.04" /etc/*-release || echo "${Ubuntu_Version}" | grep -Eqi '^23.04'; then
        CodeName='lunar'
    elif grep -Eqi "23.10" /etc/*-release || echo "${Ubuntu_Version}" | grep -Eqi '^23.10'; then
        Ubuntu_Deadline mantic
    fi
    if [ "${CodeName}" != "" ]; then
        \cp /etc/apt/sources.list /etc/apt/sources.list.$(date +"%Y%m%d")
        cat > /etc/apt/sources.list<<EOF
deb ${OldReleasesURL} ${CodeName} main restricted universe multiverse
deb ${OldReleasesURL} ${CodeName}-security main restricted universe multiverse
deb ${OldReleasesURL} ${CodeName}-updates main restricted universe multiverse
deb ${OldReleasesURL} ${CodeName}-proposed main restricted universe multiverse
deb ${OldReleasesURL} ${CodeName}-backports main restricted universe multiverse
deb-src ${OldReleasesURL} ${CodeName} main restricted universe multiverse
deb-src ${OldReleasesURL} ${CodeName}-security main restricted universe multiverse
deb-src ${OldReleasesURL} ${CodeName}-updates main restricted universe multiverse
deb-src ${OldReleasesURL} ${CodeName}-proposed main restricted universe multiverse
deb-src ${OldReleasesURL} ${CodeName}-backports main restricted universe multiverse
EOF
    fi
}

Check_Old_Releases_URL()
{
    OR_Status=`wget --spider --server-response ${OldReleasesURL}/dists/$1/Release 2>&1 | awk '/^  HTTP/{print $2}'`
    if [ "${OR_Status}" = "200" ]; then
        echo "Ubuntu 旧版本状态: ${OR_Status}";
        CodeName="$1"
    fi
}

Ubuntu_Deadline()
{
    trusty_deadline=`date -d "2024-4-30 00:00:00" +%s`
    xenial_deadline=`date -d "2026-4-30 00:00:00" +%s`
    bionic_deadline=`date -d "2028-7-30 00:00:00" +%s`
    mantic_deadline=`date -d "2024-7-30 00:00:00" +%s`
    cur_time=`date  +%s`
    case "$1" in
        trusty)
            if [ ${cur_time} -gt ${trusty_deadline} ]; then
                echo "${cur_time} > ${trusty_deadline}"
                Check_Old_Releases_URL trusty
            fi
            ;;
        xenial)
            if [ ${cur_time} -gt ${xenial_deadline} ]; then
                echo "${cur_time} > ${xenial_deadline}"
                Check_Old_Releases_URL xenial
            fi
            ;;
        bionic)
            if [ ${cur_time} -gt ${bionic_deadline} ]; then
                echo "${cur_time} > ${bionic_deadline}"
                Check_Old_Releases_URL bionic
            fi
            ;;
        mantic)
            if [ ${cur_time} -gt ${mantic_deadline} ]; then
                echo "${cur_time} > ${mantic_deadline}"
                Check_Old_Releases_URL mantic
            fi
            ;;
    esac
}

Yum_Dependencies()
{
    if [ -s /etc/yum.conf ]; then
        \cp /etc/yum.conf /etc/yum.conf.lnmp
        sed -i 's:exclude=.*:exclude=:g' /etc/yum.conf
    fi

    Echo_Blue "[+] 正在安装依赖的软件包..."
    for packages in wget tar bzip2 curl unzip;
    do yum -y install $packages; done

    if [ -s /etc/yum.conf.lnmp ]; then
        mv -f /etc/yum.conf.lnmp /etc/yum.conf
    fi
}

Apt_Dependencies()
{
    Echo_Blue "[+] 正在安装依赖的软件包..."
    apt-get update -y
    [[ $? -ne 0 ]] && apt-get update --allow-releaseinfo-change -y
    apt-get autoremove -y
    apt-get -fy install
    export DEBIAN_FRONTEND=noninteractive
    for packages in wget tar bzip2 curl unzip;
    do apt-get --no-install-recommends install -y $packages; done
}