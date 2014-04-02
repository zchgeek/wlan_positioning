#!/usr/bin/env python

import socket
import threading
import Queue
import time
import sys

if len(sys.argv) != 2:
    print 'maybe you should type like this:'
    print './test.py device'
    exit(0)

device = sys.argv[1]
IP = ""
PORT_LOCAL = 5674
PORT_SERVER = 5672
BUFSIZ = 4094
dic = {}
test_file = 'test_set_'+device+'.txt'

def init():
    for line in open('../data/param.txt'):
        attribute,value = line.strip().split('\t')
        if attribute == 'ip':
            IP = value
    for line in open('../data/features.txt'):
        mac,index = line.strip().split('\t')
        dic[index] = mac

#transform the integer index back to mac address
def retransform(entry):
    lst = []
    for item in entry.split(' '):
        index,level = item.split(':')
        lst.append(dic[index]+':'+level)
    return ' '.join(lst)

def sender(result_queue):
    total,right = 0,0
    for line in open(test_file):
#        time.sleep(1)
        entry,cls = line.strip().split('\t')
        entry = retransform(entry)
        s = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
        s.connect((IP,PORT_SERVER))
        s.send(entry)
        s.close()
        result = result_queue.get()
        print 'real:'+cls+'\t'+'predict:'+result
        total += 1
        if cls == result:
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

    

