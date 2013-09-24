import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerConnectionThread implements Runnable {

	private String message;
	private PrintWriter clientOutput;
	private String serverHostname;
	private int serverPort;

	/**
	 * Create a thread for connecting to a server.
	 * 
	 * @param clientSocket
	 *            The client to return this info to.
	 * @param serverHostname
	 *            The hostname of the server to connect to.
	 * @param serverPort
	 *            The port that the server is listening on.
	 */
	public ServerConnectionThread(String message, PrintWriter clientOutput,
			String serverHostname, int serverPort) {
		this.message = message;
		this.clientOutput = clientOutput;
		this.serverHostname = serverHostname;
		this.serverPort = serverPort;
	}

	@Override
	public void run() {
		// Start a connection with the server
		Socket socket;
		PrintWriter out;
		BufferedReader in;
		try {
			socket = new Socket(serverHostname, serverPort);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));

			// Forward request to server and await reply
			out.println(message);
			String reply = in.readLine();

			// We got a reply, so close everything and forward reply to client.
			socket.close();
			out.close();
			in.close();
			clientOutput.println(reply);

		} catch (UnknownHostException e) {
			System.err.println("Couldn't connect to server at: "
					+ serverHostname);
		} catch (IOException e) {
			System.err.println("IO error with server at: " + serverHostname);
			e.printStackTrace();
		}
		
		return;
	}

}
