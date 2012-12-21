package pl.shockah.shocky2.module.login;

import java.util.Random;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import pl.shockah.Security;
import pl.shockah.shocky2.Command;
import pl.shockah.shocky2.CommandCallback;
import pl.shockah.shocky2.Data;
import pl.shockah.shocky2.ETarget;
import pl.shockah.shocky2.Module;
import pl.shockah.shocky2.Shocky;
import pl.shockah.shocky2.smodule.login.LoginData;

public class CommandLogin extends Command {
	protected static final String passChars = "qwertyuiopasdfghjklzxcvbnm1234567890!@#$%^&*";
	
	public CommandLogin(Module module) {
		super(module);
	}
	
	public String command() {return "login";}
	public String help() {
		return ".login <password> - tries to login with <password>";
	}
	public void call(PircBotX bot, ETarget target, CommandCallback callback, Channel channel, User sender, String message) {
		String[] split = message.split("\\s");
		if (callback.target != ETarget.Console) callback.target = ETarget.Notice;
		
		if (split.length == 2) {
			LoginData ld = sender == null ? null : LoginData.getLoginData(sender.getNick());
			if (ld != null) {
				if (!ld.isLoggedIn()) {
					if (LoginData.loginExists(sender.getNick())) {
						try {
							boolean status = LoginData.login(sender.getNick(),Security.md5(split[1]+(String)Data.getCollection().findOne(Data.document("key",module.getName()+"->md5extra")).get("value")));
							if (status && channel != null) {
								LoginData.logout(sender.getNick());
	
								String newPass = "";
								Random rnd = new Random();
								for (int i = 0; i < 8; i++) newPass += passChars.charAt(rnd.nextInt(passChars.length()));
								LoginData.setPassword(sender.getNick(),Security.md5(newPass+(String)Data.getCollection().findOne(Data.document("key",module.getName()+"->md5extra")).get("value")));
								
								callback.append("Security risk! Your new password is: "+newPass);
								return;
							}
							callback.append(status ? "Logged in." : "Incorrect password.");
							return;
						} catch (Exception e) {Shocky.handle(e);}
					}
					
					callback.append("No such login; use .register first");
					return;
				}
				
				callback.append("Already logged in.");
				return;
			}
		}
		
		callback.append(help());
	}
}