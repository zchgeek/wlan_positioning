#!/usr/bin/env python
from sklearn.neighbors import KNeighborsClassifier
import pickle
from dbhelper import DB

db = DB('train.db')
n_feature = int(db.queryone('value','manifest','key="n_feature"')[0])

fin = open('model.dat')
neigh = pickle.load(fin)
fin.close

def predict(dic):
    data = [-100]*n_feature
    for m_id, rss in dic.items():
        data[int(m_id)] = float(rss)
    cls = neigh.predict([data])
    return cls[0]
