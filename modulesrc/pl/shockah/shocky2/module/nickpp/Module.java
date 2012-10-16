package pl.shockah.shocky2.module.nickpp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import pl.shockah.shocky2.BotUtils;
import pl.shockah.shocky2.Data;
import pl.shockah.shocky2.ETarget;
import pl.shockah.shocky2.Shocky;

public final class Module extends pl.shockah.shocky2.Module {
	public String getName() {return "nickpp";}
	public String getInfo() {return "bot++";}
	public boolean isListener() {return true;}
	
	public void onEnable() {
		DBCollection c = Data.getDB().getCollection("config");
		Object[] o = new Object[]{
				"announce",true
		};
		for (int i = 0; i < o.length; i += 2) {
			BasicDBObject doc = Data.document("key",getName()+"->"+o[i]);
			if (c.findOne(doc) == null) {
				doc.put("value",o[i+1] instanceof Object[] ? Data.document((Object[])o[i+1]) : o[i+1]);
				c.insert(doc);
			}
		}
	}
	
	public void onMessage(MessageEvent<PircBotX> event) {
		if (event.getMessage().length() < 5) return;
		
		Pattern p = Pattern.compile("^("+BotUtils.patternNick.pattern()+")((?:(?:\\+\\+)|(?:\\-\\-))|(?:==))$");
		Matcher m = p.matcher(event.getMessage());
		if (m.find()) {
			String name = m.group(1), namel = name.toLowerCase();
			String op = m.group(2);
			
			int change = 0;
			if (op.equals("++")) {
				if (name.equalsIgnoreCase(event.getUser().getNick())) {
					event.getBot().kick(event.getChannel(),event.getUser());
					return;
				}
				change = 1;
			} else if (op.equals("--")) change = -1;
			else if (op.equals("==")) change = 0;
			else return;
			
			DBObject find = getCollection().findOne(Data.document("name",namel));
			int value = find == null ? 0 : (Integer)find.get("value");
			if (change != 0) {
				value += change;
				getCollection().update(Data.document("name",namel),Data.document("name",namel,"value",value),true,false);
			}
			
			Shocky.send(event.getBot(),(Boolean)Data.get(event.getChannel().getName(),getName()+"->announce") ? ETarget.Channel : ETarget.Notice,event.getChannel(),event.getUser(),name+" == "+value);
		}
	}
}