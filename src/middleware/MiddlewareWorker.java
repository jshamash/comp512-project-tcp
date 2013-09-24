package middleware;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import messages.ReplyMessage;
import messages.RequestMessage;

/**
 * One of these threads gets run per client. We don't need to worry about
 * concurrent requests from client, since client blocks waiting for response.
 * 
 * @author Jake Shamash
 * 
 */
public class MiddlewareWorker extends Thread {

	private Socket clientSocket = null;
	private String host1;
	private int port1;

	public MiddlewareWorker(Socket clientSocket, String host1, int port1) {
		this.clientSocket = clientSocket;
		this.host1 = host1;
		this.port1 = port1;
	}

	public void run() {
		ObjectOutputStream clientOutput;
		ObjectInputStream clientInput;
		try {
			// Prints output at the client
			clientOutput = new ObjectOutputStream(clientSocket.getOutputStream());
			// Reads input from the client
			clientInput = new ObjectInputStream(clientSocket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		// Handle incoming requests from client
		RequestMessage request;
		while (true) {
			try {
				System.out.println("Waiting for client input...");
				request = (RequestMessage) clientInput.readObject();
				if (request == null) {
					System.out.println("Closing connection with a client...");
					clientSocket.close();
					return;
				}
				System.out.println("Got client input: " + request);
				// Here we will look at the input to see what needs to be done
				// with it.

				// Pass this message along...
				// This is done in this thread, since client blocks, so this
				// thread won't be getting any requests anyway.
				ReplyMessage serverReply = queryServer(host1, port1, request);
				System.out.println("Returning " + serverReply);
				clientOutput.writeObject("Server reply: " + serverReply);
			} catch (IOException e) {
				// Couldn't read from client input or couldn't connect to host
				e.printStackTrace();
				return;
			} catch (ClassNotFoundException e) {
				// Couldn't understand one of the request or the reply
				e.printStackTrace();
			}
		}

	}

	/**
	 * Queries a server with the specified query. Blocks while waiting for server response.
	 * @param hostname of the server to query
	 * @param port the server is listening on
	 * @param query to pass to the server
	 * @return Server's reply
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	private ReplyMessage queryServer(String hostname, int port, RequestMessage query)
			throws IOException, ClassNotFoundException {
		Socket socket;
		ObjectOutputStream out;
		ObjectInputStream in;
		socket = new Socket(hostname, port);
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());

		// Forward request to server and await reply
		out.writeObject(query);
		ReplyMessage reply = (ReplyMessage) in.readObject();

		// We got a reply, so close everything and return reply.
		socket.close();
		out.close();
		in.close();
		return reply;
	}
}
