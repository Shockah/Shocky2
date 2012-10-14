package pl.shockah.shocky2.smodule.login;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.pircbotx.Channel;
import org.pircbotx.User;
import pl.shockah.shocky2.Data;
import pl.shockah.shocky2.Shocky;
import pl.shockah.shocky2.Util;
import com.mongodb.DBObject;

public final class LoginData {
	protected static List<String> list = Util.syncedList(String.class);
	
	public static boolean loginExists(String login) {
		login = login.toLowerCase();
		return pl.shockah.shocky2.Module.getCollection("login").findOne(Data.document("login",login)) != null;
	}
	public static boolean login(String login, String password) {
		login = login.toLowerCase();
		
		DBObject find = pl.shockah.shocky2.Module.getCollection("login").findOne(Data.document("login",login));
		if (find == null) return false;
		
		if (((String)find.get("pass")).equals(password)) {
			list.add(login);
			return true;
		}
		return false;
	}
	public static boolean register(String login, String password) {
		login = login.toLowerCase();
		
		if (loginExists(login)) return false;
		pl.shockah.shocky2.Module.getCollection("login").insert(Data.document("login",login,"pass",password,"privileges",""));
		list.add(login);
		return true;
	}
	public static boolean logout(String login) {
		login = login.toLowerCase();
		
		if (list.contains(login)) {
			list.remove(login);
			return true;
		}
		return false;
	}
	
	public static LoginData getLoginData(String login) {
		return new LoginData(login);
	}
	
	protected String login;
	
	public LoginData(String login) {
		this.login = login.toLowerCase();
	}
	
	public String getLogin() {
		return login;
	}
	
	public boolean sameUser(User user) {
		return user.getNick().equalsIgnoreCase(login);
	}
	
	public boolean isController() {
		return getPrivileges().contains("+c");
	}
	public boolean isVoiced(String channel) {
		channel = channel.toLowerCase();
		if (getPrivileges().contains("+v"+channel)) return true;
		Channel c = Shocky.botManager.getChannelWithName(channel);
		
		Set<User> voices = c.getVoices();
		Iterator<User> iterator = voices.iterator();
		while (iterator.hasNext()) {
			User u = iterator.next();
			if (sameUser(u)) return c.hasVoice(u);
		}
		return false;
	}
	public boolean isOp(String channel) {
		channel = channel.toLowerCase();
		if (getPrivileges().contains("+o"+channel)) return true;
		Channel c = Shocky.botManager.getChannelWithName(channel);
		
		Set<User> ops = c.getOps();
		Iterator<User> iterator = ops.iterator();
		while (iterator.hasNext()) {
			User u = iterator.next();
			if (sameUser(u)) return c.isOp(u);
		}
		return false;
	}
	
	public boolean isLoggedIn() {
		return list.contains(login);
	}
	public List<String> getPrivileges() {
		if (!isLoggedIn()) return new ArrayList<String>();
		return Arrays.asList(((String)pl.shockah.shocky2.Module.getCollection("login").findOne(Data.document("login",login)).get("privileges")).split("\\s"));
	}
}