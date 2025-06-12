#!/usr/bin/env bash
export PATH=$PATH:/bin:/sbin:/usr/bin:/usr/sbin:/usr/local/bin:/usr/local/sbin:~/bin
filename="linux-install"
if [ -f "$filename" ]; then
    rm ./dist/${filename}.tar.gz
fi
tar -czf ../dist/${filename}.tar.gz ./${filename}