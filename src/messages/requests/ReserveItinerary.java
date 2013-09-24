package messages.requests;

import java.util.Vector;

import messages.Command;
import messages.RequestMessage;

public class ReserveItinerary extends RequestMessage {

	int id;
	int customer;
	Vector<Integer> flightNumbers;
	String location;
	boolean car;
	boolean room;

	public ReserveItinerary(int id, int customer,
			Vector<Integer> flightNumbers, String location, boolean car,
			boolean room) {
		super(Command.RESERVE_ITINERARY, new Object[] {id, customer, flightNumbers, location, car, room});
		this.id = id;
		this.customer = customer;
		this.flightNumbers = flightNumbers;
		this.location = location;
		this.car = car;
		this.room = room;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the customer
	 */
	public int getCustomer() {
		return customer;
	}

	/**
	 * @return the flightNumbers
	 */
	public Vector<Integer> getFlightNumbers() {
		return flightNumbers;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @return the car
	 */
	public boolean isCar() {
		return car;
	}

	/**
	 * @return the room
	 */
	public boolean isRoom() {
		return room;
	}

}
