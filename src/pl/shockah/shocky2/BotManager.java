package pl.shockah.shocky2;

import java.util.ArrayList;
import org.pircbotx.PircBotX;

public class BotManager {
	protected ArrayList<PircBotX> bots = new ArrayList<PircBotX>();
	
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
}