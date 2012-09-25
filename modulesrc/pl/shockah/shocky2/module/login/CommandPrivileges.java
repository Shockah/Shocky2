package pl.shockah.shocky2.module.login;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import pl.shockah.shocky2.Command;
import pl.shockah.shocky2.CommandCallback;
import pl.shockah.shocky2.ETarget;
import pl.shockah.shocky2.Shocky;

public class CommandPrivileges extends Command {
	public String command() {return "privileges";}
	public String help() {
		return ".privileges <login> <privilege> - sets a privilege on login";
	}
	public void call(PircBotX bot, ETarget target, CommandCallback callback, Channel channel, User sender, String message) {
		String[] split = message.split("\\s");
		if (callback.target != ETarget.Console) callback.target = ETarget.Notice;
		
		if (split.length == 3) {
			LoginData ld = LoginData.getLoginData(sender);
			String privilege = split[2];
			
			Pattern pattern = Pattern.compile("([\\+\\-])([a-zA-Z])[a-zA-Z]*");
			Matcher matcher = pattern.matcher(privilege);
			if (matcher.find()) {
				boolean set = matcher.group(1).equals("+");
				char c = matcher.group(2).toLowerCase().charAt(0);
				switch (c) {
					case 'v': case 'o': {
						if (channel != null) {
							if (target == ETarget.Console || ld.isOp(channel.getName())) {
								if (LoginData.loginExists(split[1])) {
									LoginData ld2 = LoginData.getLoginData(Shocky.botManager.getUserWithName(split[1]));
									System.out.println(ld2.getPrivileges().size());
									if (ld2.getPrivileges().contains("+"+c+channel.getName().toLowerCase()) != set) {
										if (set) LoginData.getPrivileges(split[1]).add("+"+c+channel.getName().toLowerCase());
										else LoginData.getPrivileges(split[1]).remove("+"+c+channel.getName().toLowerCase());
										
										callback.append("Done.");
										return;
									}
									callback.append("User already "+(set ? "" : "de")+(c == 'v' ? "voiced" : "opped")+".");
									return;
								}
								callback.append("No such login.");
								return;
							}
							callback.append("Restricted command.");
							return;
						}
						return;
					}
					case 'c': {
						if (target == ETarget.Console || ld.isController()) {
							if (LoginData.loginExists(split[1])) {
								LoginData ld2 = LoginData.getLoginData(Shocky.botManager.getUserWithName(split[1]));
								if (ld2.isController() != set) {
									if (set) LoginData.getPrivileges(split[1]).add("+c");
									else LoginData.getPrivileges(split[1]).remove("+c");
									
									callback.append("Done.");
									return;
								}
								callback.append(set ? "User is already a bot controller." : "User already isn't a bot controller.");
								return;
							}
							callback.append("No such login.");
							return;
						}
						callback.append("Restricted command.");
						return;
					}
				}
			}
		}
		
		callback.append(help());
	}
}