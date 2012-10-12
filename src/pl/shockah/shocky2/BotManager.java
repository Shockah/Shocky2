package pl.shockah.shocky2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.managers.ListenerManager;
import org.pircbotx.hooks.managers.ThreadedListenerManager;
import com.mongodb.DBCollection;

public class BotManager {
	protected final List<PircBotX> bots = Util.syncedList(PircBotX.class);
	protected final ListenerManager<PircBotX> listenerManager = new ThreadedListenerManager<PircBotX>();
	
	public PircBotX createBot() {
		try {
			DBCollection c = Data.getDB().getCollection("config");
			String sub = "bot";
			
			PircBotX bot = new PircBotX();
			bot.setVerbose((Boolean)c.findOne(Data.document("key",sub+"->verbose")).get("value"));
			bot.setName((String)c.findOne(Data.document("key",sub+"->name")).get("value"));
			bot.setLogin((String)c.findOne(Data.document("key",sub+"->login")).get("value"));
			bot.setMessageDelay((Long)c.findOne(Data.document("key",sub+"->messageDelay")).get("value"));
			bot.setEncoding((String)c.findOne(Data.document("key",sub+"->encoding")).get("value"));
			bot.setAutoNickChange(true);
			
			bot.setListenerManager(listenerManager);
			return bot;
		} catch (Exception e) {Shocky.handle(e);}
		return null;
	}
	
	public PircBotX getBotForChannel(String channel) {
		for (int i = 0; i < bots.size(); i++) {
			PircBotX bot = bots.get(i);
			Set<Channel> set = bot.getChannels();
			Iterator<Channel> iterator = set.iterator();
			while (iterator.hasNext()) {
				Channel c = iterator.next();
				if (c.getName().equalsIgnoreCase(channel)) return bot;
			}
		}
		return null;
	}
	public Channel getChannelWithName(String channel) {
		for (int i = 0; i < bots.size(); i++) {
			PircBotX bot = bots.get(i);
			Iterator<Channel> iterator = bot.getChannels().iterator();
			while (iterator.hasNext()) {
				Channel c = iterator.next();
				if (c.getName().equalsIgnoreCase(channel)) return c;
			}
		}
		return null;
	}
	
	public User getUserWithName(String user) {
		for (int i = 0; i < bots.size(); i++) {
			PircBotX bot = bots.get(i);
			Iterator<Channel> iterator = bot.getChannels().iterator();
			while (iterator.hasNext()) {
				Channel c = iterator.next();
				Iterator<User> iterator2 = c.getUsers().iterator();
				while (iterator2.hasNext()) {
					User u = iterator2.next();
					if (u.getNick().equalsIgnoreCase(user)) return u;
				}
			}
		}
		return null;
	}
	
	public ArrayList<String> getChannels() {
		ArrayList<String> ret = new ArrayList<String>();
		for (int i = 0; i < bots.size(); i++) ret.addAll(bots.get(i).getChannelsNames());
		return ret;
	}
	
	public PircBotX joinChannel(String channel) {
		if (getBotForChannel(channel) != null) return null;
		for (int i = 0; i < bots.size(); i++) {
			PircBotX bot = bots.get(i);
			if (bot.getChannels().size() < (Integer)Data.getDB().getCollection("config").findOne(Data.document("key","bot->maxChannels")).get("value")) {
				bot.joinChannel(channel);
				return bot;
			}
		}
		
		try {
			DBCollection c = Data.getDB().getCollection("config");
			
			PircBotX bot = createBot();
			bot.connect((String)c.findOne(Data.document("key","bot->server")).get("value"),(Integer)c.findOne(Data.document("key","bot->port")).get("value"));
			bot.joinChannel(channel);
			bots.add(bot);
			return bot;
		} catch (Exception e) {Shocky.handle(e);}
		
		return null;
	}
	public PircBotX partChannel(String channel) {
		PircBotX bot = getBotForChannel(channel);
		if (bot == null) return null;
		
		bot.partChannel(bot.getChannel(channel));
		return bot;
	}
}