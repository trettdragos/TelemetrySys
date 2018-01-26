import serial
import time
import os
import sys

ser = serial.Serial('/dev/ttyACM0', 9600)
ser.close()
ser.open()

for i in range(0, 30):
    temp = ser.readline()
timeNow = sys.argv
newFile = "" + str(timeNow)
a,b,c,d,e=newFile.split("'")
newFile = d+".txt"
while 1:
    temp = ser.readline()
    #print temp
    if (temp):
        fob = open(newFile, "a")
        fob.write(temp)
        fob.close()