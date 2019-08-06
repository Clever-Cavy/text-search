# Description 

Simple distributed search engine

# Libraries

akka, akka-http, akka-remote

# Server configuration

See application.conf and pack/docker-compose.yml 

                                                                +------------+
                                                                |            |
               +--------+         +------------+  +-akka-tcp+-->+ Server     |
               |        |         |            |  |             |  (worker1) |
    User +CLI->+ Client +--HTTP-->+  Server    +--+             |            |
               |        |         |   (master) |                +------------+
               +--------+         |            +--+
                                  +------------+  |             +------------+
                                                  |             |            |
                                                  +-akka-tcp+-->| Server     |
                                                                |  (worker2) |
                                                                |            |
                                                                +------------+
 


# To run server use binaries:

```
   # You can use docker-compose 2.0+
   
   cd pack
   docker-compose up
   
   # OR start services with shell scripts manually: 
   
   ./pack/bin/textsearch-server master
   ./pack/bin/textsearch-server worker1
   ./pack/bin/textsearch-server worker2
   
   # OR if you don't trust scripts run:
   
   java -cp "./pack/lib/*" task.textsearch.server.ServerRunner master
   java -cp "./pack/lib/*" task.textsearch.server.ServerRunner worker1
   java -cp "./pack/lib/*" task.textsearch.server.ServerRunner worker2
```
   
Please note, that the following ports must be available: 
9001, 9002, 9003 and 2552, 2553, 2554

You can override this config by using: 

- for master:  -Dmaster.http.server.port=9001 -Dmaster.akka.remote.netty.tcp.port=2552 -Dmaster.workers.0="akka.tcp://textsearch@localhost:2553/user/worker" -Dmaster.workers.1="akka.tcp://textsearch@localhost:2554/user/worker"
- for worker1: -Dworker1.http.server.port=9002 -Dworker1.akka.remote.netty.tcp.port=2553
- for worker2: -Dworker2.http.server.port=9003 -Dworker2.akka.remote.netty.tcp.port=2554
- for client:  -Dclient.http.endpoint="http://localhost:9001"
   
# Run client:
   
```
   ./pack/bin/client
   # OR
   java -cp "./pack/lib/*" task.textsearch.client.ClientRunner
```


# HTTP API

##### GET /storage/search

Example: http://localhost:9001/storage/search?tokens=one,two,three

Response body:

    {
        "documents": [
            "key1",
            "key2"
        ]
    }
    
##### GET /storage/documents/[key]

Example: http://localhost:9001/storage/documents/key1

Response body:

    {
        "key": "key1",
        "value": "document body"
    }

##### POST /storage/documents

Example: http://localhost:9001/storage/documents/key1

    {
        "key": "key1",
        "value": "document body"
    }
    
Response body:
    
    {
        "description":"Document created. Access it by key: key1"
    }


# Notes

Here is package description to help you start with sources:

- *client* - HTTP and CLI client
- *server* - HTTP server
- *api* - HTTP messages and json helper
- *actor* - akka actors and messages
- *storage* - text index

Dependencies graph:

    +--------+   +--------+   +---------+
    |  api   <---+ server +--->  actor  |
    +---^----+   +--------+   +----+----+
        |                          |
    +---+----+                +----v----+
    | client |                | storage |
    +--------+                +---------+
