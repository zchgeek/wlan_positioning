#!/usr/bin/env python
import pickle
from dbhelper import DB

db = DB('train.db')
n_feature = int(db.queryone('value','manifest','key="n_feature"')[0])

fin = open('model.dat')
clf = pickle.load(fin)
fin.close

def predict(dic):
    data = [-100]*n_feature
    for m_id, rss in dic.items():
        data[int(m_id)] = float(rss)
    cls = clf.predict([data])
    return cls[0]
