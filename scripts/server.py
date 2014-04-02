#!/usr/bin/env python

import threading
import Queue
import socket
import sys
#import pickle
import predictor
#import hetero_calibrate as hetero

PORT_LOCAL = 5672
PORT_CLIENT = 5674
BUFSIZ = 4096
IP = ""
dic = {}
#hetero_param = {}
def init():
    for line in open('param.txt'):
        attribute,value = line.strip().split('\t')
        if attribute == 'ip':
            IP = value
    for line in open('features.txt'):
        mac,index = line.strip().split('\t')
        dic[mac] = index
#    fin = open('fit.dat')
#    hetero_param = pickle.load(fin)
#    fin.close()

def transform(entry):
    lst = []
    for item in entry.strip().split(' '):
        mac,level = item.rsplit(':',1)
        if dic.has_key(mac):
            lst.append(dic[mac]+':'+level)
    return ' '.join(lst)

def receiver(r_queue):
    ss = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    ss.setsockopt(socket.SOL_SOCKET,socket.SO_REUSEADDR,1)
    ss.bind((IP,PORT_LOCAL))
    ss.listen(5)
    while True:
        s,addr = ss.accept()
        ip = addr[0]
        entry = s.recv(BUFSIZ)
        entry = transform(entry)
        data = ip+'\t'+entry
        print data
        if r_queue.full():
            print 'error: r_queue is full'
            sys.exit()
        r_queue.put(data)
        s.close()

def sender(s_queue):
    while True:
        data = s_queue.get()
        ip,result = data.split('\t')
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.connect((ip,PORT_CLIENT))
        s.send(result)
        s.close()

def processor(r_queue, s_queue):
    while True:
        data = r_queue.get()
        print data.split('\t')
        ip,entry = data.split('\t')
        #process data
        #entry = hetero.calibrate(entry,hetero_param)
        result = predictor.predict(entry)
        data = ip+'\t'+result
        print data
        s_queue.put(data)


if __name__ == '__main__':
    init()

    r_queue = Queue.Queue(100)
    s_queue = Queue.Queue(100)

    thread_rcv = threading.Thread(target=receiver,name='rcv',args=(r_queue,))
    thread_snd = threading.Thread(target=sender,name='snd',args=(s_queue,))
    thread_pcs = threading.Thread(target=processor,name='pcs',args=(r_queue,s_queue))

    thread_rcv.start()
    thread_snd.start()
    thread_pcs.start()
    
    print '==service started=='
