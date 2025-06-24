#!/usr/bin/env bash

Install_Debug_Tools()
{
    Echo_Blue "开始安装Agent..."
    cd ${cur_dir}/src
    rm -rf ${Debug_Tools_Dir}
    mkdir -p ${Debug_Tools_Dir}
    mv ${Debug_Tools_Agent_File_Name} ${Debug_Tools_Dir}
    mv ${Debug_Tools_Boot_File_Name} ${Debug_Tools_Dir}
    mv ${Debug_Tools_Properties_File_Name} ${Debug_Tools_Dir}
}