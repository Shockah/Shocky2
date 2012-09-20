package pl.shockah.shocky2;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

public class CommandAlias extends Command {
	protected final Command command;
	protected final String name;
	
	public CommandAlias(Command command, String name) {
		this.command = command;
		this.name = name;
		priority = 0;
	}
	
	public String command() {return name;}
	public String help() {return command.help();}
	public void call(PircBotX bot, ETarget target, CommandCallback callback, Channel channel, User sender, String message) {
		command.call(bot,target,callback,channel,sender,message);
	}
}