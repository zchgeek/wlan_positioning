#!/bin/bash
if [ $# != 2 ]
then
    echo "maybe you shuold type like this:";echo "./offline.sh divice-dataset algorithm"
    exit 1
fi
device=$1;alg=$2
#remove old files
rm -rf data; mkdir data
#copy scripts to target dir
cp -r raw_data data/raw_data
cp scripts/* data/
cp alg/${alg}_train.py data/train.py
cp alg/${alg}_predict.py data/predictor.py
#go to target dir
cd data/
#process data
./prepare.py
#generate training model
./train.py ${device}
mv test.db ../test/test.db
#start server
./server.py

