#!/usr/bin/env python
from sklearn import tree
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

clf = tree.DecisionTreeClassifier(criterion='entropy')
clf = clf.fit(data,cls)

fout = open('model.dat','w')
pickle.dump(clf,fout)
fout.close()
