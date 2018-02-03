import RPi.GPIO as GPIO
import time
import SimpleMFRC522
import sys
import MySQLdb

db = MySQLdb.connect(host="localhost",
                     user="root",
                     passwd="password",
                     db="prezenta")

cur = db.cursor()

reader = SimpleMFRC522.SimpleMFRC522()
ledGreen = 11
ledRed = 13
GPIO.cleanup()
GPIO.setmode(GPIO.BOARD)
GPIO.setup(ledGreen, GPIO.OUT)
GPIO.setwarnings(False)
GPIO.setup(ledRed, GPIO.OUT)
GPIO.output(ledRed, GPIO.HIGH)
GPIO.output(ledGreen, GPIO.LOW)

def destroy():
        GPIO.output(ledGreen, GPIO.LOW)
        GPIO.output(ledRed, GPIO.LOW)
        GPIO.cleanup()
        sys.exit()

def calcMinutes(start, end):
        a, b, c = start.split(":")
        hour = int(a)
        min = int(b)
        a, b, c = end.split(":")
        hour2=int(a)
        min2=int(b)
        minutes = 0
        if(min2<min):
                minutes = min2+min
                hour2=hour2-1
                minutes = minutes + (hour2-hour)*60
        else:
                minutes = min2-min
                minutes = minutes + (hour2-hour)*60
        return minutes

def isIDInDatabase(id):
        cur.execute("SELECT * FROM users WHERE UID = %s", (id))
        minutes = 0
        for line in cur.fetchall():
                minutes = line[3]
        if(cur.rowcount>0):
                timeNow = time.strftime('%H:%M:%S')
                cur.execute("SELECT * FROM prezente WHERE UID = %s ORDER BY ID DESC", (id))
                for row in cur.fetchall() :
                        if(row[2]==row[3]):
                                print "updating entry with exit time..."
                                cursor = db.cursor()
                                cursor.execute("UPDATE prezente SET iesire = %s WHERE ID = %s", (timeNow, row[0]))
                                minutes = minutes+calcMinutes(str(row[2]), str(timeNow))
                                cursor.execute("UPDATE users SET minutes = %s WHERE UID = %s", (minutes, id))
                        else:
                                print "insering new entry with current time..."
                                cursor = db.cursor()
                                cursor.execute("INSERT INTO prezente (UID, intrare, iesire) VALUES (%s, %s, %s)", (id, timeNow, timeNow))
                        db.commit()
                        return True

while 1:
        try:
                id, text = reader.read()
                if(isIDInDatabase(id)):
                        print id
                        GPIO.output(ledRed, GPIO.LOW)
                        GPIO.output(ledGreen, GPIO.HIGH)
                        time.sleep(3)
                        GPIO.output(ledRed, GPIO.HIGH)
                        GPIO.output(ledGreen, GPIO.LOW)
                        time.sleep(2)
                else:
                        GPIO.output(ledRed, GPIO.LOW)
                        time.sleep(0.1)
                        GPIO.output(ledRed, GPIO.HIGH)
                        time.sleep(0.1)
                        GPIO.output(ledRed, GPIO.LOW)
                        time.sleep(0.1)
                        GPIO.output(ledRed, GPIO.HIGH)
                        time.sleep(0.1)
                        GPIO.output(ledRed, GPIO.LOW)
                        time.sleep(0.1)
                        GPIO.output(ledRed, GPIO.HIGH)

        except KeyboardInterrupt:
                destroy()