#!/usr/bin/env python
import socket, fcntl, struct

def get_ip(ifname):
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    return socket.inet_ntoa(fcntl.ioctl(
            s.fileno(), 
            0x8915, 
            struct.pack('256s',ifname[:15])
            )[20:24])

