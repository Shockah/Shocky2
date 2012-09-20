package pl.shockah.shocky2;

import java.util.Collections;
import java.util.List;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

public abstract class Command {
	public static final List<Command> commands = Util.syncedList(Command.class);
	
	public static void addCommands(Command... commands) {
		for (Command cmd : commands) Command.commands.add(cmd);
	}
	public static void removeCommands(Command... commands) {
		for (Command cmd : commands) Command.commands.remove(cmd);
	}
	public static Command getCommand(String name) {
		name = name.toLowerCase();
		Collections.sort(commands,new CommandComparator(true));
		for (int i = 0; i < commands.size(); i++) {
			Command cmd = commands.get(i);
			if (cmd.command().toLowerCase().startsWith(name)) return cmd;
		}
		return null;
	}
	
	protected int priority = 1;
	
	public abstract String command();
	public abstract void call(PircBotX bot, ETarget target, CommandCallback callback, Channel channel, User sender, String message);
	
	protected void onAdd() {}
	protected void onRemove() {}
}