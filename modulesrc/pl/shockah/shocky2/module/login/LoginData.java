package pl.shockah.shocky2.module.login;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.pircbotx.Channel;
import org.pircbotx.User;
import pl.shockah.shocky2.Shocky;
import pl.shockah.shocky2.Util;

public final class LoginData {
	protected static List<LoginData> list = Util.syncedList(LoginData.class);
	
	public static LoginData getLoginData(User user) {
		for (int i = 0; i < list.size(); i++) {
			LoginData ld = list.get(i);
			if (ld.nick.equals(user.getNick()) && ld.host.equals(user.getHostmask())) return ld;
		}
		return null;
	}
	
	protected String nick;
	protected final String host;
	protected final List<String> privileges = Util.syncedList(String.class);
	
	public LoginData(User user) {
		nick = user.getNick();
		host = user.getHostmask();
	}
	
	public boolean sameUser(User user) {
		return user.getNick().equals(nick) && user.getHostmask().equals(host);
	}
	
	public boolean isController() {
		return privileges.contains("+c");
	}
	public boolean isVoiced(String channel) {
		if (privileges.contains("+v"+channel)) return true;
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
		if (privileges.contains("+o"+channel)) return true;
		Channel c = Shocky.botManager.getChannelWithName(channel);
		
		Set<User> ops = c.getOps();
		Iterator<User> iterator = ops.iterator();
		while (iterator.hasNext()) {
			User u = iterator.next();
			if (sameUser(u)) return c.isOp(u);
		}
		return false;
	}
}