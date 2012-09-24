package pl.shockah.shocky2.module.botcontrol;

import java.util.ArrayList;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import pl.shockah.shocky2.Command;
import pl.shockah.shocky2.CommandCallback;
import pl.shockah.shocky2.ETarget;
import pl.shockah.shocky2.Shocky;
import pl.shockah.shocky2.module.login.LoginData;

public class CommandPart extends Command {
	public String command() {return "part";}
	public String help() {
		return
				".part - parts current channel\n"+
				".part <channel> - parts <channel>\n"+
				".part all - parts all channels";
	}
	public void call(PircBotX bot, ETarget target, CommandCallback callback, Channel channel, User sender, String message) {
		String[] split = message.split("\\s");
		if (split.length == 1) {
			if (sender != null) {
				LoginData ld = LoginData.getLoginData(sender);
				if (!ld.isOp(channel.getName())) {
					if (callback.type != ETarget.Console) callback.type = ETarget.Notice;
					callback.append("Restricted command.");
					return;
				}
				
				Shocky.botManager.partChannel(channel.getName());
				return;
			}
		} else if (split.length == 2) {
			if (split[1].equalsIgnoreCase("all")) {
				if (sender != null && !LoginData.getLoginData(sender).isController()) {
					if (callback.type != ETarget.Console) callback.type = ETarget.Notice;
					callback.append("Restricted command.");
					return;
				}
				
				ArrayList<String> channels = Shocky.botManager.getChannels();
				for (String c : channels) Shocky.botManager.partChannel(c);
			} else {
				if (Shocky.botManager.getChannelWithName(split[1]) == null) {
					if (callback.type != ETarget.Console) callback.type = ETarget.Notice;
					callback.append("I'm not in that channel.");
					return;
				}
				
				if (sender != null && !LoginData.getLoginData(sender).isOp(split[1])) {
					if (callback.type != ETarget.Console) callback.type = ETarget.Notice;
					callback.append("Restricted command.");
					return;
				}
				Shocky.botManager.partChannel(split[1]);
			}
		}
		
		if (callback.type != ETarget.Console) callback.type = ETarget.Notice;
		callback.append(help());
	}
}