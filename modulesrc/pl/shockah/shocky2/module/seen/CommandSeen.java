package pl.shockah.shocky2.module.seen;

import java.util.Date;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import com.mongodb.DBObject;
import pl.shockah.shocky2.BotUtils;
import pl.shockah.shocky2.Command;
import pl.shockah.shocky2.CommandCallback;
import pl.shockah.shocky2.Data;
import pl.shockah.shocky2.ETarget;
import pl.shockah.shocky2.Module;

public class CommandSeen extends Command {
	public CommandSeen(Module module) {
		super(module);
	}
	
	public String command() {return "seen";}
	public String help() {
		return ".seen {nick} - tells when the user was last active";
	}
	public void call(PircBotX bot, ETarget target, CommandCallback callback, Channel channel, User sender, String message) {
		String[] split = message.split("\\s");
		
		if (split.length != 2) {
			if (callback.target != ETarget.Console) callback.target = ETarget.Notice;
			callback.append(help());
			return;
		}
		
		String nick = split[1].toLowerCase();
		if (nick.equalsIgnoreCase(bot.getNick())) {
			callback.append("Go get better glasses, 'kay?");
			return;
		}
		if (nick.equalsIgnoreCase(sender.getNick())) {
			callback.append("Schizophrenia, eh?");
			return;
		}
		
		DBObject find = module.getCollection().findOne(Data.document("nick",nick));
		if (find == null) {
			callback.append("I've never seen "+split[1]);
			return;
		}
		
		callback.append(nick+" was last active "+BotUtils.timeAgo(new Date((Long)find.get("stamp")))+" in "+find.get("channel")+" and said: "+find.get("message"));
	}
}