# Simple distributed search engine

# Libraries

akka, akka-http, akka-remote

# To run use binaries:

```
   ./pack/bin/textsearch-server master
   ./pack/bin/textsearch-server worker1
   ./pack/bin/textsearch-server worker2
   ./pack/bin/client
   
   # OR
   
   java -cp "./pack/lib/*" task.textsearch.server.ServerRunner master
   java -cp "./pack/lib/*" task.textsearch.server.ServerRunner worker1
   java -cp "./pack/lib/*" task.textsearch.server.ServerRunner worker2
   java -cp "./pack/lib/*" task.textsearch.client.ClientRunner
```


# Configuration

See application.conf 

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
 
