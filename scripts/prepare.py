#!/usr/bin/env python
'''
description:
process data at prepare phase
1.merge all raw data files into one single file named alldata.txt
2.map mac address to an integer and substitute the mac addr in alldata.txt with its relative integer
3.split alldata.txt to train set and test set
'''

import os
import random

#merge data, map macaddr to index 
PATH = './raw/'
fout = open('alldata.txt','w')
dic = {}
index = 0
for fname in os.listdir(PATH):
    for line in open(PATH+fname):
        lst = []
        pt,entry = line.strip().split('\t')
        for item in entry.split(' '):
            mac, level = item.split('@')
            if not dic.has_key(mac):
                dic[mac] = str(index)
                index = index+1
            lst.append(dic[mac]+':'+level)
        fout.write(' '.join(lst)+'\t'+pt+'\n')
fout.close()

fout = open('features.txt','w')
for mac,index in dic.items():
    fout.write(mac+'\t'+index+'\n')
fout.close()

fout = open('param.txt','a')
fout.write('feature_size'+'\t'+str(len(dic))+'\n')
fout.close()

#split alldata to train set and test set
fout_train = open('train_set.txt','w')
fout_test = open('test_set.txt','w')
for line in open('alldata.txt'):
    if random.random()<0.3:
        fout_test.write(line)
    else:
        fout_train.write(line)
fout_train.close()
fout_test.close()
