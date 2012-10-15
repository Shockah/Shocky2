package pl.shockah.shocky2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

public abstract class Command implements Comparable<Command> {
	private static final List<Command> commands = Util.syncedList(Command.class);
	
	public static void addCommands(Command... commands) {
		for (Command cmd : commands) {
			Command.commands.add(cmd);
			cmd.onAdd();
		}
	}
	public static void removeCommands(Command... commands) {
		for (Command cmd : commands) {
			Command.commands.remove(cmd);
			cmd.onRemove();
		}
	}
	public static Command getCommand(String name) {
		name = name.toLowerCase();
		Collections.sort(commands,new CommandComparator(true));
		for (int i = 0; i < commands.size(); i++) {
			Command cmd = commands.get(i);
			if (cmd.command().toLowerCase().equals(name)) return cmd;
		}
		for (int i = 0; i < commands.size(); i++) {
			Command cmd = commands.get(i);
			if (cmd.command().toLowerCase().startsWith(name)) return cmd;
		}
		return null;
	}
	public static List<Command> getCommands() {
		List<Command> commands = getAllCommands();
		for (int i = 0; i < commands.size(); i++) if (commands.get(i) instanceof CommandAlias) commands.remove(i--);
		return commands;
	}
	public static List<Command> getAllCommands() {
		return new ArrayList<Command>(commands);
	}
	
	protected int priority = 1;
	
	public abstract String command();
	public abstract String help();
	public abstract void call(PircBotX bot, ETarget target, CommandCallback callback, Channel channel, User sender, String message);
	
	protected void onAdd() {}
	protected void onRemove() {}
	
	public final int compareTo(Command command) {
		return command().compareTo(command.command());
	}
}