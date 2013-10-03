package middleware;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Vector;

import messages.Command;
import messages.ReplyMessage;
import messages.RequestMessage;
import ResImpl.Car;
import ResImpl.Customer;
import ResImpl.CustomerResourceManager;
import ResImpl.Flight;
import ResImpl.Hotel;
import ResImpl.RMHashtable;
import ResImpl.ReservedItem;
import ResImpl.Trace;

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
	private CustomerResourceManager crm;

	/**
	 * 
	 * @param crm
	 *            CustomerResourceManager that stores cust info for this program
	 * @param clientSocket
	 * @param flightHostname
	 * @param flightPort
	 * @param carHostname
	 * @param carPort
	 * @param roomHostname
	 * @param roomPort
	 */
	public MiddlewareWorker(CustomerResourceManager crm, Socket clientSocket,
			String flightHostname, int flightPort, String carHostname,
			int carPort, String roomHostname, int roomPort) {
		super();
		this.crm = crm;
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
				Object reply;

				// Based on the command, decide where to query.
				switch (request.getCommand()) {
				case ADD_CARS:
					serverReply = queryServer(carHostname, carPort, request);
					break;
				case ADD_FLIGHT:
					serverReply = queryServer(flightHostname, flightPort,
							request);
					break;
				case ADD_ROOMS:
					serverReply = queryServer(roomHostname, roomPort, request);
					break;
				case DELETE_CARS:
					serverReply = queryServer(carHostname, carPort, request);
					break;
				case DELETE_CUSTOMER:
					int id = (Integer) request.getParams()[0];
					int customerID = (Integer) request.getParams()[1];
					Trace.info("MW::deleteCustomer(" + id + ", " + customerID
							+ ") called");
					// Get customer object from RM
					// Don't want anyone touching this cust object while we're
					// deleting it!
					synchronized (crm) {
						Customer cust = crm.getCustomer(id, customerID);
						if (cust == null) {
							Trace.warn("MW::deleteCustomer(" + id + ", "
									+ customerID
									+ ") failed--customer doesn't exist");
							reply = false;
						} else {
							// for each reservation, mark them unreserved.
							RMHashtable reservationHT = cust.getReservations();
							for (Enumeration e = reservationHT.keys(); e
									.hasMoreElements();) {
								String reservedkey = (String) (e.nextElement());
								ReservedItem reserveditem = cust
										.getReservedItem(reservedkey);
								int count = reserveditem.getCount();
								Trace.info("MW::deleteCustomer(" + id + ", "
										+ customerID + ") has reserved "
										+ reserveditem.getKey() + " "
										+ reserveditem.getCount() + " times");

								String key = reserveditem.getKey();
								String type = key.split("-")[0];
								switch (type) {
								case "car":
									queryServer(
											carHostname,
											carPort,
											new RequestMessage(
													Command.REMOVE_RESERVATIONS,
													new Object[] { id, key,
															count }));
									break;
								case "room":
									queryServer(
											roomHostname,
											roomPort,
											new RequestMessage(
													Command.REMOVE_RESERVATIONS,
													new Object[] { id, key,
															count }));
									break;
								case "flight":
									queryServer(
											flightHostname,
											flightPort,
											new RequestMessage(
													Command.REMOVE_RESERVATIONS,
													new Object[] { id, key,
															count }));
									break;
								default:
									// Just ask them all to do it -- no effect
									// if it
									// doesn't have the item.
									queryServer(
											carHostname,
											carPort,
											new RequestMessage(
													Command.REMOVE_RESERVATIONS,
													new Object[] { id, key,
															count }));
									queryServer(
											roomHostname,
											roomPort,
											new RequestMessage(
													Command.REMOVE_RESERVATIONS,
													new Object[] { id, key,
															count }));
									queryServer(
											flightHostname,
											flightPort,
											new RequestMessage(
													Command.REMOVE_RESERVATIONS,
													new Object[] { id, key,
															count }));
								}
							}
							reply = crm.deleteCustomer(
									(Integer) request.getParams()[0],
									(Integer) request.getParams()[1]);
						}
					}
					serverReply = new ReplyMessage(Command.DELETE_CUSTOMER,
							reply);
					break;
				case DELETE_FLIGHT:
					serverReply = queryServer(flightHostname, flightPort,
							request);
					break;
				case DELETE_ROOMS:
					serverReply = queryServer(roomHostname, roomPort, request);
					break;
				case NEW_CUSTOMER:
					synchronized (crm) {
						reply = crm
								.newCustomer((Integer) request.getParams()[0]);
						serverReply = new ReplyMessage(Command.NEW_CUSTOMER,
								reply);
					}
					break;
				case NEW_CUSTOMER_CID:
					synchronized (crm) {
						reply = crm.newCustomer(
								(Integer) request.getParams()[0],
								(Integer) request.getParams()[1]);
						serverReply = new ReplyMessage(
								Command.NEW_CUSTOMER_CID, reply);
					}
					break;
				case QUERY_CARS:
					serverReply = queryServer(carHostname, carPort, request);
					break;
				case QUERY_CARS_PRICE:
					serverReply = queryServer(carHostname, carPort, request);
					break;
				case QUERY_CUSTOMER_INFO:
					synchronized (crm) {
						reply = crm.queryCustomerInfo(
								(Integer) request.getParams()[0],
								(Integer) request.getParams()[1]);
						serverReply = new ReplyMessage(
								Command.QUERY_CUSTOMER_INFO, reply);
					}
					break;
				case QUERY_FLIGHT:
					serverReply = queryServer(flightHostname, flightPort,
							request);
					break;
				case QUERY_FLIGHT_PRICE:
					serverReply = queryServer(flightHostname, flightPort,
							request);
					break;
				case QUERY_ROOMS:
					serverReply = queryServer(roomHostname, roomPort, request);
					break;
				case QUERY_ROOMS_PRICE:
					serverReply = queryServer(roomHostname, roomPort, request);
					break;
				case RESERVE_CAR:
					Object[] params = request.getParams();
					serverReply = reserveCar(request);
					break;

				case RESERVE_FLIGHT:
					params = request.getParams();
					serverReply = reserveFlight(request);
					break;

				case RESERVE_ITINERARY:
					/*
					 * NOTE this is not all or nothing, it might book some stuff
					 * and then fail without a rollback
					 */
					serverReply = reserveItinerary(request);
					break;

				case RESERVE_ROOM:
					serverReply = reserveRoom(request);
					break;
				default:
					System.err.println("Unrecognized command.");
					break;
				}
				System.out.println("Returning " + serverReply);
				clientOutput.writeObject(serverReply);
			} catch (IOException e) {
				// Couldn't read from client input or couldn't connect to host
				System.out.println("Disconnected from client");
				try {
					clientOutput.close();
					clientSocket.close();
				} catch (IOException e2) {
					// Who cares??
				}
				return;

			} catch (ClassNotFoundException e) {
				// Couldn't understand one of the request or the reply
				System.out.println("Couldn't understand a message");
			}
		}

	}

	private ReplyMessage reserveRoom(RequestMessage request)
			throws ClassNotFoundException, IOException {
		int id = (Integer) request.getParams()[0];
		int custID = (Integer) request.getParams()[1];
		String location = (String) request.getParams()[2];
		// See if any rooms are available at this location
		int roomsAvail = (Integer) queryServer(
				roomHostname,
				roomPort,
				new RequestMessage(Command.QUERY_ROOMS, new Object[] { id,
						location })).getReturnValue();
		if (roomsAvail > 0) {
			// Room is available, try to book the customer
			int price = (Integer) queryServer(
					roomHostname,
					roomPort,
					new RequestMessage(Command.QUERY_ROOMS_PRICE, new Object[] {
							id, location })).getReturnValue();
			boolean custAvail = false;
			synchronized (crm) {
				custAvail = (crm.reserveCustomer(id, custID,
						Hotel.getKey(location), location, price));
			}
			if (custAvail) {
				// Customer has been reserved, now book the room.
				return queryServer(roomHostname, roomPort, request);
			}
		}
		// Customer unavailable or no cars available
		Trace.warn("Customer unavailable to book room or room is not available");
		return new ReplyMessage(request.getCommand(), false);
	}

	private ReplyMessage reserveFlight(RequestMessage request)
			throws ClassNotFoundException, IOException {
		int id = (Integer) request.getParams()[0];
		int custID = (Integer) request.getParams()[1];
		int flightNum = (Integer) request.getParams()[2];

		// See if any flights are available at this location
		int flightsAvail = (Integer) queryServer(
				flightHostname,
				flightPort,
				new RequestMessage(Command.QUERY_FLIGHT, new Object[] { id,
						flightNum })).getReturnValue();
		if (flightsAvail > 0) {
			// Flight is available, try to book the customer
			int price = (Integer) queryServer(
					flightHostname,
					flightPort,
					new RequestMessage(Command.QUERY_FLIGHT_PRICE,
							new Object[] { id, flightNum })).getReturnValue();
			boolean custAvail = false;
			synchronized (crm) {
				custAvail = (crm.reserveCustomer(id, custID,
						Flight.getKey(flightNum), String.valueOf(flightNum),
						price));
			}
			if (custAvail) {
				// Customer has been reserved, now book the flight.
				return queryServer(flightHostname, flightPort, request);
			}
		}
		// Customer unavailable or no seats available
		Trace.warn("Customer unavailable to book flight or flight is not available");
		return new ReplyMessage(request.getCommand(), false);
	}

	private ReplyMessage reserveCar(RequestMessage request)
			throws ClassNotFoundException, IOException {
		int id = (Integer) request.getParams()[0];
		int custID = (Integer) request.getParams()[1];
		String location = (String) request.getParams()[2];

		// See if any cars are available at this location
		int carsAvail = (Integer) queryServer(
				carHostname,
				carPort,
				new RequestMessage(Command.QUERY_CARS, new Object[] { id,
						location })).getReturnValue();
		if (carsAvail > 0) {
			// Car is available, try to book the customer
			int price = (Integer) queryServer(
					carHostname,
					carPort,
					new RequestMessage(Command.QUERY_CARS_PRICE, new Object[] {
							id, location })).getReturnValue();
			boolean custAvail = false;
			synchronized (crm) {
				custAvail = (crm.reserveCustomer(id, custID,
						Car.getKey(location), location, price));
			}
			if (custAvail) {
				// Customer has been reserved, now book the car.
				return queryServer(carHostname, carPort, request);
			}
		}
		// Customer unavailable or no cars available
		Trace.warn("Customer unavailable to book car or car is not available");
		return new ReplyMessage(request.getCommand(), false);
	}

	private ReplyMessage reserveItinerary(RequestMessage request)
			throws ClassNotFoundException, IOException {
		Object[] params = request.getParams();
		int id = (Integer) params[0];
		int custID = (Integer) params[1];
		Vector flightNumbers = (Vector) params[2];
		String location = (String) params[3];
		boolean car = (Boolean) params[4];
		boolean room = (Boolean) params[5];

		Trace.info("MW::reserveItinerary(" + id + ", " + custID + ", "
				+ flightNumbers + ", " + location + ", " + car + ", " + room
				+ ") was called");

		// Book the flights.
		for (Object flightNumber : flightNumbers) {
			// Ugly cast... not *my* decision to design things this
			// way, lol
			int flightNum = Integer.parseInt((String) flightNumber);
			boolean success = (Boolean) reserveFlight(
					new RequestMessage(Command.RESERVE_FLIGHT, new Object[] {
							id, custID, flightNum })).getReturnValue();
			// Book it
			if (!success) {
				// One of the flights couldn't be booked, abort!
				Trace.warn("MW::reserveItinerary failed, could not reserve flight "
						+ flightNum);
				return new ReplyMessage(Command.RESERVE_ITINERARY, false);
			} else {
				Trace.info("MW::reserveItinerary:: reserved flight "
						+ flightNum);
			}
		}

		if (car) {
			// Try to reserve the car
			boolean success = (Boolean) reserveCar(
					new RequestMessage(Command.RESERVE_CAR, new Object[] { id,
							custID, location })).getReturnValue();
			if (!success) {
				Trace.warn("MW::reserveItinerary failed, could not reserve car at location "
						+ location);
				return new ReplyMessage(Command.RESERVE_ITINERARY, false);
			} else {
				Trace.info("MW::reserveItinerary:: reserved car at location "
						+ location);
			}
		}

		if (room) {
			// Try to reserve the room
			boolean success = (Boolean) reserveRoom(
					new RequestMessage(Command.RESERVE_ROOM, new Object[] { id,
							custID, location })).getReturnValue();
			if (!success) {
				Trace.warn("MW::reserveItinerary failed, could not reserve room at location "
						+ location);
				return new ReplyMessage(Command.RESERVE_ITINERARY, false);
			} else {
				Trace.info("MW::reserveItinerary:: reserved room at location "
						+ location);
			}
		}

		// Everything worked!
		return new ReplyMessage(Command.RESERVE_ITINERARY, true);
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
