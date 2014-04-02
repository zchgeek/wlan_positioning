#!/usr/bin/env python
from sklearn import svm
import matplotlib.pyplot as plt
import pickle

F_SIZE = 0
#data init
for line in open('param.txt'):
    attribute,value = line.strip().split('\t')
    if attribute == 'feature_size':
        F_SIZE = int(value)

fin = open('model.dat')
clf = pickle.load(fin)
fin.close

#predic
def predict(entry):
    data = [0]*F_SIZE
    for item in entry.split(' '):
        index,level = item.split(':')
        print index
        print level
        data[int(index)] = float(level)
    print data
    cls = clf.predict([data])
    return cls[0]
