package kvds.common.model;
 
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Request {

	private Command command;
	private String key;
	private Deque<String> values = new ConcurrentLinkedDeque<String>();

	public Command getCommand() {
		return this.command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Deque<String> getValues() {
		return this.values;
	}

	public void setValues(ConcurrentLinkedDeque<String> values) {
		this.values = values;
	}

	public void addValues(String... value) {
		for (String v : value) {
			values.addLast(v);
		}
	}

}
