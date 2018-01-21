import serial
import time
import htmlPy
ser = serial.Serial('/dev/ttyACM0', 9600)
temp = ser.readline()
timeNow = time.strftime('%H:%M:%S-%d-%b-%Y')
fob = open(timeNow+'.txt', 'a')
fob.close()