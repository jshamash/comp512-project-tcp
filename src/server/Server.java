package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	/**
	 * @param args
	 *            The port to listen on.
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage: java Server [port]");
			System.exit(1);
		}
		
		int port = Integer.parseInt(args[0]);
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		try {
			serverSocket = new ServerSocket(port);			
		} catch (IOException e) {
			System.err.println("Could not listen on port " + port);
			System.exit(1);
		}
		
		try {
			while(true) {
				System.out.println("Waiting for bind...");
				clientSocket = serverSocket.accept();
				System.out.println("Got a bind!");
				// Delegate this task to a worker.
				(new ServerWorker(clientSocket)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
