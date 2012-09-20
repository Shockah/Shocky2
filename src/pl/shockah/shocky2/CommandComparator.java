package pl.shockah.shocky2;

import java.util.Comparator;

public class CommandComparator implements Comparator<Command> {
	protected final boolean checkPriority;
	
	public CommandComparator(boolean checkPriority) {
		this.checkPriority = checkPriority;
	}
	
	public int compare(Command c1, Command c2) {
		if (checkPriority && c1.priority != c2.priority) return new Integer(c2.priority).compareTo(new Integer(c1.priority));
		return c1.command().compareTo(c2.command());
	}
}