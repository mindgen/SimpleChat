# SimpleChat
Simple Chat with one room

Server uses Java NIO with socket option SO_REUSEPORT. 
Every worker thread accepts new connections, read and write data to client socket.
Load balancing on kernel level with SO_REUSEPORT works only on Linux hosts.

## Modules:
- core - code base for server and client
- chatclient - app for client
- chatserver - app for server
- loadtesting - load testing for chat server
