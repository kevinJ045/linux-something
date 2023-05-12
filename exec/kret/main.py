import os
import sys
# from pynput import keyboard

sys.argv.pop(0)

ern = os.system(" ".join(sys.argv))

# def on_press(key):
    # try:
        # k = key.char
    # except:
        # k = key.name
    # if k in ['q']:
        # exit()
        # return False
# 
# listener = keyboard.Listener(on_press=on_press)
# listener.start()
# listener.join()

def retry():
    ern = os.system(" ".join(sys.argv))
    if ern != 0:
        retry()

if ern != 0:
    retry()
