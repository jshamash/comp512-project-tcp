package messages.replies;

import messages.Command;
import messages.ReplyMessage;

public class AddRooms extends ReplyMessage {

	public AddRooms(boolean status) {
		super(Command.ADD_ROOMS, status);
	}

}
