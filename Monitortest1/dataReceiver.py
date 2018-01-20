import serial
import time

ser = serial.Serial('/dev/ttyACM1', 9600)
while 1:
    temp = ser.readline()
    print temp
    if (temp):
        fob = open('dat.txt', 'a')
        fob.write(temp)
        fob.close()