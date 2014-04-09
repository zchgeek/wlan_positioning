#!/usr/bin/env python

import socket
import threading
import Queue
import time
import sys
from dbhelper import DB
import util
import json

if len(sys.argv) != 2:
    print 'maybe you should type like this:'
    print './test.py device'
    exit(0)

device = sys.argv[1]
IP = ""
PORT_LOCAL = 5674
PORT_SERVER = 5672
BUFSIZ = 4094
map_mac = {}
map_pt = {}

def init():
    global map_mac,map_pt,IP
    IP = util.get_ip('eth0')
    db = DB('test.db')
    map_mac = dict([(m_id,mac) for m_id,mac in db.query(['m_id','mac'],'map_mac')])
    map_pt = dict([(p_id,pt) for p_id,pt in db.query(['p_id','pt'],'map_pt')])

#transform the integer index back to mac address
def retransform(entry):
    dic = json.loads(entry)
    dic = dict([(map_mac[int(m_id)],float(rss)) for m_id,rss in dic.items()])
    return json.dumps(dic)

def sender(result_queue):
    db = DB('test.db')
    total,right = 0,0
    for p_id,entry in db.query(['p_id','entry'],'rss_'+device):
        entry = retransform(entry)
        s = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
        s.connect((IP,PORT_SERVER))
        s.send(entry)
        s.close()
        result = result_queue.get()
        real = map_pt[p_id]
        print 'real:'+real+'\t'+'predict:'+result
        total += 1
        if real == result:
            right += 1
    print 'rate\t',float(right)/total
    print 'done'

def receiver(result_queue):
    ss = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    ss.settimeout(5)
    ss.setsockopt(socket.SOL_SOCKET,socket.SO_REUSEADDR,1) #??
    ss.bind((IP,PORT_LOCAL))
    ss.listen(5)
    while True:
        try:
            s,addr = ss.accept()
            data = s.recv(BUFSIZ)
            result_queue.put(data)
            s.close()
        except Exception as e:
            ss.close()
            break

if __name__ == '__main__':
    init()
    result_queue = Queue.Queue(1)
    t_sender = threading.Thread(target=sender,args=(result_queue,))
    t_receiver = threading.Thread(target=receiver,args=(result_queue,))
    t_sender.start()
    t_receiver.start()

    

