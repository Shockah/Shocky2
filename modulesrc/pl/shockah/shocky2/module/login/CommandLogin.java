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
import pl.shockah.shocky2.module.LoginData;

public class CommandLogin extends Command {
	protected final Module parent;
	
	public CommandLogin(Module parent) {
		this.parent = parent;
	}
	
	public String command() {return "login";}
	public String help() {
		return ".login <password> - tries to login with <password>";
	}
	public void call(PircBotX bot, ETarget target, CommandCallback callback, Channel channel, User sender, String message) {
		String[] split = message.split("\\s");
		if (callback.target != ETarget.Console) callback.target = ETarget.Notice;
		
		if (split.length == 2) {
			LoginData ld = LoginData.getLoginData(sender);
			if (!ld.isLoggedIn()) {
				if (LoginData.loginExists(sender.getNick())) {
					try {
						callback.append(LoginData.login(sender,Security.md5(split[1]+(String)parent.getCollection().findOne(Data.document("key","md5extra")).get("value"))) ? "Logged in" : "Incorrect password");
						return;
					} catch (Exception e) {Shocky.handle(e);}
				}
				
				callback.append("No such login; use .register first");
				return;
			}
			
			callback.append("Already logged in.");
			return;
		}
		
		callback.append(help());
	}
}