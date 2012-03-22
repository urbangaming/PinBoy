Urban Gaming dark underground movement presents:

 _______ _________ _______    ______   _______          
(  ____ )\__   __/(  ____ )  (  ___ \ (  ___  )|\     /|
| (    )|   ) (   | (    )|  | (   ) )| (   ) |( \   / )
| (____)|   | |   | (____)|  | (__/ / | |   | | \ (_) / 
|  _____)   | |   |  _____)  |  __ (  | |   | |  \   /  
| (         | |   | (        | (  \ \ | |   | |   ) (   
| )      ___) (___| )        | )___) )| (___) |   | |   
|/       \_______/|/         |/ \___/ (_______)   \_/   
                                                        v 1.0

Notes, Q&A, FYI, etc...
=======================

JAD stringy!!!!


Accepted messages
-----------------
It is possible to send messages to PinBoy instances listening to the
Urban Gaming server. Each message consists of 
   i)  its 3 letters long unique code
   ii) PinBoy MIDlet unique ID
   Note: ID 0 has a special meaning: It means "multicast". Message addressed to "00"
         (2 digit representation of zero) shall be interpreted by each client.
   Note: Urban Gaming server acts as a dummy router. Each message is routed to all
         clients and it is up to every single Pip Boy instance to decide whether
         to interpret message or not.
   Note: It is necessary to flush buffers and wait a bit between sending messages
         to Pip Boy clients due to "Two messages bundle" issue.
   Optionally:
   iii) Some other argument such as file name or an integer value.

Vibration
   Pip Boy vibrates for a given number of milliseconds.
   Message example:

      VIB592000

   Description:
      "VIB": 3 letters mandatory code
      "59": 2 digits PinBoy MIDlet ID.  e.g. ID 2 is "02"
      "2000": Positive integer. Number of milliseconds the PinBoy should vibrate.

Background music
   Pip Boy plays music. Background music is going to be played 100 times in a loop
   by default.
   Message example:

      BGM59bgm.mp3

   Description:
      "BGM": 3 letters mandatory code
      "59": 2 digits PinBoy MIDlet ID.  e.g. ID 2 is "02"
      "bgm.mp3": A music file stored in: /e:/ugame/  (on Sony Ericsson c702)

Sound effect
   Pip Boy pauses the background music (if there is any playing at the moment)
   and plays a short sound effect. Unlike background music, sound effect is
   played only once-per-message.
   Message example:

      SFX59sfx.mp3

   Description:
      "SFX": 3 letters mandatory code
      "59": 2 digits PinBoy MIDlet ID.  e.g. ID 2 is "02"
      "sfx.mp3": A music file stored in: /e:/ugame/  (on Sony Ericsson c702)

All audio off
   In order to save battery, we may want to switch the background music (and sound effects)
   playing at the moment off.
   Message example:

      OFF59

   Description:
      "OFF": 3 letters mandatory code
      "59": 2 digits PinBoy MIDlet ID.  e.g. ID 2 is "02"

Hibernate PinBoy MIDlet
   If you know the player is not going to need his Pip Boy for let's say, an hour or so,
   you can hibernate it by sending this message. Note: There is no way how to wake the
   Pip Boy up earlier!
   Message example:

      SLP0015

   Description:
      "SLP": 3 letters mandatory code
      "00": 2 digits representation of zero means the message is intended to
            all the Pip Boy instances.
      "15": Positive integer representing number of _minutes_ we want to put
            Pip Boys to sleep.

Retrieve command periodicity
   If we want to use Pip Boy merely as a GPS tracker, you may save some battery
   life with setting a longer command retrieval period. E.g. it might be enough
   for Pip Boy to read commands one a minute or so...
   Note: It is necessary to avoid piling up many commands and sending them in
         a bundle! (Urban Gaming server issue.)
   Message example:

      RTC598

   Description:
      "RTC": 3 letters mandatory code
      "59": 2 digits PinBoy MIDlet ID.  e.g. ID 2 is "02"
      "8": Positive integer representing number of _seconds_ we want Pip Boy to
           wait before trying to read a new command from the server.


Messages being send by Pip Boy
------------------------------
At the moment, there is the only one message type Pip Boy sends to the
Urban Gaming server. It is a GPS location message.

GPS location message
   Message example:

      GPS 59 666.666 666.666

   Description:
      "GPS ": (following space included) is a common message prefix.
      "59 ": Is a Pip Boy MIDlet ID followed by one space.
      "666.666 666.666": Two values of type double follow. They are separated with space.
         Note: "666.666 666.666" is a default value in case the Pip Boy is unable
               to determine its GPS location (e.g. due to weak signal etc.)
      Example of a real-world message:

         GPS 59 49.225863218307495 16.581673622131348

   Note regarding some known issues:
      It might happen that Urban Gaming server receives something like this:

        GPS 59 49.225863218307495 16.581673622131348GPS 59 49.225863218307495 16.581673622131348

      Basically, the aforementioned string consists of two GPS location messages
      erroneously glued together. The issue is caused by J2ME socket TCP/IP send buffer
      implementation. The only solution at the moment might be to migrate to Datagram UDP
      communication. To be decided...


Pip Boy MIDlet configuration
----------------------------

JAD file configuration
----------------------

Deployment
----------

Known issues
------------
