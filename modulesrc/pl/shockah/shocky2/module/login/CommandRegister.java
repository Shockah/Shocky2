package pl.shockah.shocky2.module.login;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import pl.shockah.Security;
import pl.shockah.shocky2.Command;
import pl.shockah.shocky2.CommandCallback;
import pl.shockah.shocky2.Data;
import pl.shockah.shocky2.ETarget;
import pl.shockah.shocky2.Shocky;

public class CommandRegister extends Command {
	public String command() {return "register";}
	public String help() {
		return ".register <password> - tries to register with <password>";
	}
	public void call(PircBotX bot, ETarget target, CommandCallback callback, Channel channel, User sender, String message) {
		String[] split = message.split("\\s");
		if (callback.target != ETarget.Console) callback.target = ETarget.Notice;
		
		if (split.length == 2) {
			LoginData ld = LoginData.getLoginData(sender);
			if (!ld.isLoggedIn()) {
				if (!LoginData.loginExists(sender.getNick())) {
					try {
						LoginData.register(sender,Security.md5(split[1]+Data.getConfig().getString("login->md5extra")));
						callback.append("Registered, logged in.");
						return;
					} catch (Exception e) {Shocky.handle(e);}
				}
				
				callback.append("Login already exists; use .login instead");
				return;
			}
			
			callback.append("Already logged in.");
			return;
		}
		
		callback.append(help());
	}
} 