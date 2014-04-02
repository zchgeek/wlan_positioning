#!/bin/bash

alg=`grep alg data/param.txt | sed "s/alg\t//"`
#copy files
cp alg/${alg}_predict.py data/predictor.py
cp scripts/server.py data/
#change dir
cd data
ip=`ifconfig wlan0 | grep "inet" | sed "s/[^.0-9]/\n/g" | grep -m 1 [0-9]`
sed -i '/ip/d' param.txt
if [ "$ip" = "" ];then
    sed -i '$ a ip\t127.0.0.1' param.txt
else
    sed -i "$ a ip\t${ip}" param.txt
fi
./server.py