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
import pl.shockah.shocky2.smodule.login.LoginData;

public class CommandRegister extends Command {
	protected final Module parent;
	
	public CommandRegister(Module parent) {
		this.parent = parent;
	}
	
	public String command() {return "register";}
	public String help() {
		return ".register <password> - tries to register with <password>";
	}
	public void call(PircBotX bot, ETarget target, CommandCallback callback, Channel channel, User sender, String message) {
		String[] split = message.split("\\s");
		if (callback.target != ETarget.Console) callback.target = ETarget.Notice;
		
		if (split.length == 2) {
			LoginData ld = LoginData.getLoginData(sender.getNick());
			if (!ld.isLoggedIn()) {
				if (!LoginData.loginExists(sender.getNick())) {
					try {
						LoginData.register(sender.getNick(),Security.md5(split[1]+(String)Data.getCollection().findOne(Data.document("key",parent.getName()+"->md5extra")).get("value")));
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