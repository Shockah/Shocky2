package pl.shockah.shocky2.module;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.pircbotx.Channel;
import org.pircbotx.User;
import pl.shockah.FileLine;
import pl.shockah.shocky2.Shocky;
import pl.shockah.shocky2.Util;

public final class LoginData {
	protected static List<LoginData> list = Util.syncedList(LoginData.class);
	protected static Map<String,String> pass = Util.syncedMap(String.class,String.class);
	protected static Map<String,List<String>> privileges = Collections.synchronizedMap(new HashMap<String,List<String>>());
	
	public static LoginData getLoginData(User user) {
		if (user == null) return null;
		for (int i = 0; i < list.size(); i++) {
			LoginData ld = list.get(i);
			if (ld.nick.equalsIgnoreCase(user.getNick()) && ld.host.equals(user.getHostmask())) return ld;
		}
		
		LoginData ld = new LoginData(user);
		list.add(ld);
		privileges.put(user.getNick().toLowerCase(),Util.syncedList(String.class));
		return ld;
	}
	public static void loadLoginData(User user) {
		File dir = new File("data");
		dir.mkdir();
		
		try {
			ArrayList<String> lines = FileLine.read(new File(dir,"login.cfg"));
			for (int i = 0; i < lines.size(); i += 3) {
				List<String> l = Util.syncedList(String.class);
				privileges.put(lines.get(i),l);
				pass.put(lines.get(i),lines.get(i+1));
				l.addAll(Arrays.asList(lines.get(i+2).split(" ")));
			}
		} catch (Exception e) {Shocky.handle(e);}
	}
	
	public static boolean loginExists(String nick) {
		return pass.containsKey(nick.toLowerCase());
	}
	public static boolean login(User user, String password) {
		LoginData ld = getLoginData(user);
		if (!loginExists(ld.nick)) return false;
		if (pass.get(ld.nick).equals(password)) {
			ld.loggedIn = true;
			return true;
		}
		return false;
	}
	public static boolean register(User user, String password) {
		LoginData ld = getLoginData(user);
		if (loginExists(ld.nick)) return false;
		pass.put(ld.nick.toLowerCase(),password);
		ld.loggedIn = true;
		return true;
	}
	
	public static List<String> getPrivileges(String nick) {
		nick = nick.toLowerCase();
		if (!privileges.containsKey(nick)) privileges.put(nick,Util.syncedList(String.class));
		return privileges.get(nick);
	}
	
	protected String nick;
	protected final String host;
	protected boolean loggedIn = false;
	
	public LoginData(User user) {
		nick = user.getNick().toLowerCase();
		host = user.getHostmask();
	}
	
	public boolean sameUser(User user) {
		return user.getNick().equalsIgnoreCase(nick) && user.getHostmask().equals(host);
	}
	
	public boolean isController() {
		return loggedIn && getPrivileges(nick).contains("+c");
	}
	public boolean isVoiced(String channel) {
		channel = channel.toLowerCase();
		if (loggedIn && getPrivileges(nick).contains("+v"+channel)) return true;
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
		if (loggedIn && getPrivileges(nick).contains("+o"+channel)) return true;
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
		return loggedIn;
	}
	
	public List<String> getPrivileges() {
		return getPrivileges(nick);
	}
}