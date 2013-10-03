comp512-project-tcp
===================

A distributed system that uses TCP to communicate between the individual components.

The client makes a connection request to the middleware, and blocks waiting for a reply.
The middleware receives this request and starts a new thread to service that client.
When the client makes a request to the middleware, the middleware forwards that request to the appropriate server.
The server then starts a new thread, services the request, and returns the response to the middleware.
The middleware forwards this response back to the client, who interprets it and outputs to the user.

The middleware is responsible for handling customer data. It stores a list of customers. Any requests to add or delete a customer are done directly by the middleware. Any reservation requests involve an action in the middleware as well as an action on one of the item servers (i.e. flight, car, room). The middleware handles part of this request by itself, and delegates the rest to the appropriate server(s).

Each of the servers, middleware and clients can be run on different machines.
To run:

```
cd src
javac */*.java

# Running a server:
java server.Server <listen-port>

# Starting the middleware:
java middleware.Middleware <server1-hostname> <sever1-port> <server2-hostname> <sever2-port> <server3-hostname> <sever3-port> <listen-port>

# Starting the client:
java client.Client <middleware-hostname> <middleware-port>
```

## Testing locally

To test everything locally, run

```
cd comp512-project
./testscript
```

This will launch three servers, a middleware, and two clients, all running on localhost.
