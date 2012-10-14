package pl.shockah.shocky2.module.botcontrol;

import java.util.ArrayList;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import pl.shockah.shocky2.Command;
import pl.shockah.shocky2.CommandCallback;
import pl.shockah.shocky2.ETarget;
import pl.shockah.shocky2.Shocky;
import pl.shockah.shocky2.smodule.login.LoginData;

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
		LoginData ld = LoginData.getLoginData(sender.getNick());
		
		if (split.length == 1) {
			commandPart(callback,channel.getName(),target,ld);
		} else if (split.length == 2) {
			if (split[1].equalsIgnoreCase("all")) commandPart(callback,null,target,ld);
			else commandPart(callback,split[1],target,ld);
		} else {
			if (callback.target != ETarget.Console) callback.target = ETarget.Notice;
			callback.append(help());
		}
	}
	
	private void commandPart(CommandCallback callback, String channelName, ETarget target, LoginData ld) {
		if (target == ETarget.Console || (ld != null && (ld.isController() || (channelName != null && ld.isOp(channelName))))) {
			if (channelName == null) {
				ArrayList<String> channels = Shocky.botManager.getChannels();
				for (String c : channels) Shocky.botManager.partChannel(c);
				return;
			} else {
				Shocky.botManager.partChannel(channelName);
				return;
			}
		}
		
		if (callback.target != ETarget.Console) callback.target = ETarget.Notice;
		callback.append("Restricted command.");
		return;
	}
}