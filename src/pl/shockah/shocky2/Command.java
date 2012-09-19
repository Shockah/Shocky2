package pl.shockah.shocky2;

public abstract class Command {
	public abstract String command();
	protected int priority() {return 0;}
}