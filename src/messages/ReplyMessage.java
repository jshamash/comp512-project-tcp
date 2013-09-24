package messages;

public class ReplyMessage implements Message {

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

}
