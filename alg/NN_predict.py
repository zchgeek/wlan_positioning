#!/usr/bin/env python
from sklearn import linear_model
from sklearn.neighbors import KNeighborsClassifier
import matplotlib.pyplot as plt
import pickle

F_SIZE = 0
#data init
for line in open('param.txt'):
    attribute,value = line.strip().split('\t')
    if attribute == 'feature_size':
        F_SIZE = int(value)

fin = open('model.dat')
neigh = pickle.load(fin)
fin.close

#predict
def predict(entry):
    data = [-100]*F_SIZE
    for item in entry.split(' '):
        index,level = item.split(':')
        data[int(index)] = float(level)
    cls = neigh.predict([data])
    return 'pt'+str(cls[0])
