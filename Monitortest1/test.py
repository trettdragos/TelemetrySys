import threading
import time

def worker():
    print threading.currentThread().getName(), 'Starting1'
    time.sleep(2)
    print threading.currentThread().getName(), 'Exiting1'

def my_service():
    print threading.currentThread().getName(), 'Starting2'
    time.sleep(3)
    print threading.currentThread().getName(), 'Exiting2'

t = threading.Thread(name='my_service', target=my_service)
w = threading.Thread(name='worker', target=worker)
w.start()
t.start()