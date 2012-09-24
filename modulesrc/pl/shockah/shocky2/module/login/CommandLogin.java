package pl.shockah.shocky2.module.login;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import pl.shockah.shocky2.Command;
import pl.shockah.shocky2.CommandCallback;
import pl.shockah.shocky2.ETarget;

public class CommandLogin extends Command {
	public String command() {return "login";}
	public String help() {
		return ".login <password> - tries to login with <password>";
	}
	public void call(PircBotX bot, ETarget target, CommandCallback callback, Channel channel, User sender, String message) {
		LoginData ld = LoginData.getLoginData(sender);
		//TODO
	}
}