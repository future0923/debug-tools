#!/usr/bin/env bash

Install_Jdk_8()
{
    Echo_Blue "[+] 开始安装 ${Jdk_Ver}..."
    if [ "${Jdk8Select}" = "1" ]; then
        cd ${cur_dir}/src
        Tar_File ${Jdk_8_File_Name} ${Jdk_8_Dir}
        JAVA_DIR=${Jdk_Install_Dir}/${Jdk_8_Dir}
        rm -rf ${JAVA_DIR}
        mkdir -p ${JAVA_DIR}
        mv ${Jdk_8_Dir}/* ${JAVA_DIR}
        Set_Java_Home_File ${JAVA_DIR}
    elif [ "${Jdk8Select}" = "2" ]; then
        cd ${cur_dir}/src
        mkdir -p ${JAVA_HOME}/jre/lib/amd64/dcevm/
        cp libjvm${LIB_JVM_VERSION}.so ${JAVA_HOME}/jre/lib/amd64/dcevm/libjvm.so
    else
        Echo_Red "请选在 (1 或者 2)"
        exit 1
    fi
}

Install_Jdk_11()
{
    Echo_Blue "[+] 开始安装 ${Jdk_Ver}..."
    cd ${cur_dir}/src
    Tar_File ${Jdk_11_File_Name} ${Jdk_11_Dir}
    JAVA_DIR=${Jdk_Install_Dir}/${Jdk_11_Dir}
    rm -rf ${JAVA_DIR}
    mkdir -p ${JAVA_DIR}
    mv ${Jdk_11_Dir}/* ${JAVA_DIR}
    Set_Java_Home_File ${JAVA_DIR}
}

Install_Jdk_17()
{
    Echo_Blue "[+] 开始安装 ${Jdk_Ver}..."
    cd ${cur_dir}/src
    Tar_File ${Jdk_17_File_Name} ${Jdk_17_Dir}
    JAVA_DIR=${Jdk_Install_Dir}/${Jdk_17_Dir}
    rm -rf ${JAVA_DIR}
    mkdir -p ${JAVA_DIR}
    mv ${Jdk_17_Dir}/* ${JAVA_DIR}
    Set_Java_Home_File ${JAVA_DIR}
}

Install_Jdk_21()
{
    Echo_Blue "[+] 开始安装 ${Jdk_Ver}..."
    cd ${cur_dir}/src
    Tar_File ${Jdk_21_File_Name} ${Jdk_21_Dir}
    JAVA_DIR=${Jdk_Install_Dir}/${Jdk_21_Dir}
    rm -rf ${JAVA_DIR}
    mkdir -p ${JAVA_DIR}
    mv ${Jdk_21_Dir}/* ${JAVA_DIR}
    Set_Java_Home_File ${JAVA_DIR}
}

Get_Java_Major_Ver()
{
    if [[ -n "$JAVA_HOME" ]]; then
        MAJOR_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F. '{if ($1 == "1") print $2; else print $1}')
    else
        Echo_Red "没有找到JAVA_HOME信息"
        exit 1
    fi
}

Get_Java_Small_Ver()
{
    if [[ -n "$JAVA_HOME" ]]; then
        SMALL_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F_ '{print $2}')
    else
        Echo_Red "没有找到JAVA_HOME信息"
        exit 1
    fi
}

Set_Java_Home_File()
{
    NEW_JAVA_HOME=$1
    FILES_TO_CHECK=(
        "$HOME/.bashrc"
        "$HOME/.bash_profile"
        "$HOME/.profile"
        "/etc/profile"
        "/etc/environment"
        "/etc/profile.d/java.sh"
        "/etc/profile.d/*java*.sh"
    )
    found=0
    for file in "${FILES_TO_CHECK[@]}"; do
        if [ -f "$file" ]; then
            if grep -q '^export JAVA_HOME=' "$file"; then
                echo "含有 java_home 信息的文件: $file"
                sed -i "s|^export JAVA_HOME=.*|export JAVA_HOME=${NEW_JAVA_HOME}|" "$file"
                Echo_Blue "new JAVA_HOME: $NEW_JAVA_HOME"
                echo "备份文件: ${file}.bak"
                found=1
                source "$file"
                Echo_Green "source $file 完成，如果你想让前窗口也立即生效, 请运行 'source $file'"
            fi
        fi
    done

    if [ $found -eq 0 ]; then
        echo "没有找到 JAVA_HOME 配置信息, 将 JAVA_HOME 添加到 '/etc/profile'"
        echo "export JAVA_HOME=${NEW_JAVA_HOME}" >> /etc/profile
        echo 'export PATH=$JAVA_HOME/bin:$PATH' >> /etc/profile
        source "$file"
        Echo_Green "source $file 完成，如果你想让前窗口也立即生效, 请运行 'source $file'"
    fi
}