package pl.shockah.shocky2.module.login;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import pl.shockah.shocky2.Command;
import pl.shockah.shocky2.CommandCallback;
import pl.shockah.shocky2.ETarget;
import pl.shockah.shocky2.Module;
import pl.shockah.shocky2.smodule.login.LoginData;

public class CommandLogout extends Command {
	public CommandLogout(Module module) {
		super(module);
	}
	
	public String command() {return "logout";}
	public String help() {
		return ".logout - logs out";
	}
	public void call(PircBotX bot, ETarget target, CommandCallback callback, Channel channel, User sender, String message) {
		String[] split = message.split("\\s");
		if (callback.target != ETarget.Console) callback.target = ETarget.Notice;
		
		if (split.length == 1) {
			LoginData ld = sender == null ? null : LoginData.getLoginData(sender.getNick());
			if (ld != null) {
				callback.append(ld.isLoggedIn() ? "Logged out." : "Already logged out.");
				if (ld.isLoggedIn()) LoginData.logout(sender.getNick());
			}
		}
		
		callback.append(help());
	}
}