#!/bin/bash
if [ $# != 2 ]
then
    echo "maybe you shuold type like this:";echo "./offline.sh divice-dataset algorithm"
    exit 1
fi
data=$1;alg=$2
#remove old files
rm -r data
mkdir data
#copy scripts to target dir
cp -r raw_data/${1} data/raw
cp scripts/prepare.py data/
cp alg/${2}_train.py data/
#go to target dir
cd data/
printf "alg\t${2}\n" > param.txt
printf "device\t${1}\n" >> param.txt
#process data
./prepare.py
#generate training model
./${2}_train.py
#rm scripts
cd ..
mv data/test_set.txt test/test_set_${1}.txt
rm data/prepare.py
rm data/${2}_train.py
#rm data/alldata.txt
#rm data/train_set.txt
rm -r data/raw
