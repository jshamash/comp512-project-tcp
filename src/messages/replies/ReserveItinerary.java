package messages.replies;

import messages.Command;
import messages.ReplyMessage;

public class ReserveItinerary extends ReplyMessage {

	public ReserveItinerary(boolean status) {
		super(Command.RESERVE_ITINERARY, status);
	}

}
