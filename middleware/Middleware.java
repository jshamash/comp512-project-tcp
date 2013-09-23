import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Middleware {

	private static ServerSocket serverSocket = null;
	private static Socket clientSocket = null;
//	private static ClientConnectionThread[] clientThreads = null;
//	private static final int MAX_CLIENTS = 10;

	/**
	 * @param args
	 *            The port to listen on for the client to connect to.
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Usage: java Middleware [port]");
			System.exit(1);
		}

		//clientThreads = new ClientConnectionThread[MAX_CLIENTS];
		int port = Integer.parseInt(args[0]);
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
				(new ClientConnectionThread(clientSocket)).run();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
