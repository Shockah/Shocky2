package pl.shockah.shocky2;

import org.pircbotx.PircBotX;

public abstract class Module {
	public abstract String name();
	public abstract String info();
	protected boolean canDisable() {return true;}
	protected String[] staticClasses() {return new String[0];}
	
	public void onEnable() {}
	public void onDisable() {}
	public void onDie(PircBotX bot) {}
}