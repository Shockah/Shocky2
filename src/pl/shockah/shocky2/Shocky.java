package pl.shockah.shocky2;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

public class Shocky {
	public static BotManager botManager;
	
	public static void main(String[] args) {
		botManager = new BotManager();
		Data.fillDefault();
		Module.loadNewModules();
		
		new ThreadConsoleInput().start();
		botManager.joinChannel("#shocky");
	}
	
	public static void handle(Throwable t) {
		t.printStackTrace();
	}
	
	public void onMessage(MessageEvent<PircBotX> event) {
		//if (Data.isBlacklisted(event.getUser())) return;
		if (event.getMessage().length()<=1) return;
		if (Data.getString(event.getChannel().getName(),"main-cmdchar").contains(event.getMessage().substring(0,1))) return;
		CommandCallback callback = new CommandCallback();
		callback.targetUser = event.getUser();
		callback.targetChannel = event.getChannel();
		Command cmd = Command.getCommand(event.getBot(),event.getUser(),event.getChannel().getName(),ETarget.Channel,callback,event.getMessage().substring(1));
		if (cmd != null) cmd.call(event.getBot(),ETarget.Channel,callback,event.getChannel(),event.getUser(),event.getMessage());
		if (callback.length()>0) {
			if (callback.type == ETarget.Channel) {
				callback.insert(0,": ");
				callback.insert(0,event.getUser().getNick());
			}
			send(event.getBot(),callback.type == ETarget.Notice ? ETarget.Notice : ETarget.Channel,callback.targetChannel,callback.targetUser,callback.toString());
		}
	}
}