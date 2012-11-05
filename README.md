TcpUdpServerClient
==================

A TCP/UDP Server/Client platform in Java

1. What are the files ?

// Functional classes
TCPServer.java: the class object of TCP server
UDPServer.java: the class object of UDP server
TCPClient.java: the class object of TCP client
UDPClient.java: the class object of UDP client

// GUI classes
Server.java: the applet file of server, for both TCP and UDP
Client.java: the applet file of server, for both TCP and UDP

2. How to compile ?

Just compile Server.java and Client.java for the GUI server and client

3. How to use ?

The GUI is quite self-explanatory, the only thing to notice is that for TCP client, sending "@close" command can close the thread for both server and client ends

4. Completed functionalities

All of the required and optional objectives in PA 1 are completed.