#!/usr/bin/env python
#import sqlite3
import os
import json
import random
from dbhelper import DB

#-------------------------------store data----------------------------
map_mac = {}
map_pt = {}

db = DB('raw.db')
# map_mac : ( m_id | mac )
db.new_table('map_mac', ('m_id integer','mac text'))        
# map_pt : ( p_id | pt | x | y )
db.new_table('map_pt', ('p_id integer','pt text','x real','y real'))
# manifest : (key, value)
db.new_table('manifest', ('key text','value text'))

m_id = 0
p_id = 0
for device in os.listdir('raw_data/devices'):
    r_id = 0
    db.insert(('device','rss_'+device), 'manifest')
    #rss_device : ( r_id | p_id | entry )
    db.new_table('rss_'+device, ('r_id integer','p_id integer','entry text'))
    for txt in os.listdir('raw_data/devices/'+device):
        for line in open('raw_data/devices/'+device+'/'+txt):
            r_id += 1
            dic = {}
            pt,entry = line.strip().split('\t')
            for item in entry.split(' '):
                mac,rss = item.split('@')
                if not mac in map_mac:
                    map_mac[mac] = m_id
                    m_id += 1
                dic[map_mac[mac]] = rss
            if not pt in map_pt:
                map_pt[pt] = p_id
                p_id += 1
            entry = json.dumps(dic)
            db.insert((r_id, map_pt[pt], entry), 'rss_'+device)

map_coord = {}
for line in open('raw_data/coord.txt'):
    pt,xy = line.strip().split('\t')
    x,y = xy.split(' ')
    map_coord[pt] = (x,y)

db.insert(('n_feature',len(map_mac)), 'manifest')
db.insertmany([ (m_id,mac) for mac,m_id in map_mac.items()], 'map_mac')
db.insertmany([(p_id,pt)+map_coord[pt] for pt,p_id in map_pt.items()], 'map_pt')
db.commit()
            
#-------------------------------split data----------------------------
db_train = DB('train.db')
db_test = DB('test.db')
for device in db.queryone('value', 'manifest', 'key="device"'):
    db_train.new_table(device, ('r_id integer','p_id integer','entry text'))
    db_test.new_table(device, ('r_id integer','p_id integer','entry text'))
    for record in db.queryall(device):
        if random.random() > 0.3:
            db_train.insert(record, device)
        else:
            db_test.insert(record, device)

db_train.clone_from(db, 'manifest')
db_train.clone_from(db, 'map_mac')
db_train.clone_from(db, 'map_pt')

db_test.clone_from(db, 'manifest')
db_test.clone_from(db, 'map_mac')
db_test.clone_from(db, 'map_pt')

db_train.commit()
db_test.commit()


                
                
                
                
