package pl.shockah.shocky2.module.login;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.pircbotx.User;

public final class LoginData {
	protected static List<LoginData> list = Collections.synchronizedList(new ArrayList<LoginData>());
	
	public static LoginData getLoginData(User user) {
		for (int i = 0; i < list.size(); i++) {
			LoginData ld = list.get(i);
			if (ld.nick.equals(user.getNick()) && ld.host.equals(user.getHostmask())) return ld;
		}
		return null;
	}
	
	protected String nick;
	protected final String host;
	protected Group group;
	
	public LoginData(User user, Group group) {
		nick = user.getNick();
		host = user.getHostmask();
		this.group = group;
	}
}