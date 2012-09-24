package pl.shockah.shocky2.module.botcontrol;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import pl.shockah.shocky2.Command;
import pl.shockah.shocky2.CommandCallback;
import pl.shockah.shocky2.ETarget;
import pl.shockah.shocky2.Shocky;
import pl.shockah.shocky2.module.login.LoginData;

public class CommandJoin extends Command {
	public String command() {return "join";}
	public String help() {
		return ".join <channel> - joins <channel>";
	}
	public void call(PircBotX bot, ETarget target, CommandCallback callback, Channel channel, User sender, String message) {
		if (sender != null && !LoginData.getLoginData(sender).isController()) {
			if (callback.type != ETarget.Console) callback.type = ETarget.Notice;
			callback.append("Restricted command.");
			return;
		}
		
		String[] split = message.split("\\s");
		if (split.length != 2) {
			if (callback.type != ETarget.Console) callback.type = ETarget.Notice;
			callback.append(help());
		}
		
		Shocky.botManager.joinChannel(split[1]);
	}
}