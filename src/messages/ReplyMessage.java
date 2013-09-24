package messages;

import java.io.Serializable;

public class ReplyMessage implements Message, Serializable {

	private static final long serialVersionUID = 8089645795941774146L;
	Command command;
	Object reply;
	
	public ReplyMessage(Command command, Object reply) {
		this.command = command;
		this.reply = reply;
	}
	
	public Command getCommand() {
		return command;
	}
	
	public Object getReturnValue() {
		return reply;
	}

	@Override
	public String toString() {
		return "ReplyMessage [command=" + command + ", reply=" + reply + "]";
	}

}
