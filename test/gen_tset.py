#!/usr/bin/env python

import os
import sys
import random

if len(sys.argv) != 2:
    print 'maybe you should type like this:'
    print './gen_tset.py device'
    exit(0)

device = sys.argv[1]

dic = {}
for line in open('../data/features.txt'):
    mac,index = line.strip().split('\t')
    dic[mac] = index

fout = open('test_set_'+device+'.txt','w')
PATH = '../raw_data/'+device+'/'
for fname in os.listdir(PATH):
    for line in open(PATH+fname):
        if random.random()>0.3:
            continue
        lst = []
        pt,entry = line.strip().split('\t')
        for item in entry.split(' '):
            mac, level = item.split('@')
            if dic.has_key(mac):
                lst.append(dic[mac]+':'+level)
        fout.write(' '.join(lst)+'\t'+pt+'\n')
fout.close()
