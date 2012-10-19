package pl.shockah.shocky2.module.tell;

import java.util.Date;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import pl.shockah.shocky2.Command;
import pl.shockah.shocky2.CommandCallback;
import pl.shockah.shocky2.ETarget;
import pl.shockah.shocky2.Module;
import pl.shockah.shocky2.Util;

public class CommandTell extends Command {
	public CommandTell(Module module) {
		super(module);
	}
	
	public String command() {return "tell";}
	public String help() {
		return ".tell {user} {message} - relay the message to user";
	}
	public void call(PircBotX bot, ETarget target, CommandCallback callback, Channel channel, User sender, String message) {
		String[] split = message.split("\\s");
		if (callback.target != ETarget.Console) callback.target = ETarget.Notice;
		
		if (split.length < 3) {
			callback.append(help());
			return;
		}
		
		String aNick = split[1].toLowerCase(), aMessage = Util.implode(split,2," ");
		
		if (bot.getNick().equalsIgnoreCase(aNick)) {
			callback.append("I'm here, you know?");
			return;
		}
		
		Tell tell = new Tell(sender.getNick(),aNick,aMessage,new Date());
		pl.shockah.shocky2.module.tell.Module.tells.add(tell);
		module.getCollection().insert(tell.intoDocument());
		callback.append("I'll pass that along.");
	}
}