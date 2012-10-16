package pl.shockah.shocky2.module.seen;

import java.util.Date;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import pl.shockah.shocky2.Data;

public final class Module extends pl.shockah.shocky2.ModuleWithCommands {
	public String getName() {return "seen";}
	public String getInfo() {return "Allows to check user's last activity";}
	public boolean isListener() {return true;}
	
	public void onEnable() {
		addCommands(new CommandSeen(this));
	}
	
	public void onMessage(MessageEvent<PircBotX> event) {
		String nick = event.getUser().getNick().toLowerCase(), channel = event.getChannel().getName().toLowerCase();
		getCollection().update(Data.document("nick",nick),Data.document("nick",nick,"channel",channel,"stamp",new Date().getTime(),"message",event.getMessage()),true,false);
	}
}