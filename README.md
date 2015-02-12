TcpUdpServerClient
==================

## Introduction

A TCP/UDP Server/Client platform in Java

## Files 

- Functional classes
  - TCPServer.java: the class object of TCP server
  - UDPServer.java: the class object of UDP server
  - TCPClient.java: the class object of TCP client
  - UDPClient.java: the class object of UDP client

- GUI classes
  - Server.java: the applet file of server, for both TCP and UDP;
  - Client.java: the applet file of server, for both TCP and UDP.

## Compile

Just compile Server.java and Client.java for the GUI server and client

## Use

The GUI is quite self-explanatory, the only thing to notice is that for TCP client, sending "@close" command can close the thread for both server and client ends ("@" is a system keyword).
