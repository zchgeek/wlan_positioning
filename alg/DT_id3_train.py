#!/usr/bin/env python
from sklearn import tree
import matplotlib.pyplot as plt
import pickle

F_SIZE = 0
TRAIN_FILE = 'train_set.txt'

#data init
for line in open('param.txt'):
    attribute,value = line.strip().split('\t')
    if attribute == 'feature_size':
        F_SIZE = int(value)

#prepare to train
data = []
cls = []
for line in open(TRAIN_FILE):
    try:
        entry,y = line.strip().split('\t')
    except:
        continue
    x = [0]*F_SIZE
    for item in entry.split(' '):
        index,level = item.split(':')
        x[int(index)] = float(level)
    data.append(x)
    cls.append(y)

#train model
clf = tree.DecisionTreeClassifier(criterion='entropy')
clf = clf.fit(data,cls)

#save model
fout = open('model.dat','w')
pickle.dump(clf,fout)
fout.close()
