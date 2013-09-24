package messages.requests;

import messages.Command;
import messages.RequestMessage;

public class AddFlight extends RequestMessage {

	int id;
	int flightNum;
	int flightSeats;
	int flightPrice;

	public AddFlight(int id, int flightNum, int flightSeats, int flightPrice) {
		super(Command.ADD_FLIGHT, new Object[] {id, flightNum, flightSeats, flightPrice});
		this.id = id;
		this.flightNum = flightNum;
		this.flightSeats = flightSeats;
		this.flightPrice = flightPrice;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the flightNum
	 */
	public int getFlightNum() {
		return flightNum;
	}

	/**
	 * @return the flightSeats
	 */
	public int getFlightSeats() {
		return flightSeats;
	}

	/**
	 * @return the flightPrice
	 */
	public int getFlightPrice() {
		return flightPrice;
	}

}
