#!/usr/bin/env python
from sklearn.neighbors import KNeighborsClassifier
import pickle
import json
from dbhelper import DB
import sys

device = sys.argv[1]

db = DB('train.db')
n_feature = int(db.queryone('value','manifest','key="n_feature"')[0])

data = []
cls = []
for y,entry in db.query(['p_id','entry'], 'rss_'+device):
    dic = json.loads(entry)
    x = [-100]*n_feature
    for m_id,rss in dic.items():
        x[int(m_id)] = float(rss)
    data.append(x)
    cls.append(y)
neigh = KNeighborsClassifier(n_neighbors=1)
neigh.fit(data,cls)

fout = open('model.dat','w')
pickle.dump(neigh,fout)
fout.close()
