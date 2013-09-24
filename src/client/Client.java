package client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

	/**
	 * Makes a connection to middleware server.
	 * 
	 * @param args
	 *            The host to connect to followed by the port.
	 */
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Usage: java Client [mw-host] [mw-port]");
			System.exit(1);
		}

		String host = args[0];
		int port = Integer.parseInt(args[1]);

		Socket socket = null;
		PrintWriter out = null;
		BufferedReader in = null;

		try {
			socket = new Socket(host, port);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Unknown host: " + host);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: "
					+ host);
			System.exit(1);
		}

		// Read from stdin
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		String userInput;

		try {
			System.out.println("Enter something: ");
			while ((userInput = stdIn.readLine()) != null) {
				out.println(userInput);
				String received = in.readLine();
				System.out.println("received: " + received);
				System.out.println("Enter something: ");
			}

			out.close();
			in.close();
			stdIn.close();
			socket.close();
		} catch (IOException e) {
			System.err.println("Encountered io error:");
			e.printStackTrace();
		}

	}
}
