package pl.shockah.shocky2.module.login;

import java.util.ArrayList;
import java.util.Random;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.PartEvent;
import pl.shockah.shocky2.Data;
import pl.shockah.shocky2.smodule.login.LoginData;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public final class Module extends pl.shockah.shocky2.ModuleWithCommands {
	public String getName() {return "login";}
	public String getInfo() {return "Adds a login system";}
	protected boolean isListener() {return true;}
	public boolean canDisable() {return false;}
	
	public void onEnable() {
		addCommands(new CommandLogin(this),new CommandRegister(this),new CommandPrivileges(this));
		
		Random rnd = new Random();
		String chars = "0123456789abcdef";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 32; i++) sb.append(chars.charAt(rnd.nextInt(chars.length())));
		
		DBCollection c = Data.getDB().getCollection("config");
		Object[] o = new Object[]{
				"md5extra",sb.toString()
		};
		for (int i = 0; i < o.length; i += 2) {
			BasicDBObject doc = Data.document("key",getName()+"->"+o[i]);
			if (c.findOne(doc) == null) {
				doc.put("value",o[i+1] instanceof Object[] ? Data.document((Object[])o[i+1]) : o[i+1]);
				c.insert(doc);
			}
		}
	}
	
	public void onPart(PartEvent<PircBotX> ev) {
		ArrayList<Channel> channels = new ArrayList<Channel>(ev.getUser().getChannels());
		if (channels.isEmpty() || (channels.size() == 1 && channels.get(0).getName().equals(ev.getChannel()))) LoginData.logout(ev.getUser().getNick());
	}
	public void onNickChange(NickChangeEvent<PircBotX> ev) {
		LoginData.relog(ev.getOldNick(),ev.getNewNick());
	}
}