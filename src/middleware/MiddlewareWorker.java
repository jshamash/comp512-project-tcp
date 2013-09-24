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
	private String flightHostname;
	private int flightPort;
	private String carHostname;
	private int carPort;
	private String roomHostname;
	private int roomPort;



	/**
	 * 
	 * @param clientSocket
	 * @param flightHostname
	 * @param flightPort
	 * @param carHostname
	 * @param carPort
	 * @param roomHostname
	 * @param roomPort
	 */
	public MiddlewareWorker(Socket clientSocket, String flightHostname,
			int flightPort, String carHostname, int carPort,
			String roomHostname, int roomPort) {
		super();
		this.clientSocket = clientSocket;
		this.flightHostname = flightHostname;
		this.flightPort = flightPort;
		this.carHostname = carHostname;
		this.carPort = carPort;
		this.roomHostname = roomHostname;
		this.roomPort = roomPort;
	}

	public void run() {
		ObjectOutputStream clientOutput;
		ObjectInputStream clientInput;
		try {
			// Prints output at the client
			clientOutput = new ObjectOutputStream(
					clientSocket.getOutputStream());
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

				ReplyMessage serverReply = null;

				// Based on the command, decide where to query.
				switch (request.getCommand()) {
				case ADD_CARS:
					serverReply = queryServer(carHostname, carPort, request);
					break;
				case ADD_FLIGHT:
					serverReply = queryServer(flightHostname, flightPort, request);
					break;
				case ADD_ROOMS:
					serverReply = queryServer(roomHostname, roomPort, request);
					break;
				case DELETE_CARS:
					serverReply = queryServer(carHostname, carPort, request);
					break;
				case DELETE_CUSTOMER:
					// TODO
					break;
				case DELETE_FLIGHT:
					serverReply = queryServer(flightHostname, flightPort, request);
					break;
				case DELETE_ROOMS:
					serverReply = queryServer(roomHostname, roomPort, request);
					break;
				case NEW_CUSTOMER:
					// TODO
					break;
				case NEW_CUSTOMER_CID:
					// TODO
					break;
				case QUERY_CARS:
					serverReply = queryServer(carHostname, carPort, request);
					break;
				case QUERY_CARS_PRICE:
					serverReply = queryServer(carHostname, carPort, request);
					break;
				case QUERY_CUSTOMER_INFO:
					// TODO
					break;
				case QUERY_FLIGHT:
					serverReply = queryServer(flightHostname, flightPort, request);
					break;
				case QUERY_FLIGHT_PRICE:
					serverReply = queryServer(flightHostname, flightPort, request);
					break;
				case QUERY_ROOMS:
					serverReply = queryServer(roomHostname, roomPort, request);
					break;
				case QUERY_ROOMS_PRICE:
					serverReply = queryServer(roomHostname, roomPort, request);
					break;
				case RESERVE_CAR:
					// TODO
					break;
				case RESERVE_FLIGHT:
					// TODO
					break;
				case RESERVE_ITINERARY:
					// TODO
					break;
				case RESERVE_ROOM:
					// TODO
					break;
				default:
					System.err.println("Unrecognized command.");
					break;
				}
				System.out.println("Returning " + serverReply);
				clientOutput.writeObject(serverReply);
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
	 * Queries a server with the specified query. Blocks while waiting for
	 * server response.
	 * 
	 * @param hostname
	 *            of the server to query
	 * @param port
	 *            the server is listening on
	 * @param query
	 *            to pass to the server
	 * @return Server's reply
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private ReplyMessage queryServer(String hostname, int port,
			RequestMessage query) throws IOException, ClassNotFoundException {
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
