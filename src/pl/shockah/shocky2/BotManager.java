package pl.shockah.shocky2;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;

public class BotManager {
	protected final List<PircBotX> bots = Util.syncedList(PircBotX.class);
	
	public PircBotX createBot() {
		try {
			PircBotX bot = new PircBotX();
			bot.setVerbose(Data.getBoolean("bot->verbose"));
			bot.setName(Data.getString("bot->name"));
			bot.setLogin(Data.getString("bot->name"));
			bot.setMessageDelay(Data.getInt("bot->messagedelay"));
			bot.setEncoding(Data.getString("bot->encoding"));
			bot.setAutoNickChange(true);
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
			Set<Channel> set = bot.getChannels();
			Iterator<Channel> iterator = set.iterator();
			while (iterator.hasNext()) {
				Channel c = iterator.next();
				if (c.getName().equalsIgnoreCase(channel)) return c;
			}
		}
		return null;
	}
}