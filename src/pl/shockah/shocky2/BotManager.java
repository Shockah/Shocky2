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

public class BotManager {
	protected final List<PircBotX> bots = Util.syncedList(PircBotX.class);
	protected final ListenerManager<PircBotX> listenerManager = new ThreadedListenerManager<PircBotX>();
	
	public PircBotX createBot() {
		try {
			PircBotX bot = new PircBotX();
			bot.setVerbose(Data.getBoolean("bot->verbose"));
			bot.setName(Data.getString("bot->name"));
			bot.setLogin(Data.getString("bot->name"));
			bot.setMessageDelay(Data.getInt("bot->messagedelay"));
			bot.setEncoding(Data.getString("bot->encoding"));
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
			if (bot.getChannels().size() < Data.getInt("bot->maxchannels")) {
				bot.joinChannel(channel);
				return bot;
			}
		}
		
		try {
			PircBotX bot = createBot();
			bot.connect(Data.getString("bot->server"),Data.getInt("bot->port"));
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