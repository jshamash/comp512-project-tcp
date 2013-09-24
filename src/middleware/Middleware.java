package middleware;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Middleware {

	private static ServerSocket serverSocket = null;
	private static Socket clientSocket = null;

	/**
	 * @param args
	 *            The port to listen on for the client to connect to.
	 */
	public static void main(String[] args) {
		if (args.length != 7) {
			System.out.println("Usage: java Middleware [server-1-host] [server-1-port]" +
					"[server-2-host] [server-2-port] [server-3-host] [server-3-port] [listen-port]");
			System.exit(1);
		}
		
		String host1 = args[0];
		int port1 = Integer.parseInt(args[1]);
		String host2 = args[2];
		int port2 = Integer.parseInt(args[3]);
		String host3 = args[4];
		int port3 = Integer.parseInt(args[5]);
		int port = Integer.parseInt(args[6]);
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Wait for incoming client connections
		while (true) {
			try {
				System.out.println("Waiting for a connection....");
				clientSocket = serverSocket.accept();
				// Client connection started
				System.out.println("Got a connection!");
				(new MiddlewareWorker(clientSocket, host1, port1, host2, port2, host3, port3)).start();

			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}

	}
}
