package pl.shockah.shocky2.module.botcontrol;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import pl.shockah.shocky2.Command;
import pl.shockah.shocky2.CommandCallback;
import pl.shockah.shocky2.ETarget;
import pl.shockah.shocky2.Shocky;
import pl.shockah.shocky2.Util;
import pl.shockah.shocky2.module.login.LoginData;

public class CommandDie extends Command {
	public String command() {return "die";}
	public String help() {
		return ".die [reason] - shutdowns the bot";
	}
	public void call(PircBotX bot, ETarget target, CommandCallback callback, Channel channel, User sender, String message) {
		if (callback.target != ETarget.Console) callback.target = ETarget.Notice;
		
		if (sender == null || LoginData.getLoginData(sender).isController()) {
			String[] split = message.split("\\s");
			Shocky.quitMessage = split.length > 1 ? Util.implode(split,1," ") : "";
			System.exit(0);
			return;
		}
		
		callback.append("Restricted command.");
	}
}