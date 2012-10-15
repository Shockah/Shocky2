package pl.shockah.shocky2.module.login;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import com.mongodb.DBObject;
import pl.shockah.shocky2.Command;
import pl.shockah.shocky2.CommandCallback;
import pl.shockah.shocky2.Data;
import pl.shockah.shocky2.ETarget;
import pl.shockah.shocky2.Module;
import pl.shockah.shocky2.Util;
import pl.shockah.shocky2.smodule.login.LoginData;

public class CommandPrivileges extends Command {
	public CommandPrivileges(Module module) {
		super(module);
	}
	
	public String command() {return "privileges";}
	public String help() {
		StringBuilder sb = new StringBuilder();
		sb.append(".privileges <login> - retrieves login's permissions\n");
		sb.append(".privileges <login> <privilege> - sets a privilege on login");
		return sb.toString();
	}
	public void call(PircBotX bot, ETarget target, CommandCallback callback, Channel channel, User sender, String message) {
		String[] split = message.split("\\s");
		if (callback.target != ETarget.Console) callback.target = ETarget.Notice;
		
		if (split.length == 2) {
			DBObject find = module.getCollection().findOne(Data.document("login",split[1].toLowerCase()));
			if (find == null) {
				callback.append("No such login.");
				return;
			} else {
				callback.append("Privileges for "+find.get("login")+": "+find.get("privileges"));
				return;
			}
		} else if (split.length == 3) {
			LoginData ld = sender == null ? null : LoginData.getLoginData(sender.getNick());
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
									LoginData ld2 = LoginData.getLoginData(split[1]);
									if (ld2.getPrivileges().contains("+"+c+channel.getName().toLowerCase()) != set) {
										ArrayList<String> privileges = new ArrayList<String>(ld2.getPrivileges());
										if (set) privileges.add("+"+c+channel.getName().toLowerCase());
										else privileges.remove("+"+c+channel.getName().toLowerCase());
										
										module.getCollection().update(Data.document("login",ld2.getLogin()),Data.document("$set",Data.document("privileges",privileges.toArray(new String[privileges.size()]))));
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
								LoginData ld2 = LoginData.getLoginData(split[1]);
								if (ld2.isController() != set) {
									ArrayList<String> privileges = new ArrayList<String>(ld2.getPrivileges());
									if (set) privileges.add("+c");
									else privileges.remove("+c");
									
									module.getCollection().update(Data.document("login",ld2.getLogin()),Data.document("$set",Data.document("privileges",Util.implode(privileges," "))));
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