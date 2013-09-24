import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

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
		PrintWriter clientOutput;
		BufferedReader clientInput;
		try {
			// Prints output at the client
			clientOutput = new PrintWriter(clientSocket.getOutputStream());
			clientOutput.println("gotcha!");
			// Reads input from the client
			clientInput = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		// Handle incoming requests from client
		String inputLine;
		while (true) {
			try {
				clientOutput.println("Well?");
				System.out.println("Waiting for client input...");
				inputLine = clientInput.readLine();
				if (inputLine == null) {
					System.out.println("Closing connection with a client...");
					clientSocket.close();
					return;
				}
				System.out.println("Got client input: " + inputLine);
				// Here we will look at the input to see what needs to be done
				// with it.

				// Pass this message along...
				// This is done in this thread, since client blocks, so this
				// thread won't be getting any requests anyway.
				String serverReply = queryServer(host1, port1, inputLine);
				clientOutput.println("Server reply: " + serverReply);
			} catch (IOException e) {
				// Couldn't read from client input or couldn't connect to host
				e.printStackTrace();
				return;
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
	 */
	private String queryServer(String hostname, int port, String query)
			throws IOException {
		Socket socket;
		PrintWriter out;
		BufferedReader in;
		socket = new Socket(hostname, port);
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		// Forward request to server and await reply
		out.println(query);
		String reply = in.readLine();
		System.out.println("Got a reply from server: " + reply);

		// We got a reply, so close everything and return reply.
		socket.close();
		out.close();
		in.close();
		return reply;
	}
}
