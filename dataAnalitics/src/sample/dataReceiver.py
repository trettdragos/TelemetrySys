import serial as serial
import time
import os
import sys

ser = serial.Serial('COM8', 9600)
ser.close()
ser.open()
timeNow = str(time.strftime('%H-%M-%S-%d-%m-%Y'))+".txt"
#timeNow = sys.argv
#newFile = "" + str(timeNow)
#a,b,c,d,e=newFile.split("'")
newFile = os.path.join("db", timeNow)
print "writing in file: "+newFile
while 1:
    temp = ser.readline()
    #print temp
    if (temp):
        time, tempMotor, tempBatery, dump = temp.split(";")
        print "Motor: "+tempMotor+ " - Batery: "+ tempBatery +" time: "+str(float(time)/10)+"s"
        temp = temp[:-1]
        fob = open(newFile, "a")
        fob.write(temp)
        fob.close()