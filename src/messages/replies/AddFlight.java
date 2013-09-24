package messages.replies;

import messages.Command;
import messages.ReplyMessage;

public class AddFlight extends ReplyMessage {

	public AddFlight(boolean status) {
		super (Command.ADD_FLIGHT, status);
	}

}
