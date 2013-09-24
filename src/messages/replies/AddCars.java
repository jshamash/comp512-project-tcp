package messages.replies;

import messages.Command;
import messages.ReplyMessage;

public class AddCars extends ReplyMessage {

	public AddCars(boolean status) {
		super(Command.ADD_CARS, status);
	}

}
