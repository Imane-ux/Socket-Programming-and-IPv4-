# Lab3 - CEG 3185

### Teams:

Yasmeen Aburamadan: 300279906 

Imane Amine: 300251048  

Adam Alhaje: 300130500 

### Description:

In order to run this code, you need to:
   
    1. Start the server program "PacketReceiver.java". If you're using Visual Studio Code (VSCode):
                        Right-click on PacketReceiver.java.
                        Select "Run Java" from the context menu.
        Alternatively, open another terminal or command prompt:
                        Navigate to the folder containing PacketReceiver.java.
                        Compile the client program using: javac PacketReceiver.java
                        Run the client program using: java PacketReceiver
    
    2. Now you can run the run the client program file, i.e "PacketSender.java". Same process, if you're using Visual Studio Code (VSCode):
                        Right-click on PacketSender.java.
                        Select "Run Java" from the context menu.
        Alternatively, open another terminal or command prompt:
                        Navigate to the folder containing PacketSender.java.
                        Compile the client program using: javac PacketSender.java
                        Run the client program using: java PacketSender

At the client side (PacketSender terminal) you are prompted to enter a string. After entering a string using your keybaord (e.g. COLOMBIA 2 - MESSI 0), the packet sender encodes the string by converting it to HEX, encapsulates the data into an IP datagram (packet), calculates the checksum, and then sends it to the server program. The server program ﻿acknowledges that the encoded stream has been received and ﻿﻿decodes the stream by verifieng the checksum, then prints it on the terminal. 


