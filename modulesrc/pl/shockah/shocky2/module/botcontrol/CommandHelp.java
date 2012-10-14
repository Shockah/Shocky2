package pl.shockah.shocky2.module.botcontrol;

import java.util.Collections;
import java.util.List;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import pl.shockah.shocky2.Command;
import pl.shockah.shocky2.CommandCallback;
import pl.shockah.shocky2.ETarget;

public class CommandHelp extends Command {
	public String command() {return "help";}
	public String help() {
		StringBuilder sb = new StringBuilder();
		sb.append(".help - shows this help\n");
		sb.append(".help {command} - shows help about command\n");
		sb.append("List of commands:\n");
		
		List<Command> commands = Command.getCommands();
		Collections.sort(commands);
		for (int i = 0; i < commands.size(); i++) {
			if (i != 0) sb.append(", ");
			sb.append(commands.get(i).command());
		}
		return sb.toString();
	}
	public void call(PircBotX bot, ETarget target, CommandCallback callback, Channel channel, User sender, String message) {
		if (callback.target != ETarget.Console) callback.target = ETarget.Notice;
		
		String[] split = message.split("\\s");
		callback.append((split.length == 1 || split.length > 2 ? this : Command.getCommand(split[1])).help());
	}
}