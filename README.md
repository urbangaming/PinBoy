    Urban Gaming dark underground movement presents:
     ________________ _       ______  _______         
    (  ____ )__   __/( (    /|  ___ \(  ___  )\     /|
    | (    )|  ) (   |  \  ( | (   ) ) (   ) | \   / )
    | (____)|  | |   |   \ | | (__/ /| |   | |\ (_) / 
    |  _____)  | |   | (\ \) |  __ ( | |   | | \   /  
    | (        | |   | | \   | (  \ \| |   | |  ) (   
    | )     ___) (___| )  \  | )___) ) (___) |  | |   
    |/      \_______/|/    )_)/ \___/(_______)  \_/   
                                                        v 1.0

Introduction to PinBoy
======================
Coming soon...

Accepted messages
=================
It is possible to send messages to PinBoy instances listening to the UrbanGaming server. Each message consists of the undermentioned elements:

 1. its 3 letters long unique code
 2. PinBoy MIDlet unique ID
    Note: ID 0 has a special meaning: It means "multicast". Message addressed to "00" (2 digit representation of zero) shall be interpreted by each client.
    Note: UrbanGaming server acts as a dummy router. Each message is routed to all clients and it is up to every single PinBoy instance to decide whether to interpret the received message or not.
    Note: It is necessary to flush buffers and wait a bit between sending messages to PinBoy clients due to "Two messages bundle" issue.
   
    Optionally:
 3. Some other argument such as string (file name) or an integer value (sound volume, sleep time, etc.).

Vibration
---------
PinBoy vibrates for a given number of milliseconds.

*Message example:*

    VIB592000
*Description:*

    "VIB":  3 letters mandatory code
    "59":   2 digits PinBoy MIDlet ID.  e.g. ID 2 is "02"
    "2000": Positive integer. Number of milliseconds the PinBoy should vibrate.

Background music
----------------
PinBoy plays music. Background music is going to be played 100 times in a loop by default.

*Message example:*

    BGM59bgm.mp3
*Description:*

    "BGM":     3 letters mandatory code
    "59":      2 digits PinBoy MIDlet ID.  e.g. ID 2 is "02"
    "bgm.mp3": A music file stored in: /e:/ugame/  (path valid on Sony Ericsson c702)

Sound effect
------------
PinBoy pauses the background music (if there is any playing at the moment) and plays a short sound effect. Unlike background music, sound effect is to be played only once-per-message (no default loop).

*Message example:*

    SFX59sfx.mp3
*Description:*

    "SFX":     3 letters mandatory code
    "59":      2 digits PinBoy MIDlet ID.  e.g. ID 2 is "02"
    "sfx.mp3": A music file stored in: /e:/ugame/  (on Sony Ericsson c702)

All audio off
-------------
In order to save battery, we may want to switch the background music (and sound effects) playing at the moment off.

*Message example:*

    OFF59
*Description:*

    "OFF": 3 letters mandatory code
    "59":  2 digits PinBoy MIDlet ID.  e.g. ID 2 is "02"

Hibernate PinBoy MIDlet
-----------------------
If you know the player is not going to need his PinBoy for let's say, an hour or so, you can hibernate it by sending this message. Note: There is no way to wake the PinBoy up earlier! (...well, sometimes, with some GPRS carriers on some cell phones, the PinBoy instance might not wake up at all :-))

*Message example:*

    SLP0015
*Description:*

    "SLP": 3 letters mandatory code
    "00":  2 digits representation of zero means the message
           is intended to all the PinBoy instances.
    "15":  Positive integer representing a number of _minutes_
           we want to put PinBoy instances to sleep.

Retrieve command periodicity
----------------------------
If we want to use PinBoy merely as a GPS tracker, you may save some battery life with setting a longer command retrieval period. E.g. it might be enough for PinBoy to read commands once a minute or so...
Note: It is necessary to avoid piling up many commands and sending them in a bundle! (UrbanGaming server issue.)

*Message example:*

    RTC598
*Description:*

    "RTC": 3 letters mandatory code
    "59":  2 digits PinBoy MIDlet ID.  e.g. ID 2 is "02"
    "8":   Positive integer representing a number of _seconds_
           we want PinBoy to wait before trying to read a new command from the server.

Messages being send by PinBoy
==============================
At the moment, there is the only one message type PinBoy sends to the UrbanGaming server. It is GPS location message.

GPS location message
--------------------

*Message example:*

    GPS 59 666.666 666.666
*Description:*

    "GPS ": (following space included) is a common message prefix.
    "59 ":  Is a PinBoy MIDlet ID followed by one space.
    "666.666 666.666": Two values of type double follow. They are separated with space.
    
Note: _666.666 666.666_ is a default value in case the PinBoy is unable to determine its GPS location (e.g. due to weak signal etc.). It is an equivalent to "no fix" message known from other trackers.

*Example of a real-world message:*

    GPS 59 49.225863218307495 16.581673622131348

PinBoy MIDlet configuration
============================
JAD file configuration
----------------------
TODO
Deployment
----------
TODO
Known issues
============
2 GPS location messages
-----------------------
It might happen that Urban Gaming server receives something like this:

    GPS 59 49.225863218307495 16.581673622131348GPS 59 49.225863218307495 16.581673622131348
Basically, the aforementioned string consists of two GPS location messages erroneously glued together. The issue is caused by J2ME socket TCP/IP send buffer implementation. The only solution at the moment might be to migrate to Datagram UDP communication. To be decided...

Many more known issues :-)
--------------------------
TODO
