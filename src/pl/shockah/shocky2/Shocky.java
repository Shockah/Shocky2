package pl.shockah.shocky2;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

public class Shocky extends ShockyListenerAdapter {
	public static BotManager botManager;
	
	public static void main(String[] args) {
		botManager = new BotManager();
		Data.fillDefault();
		botManager.listenerManager.addListener(new Shocky());
		for (Module module : Module.loadNewModules()) System.out.println("Loaded module: "+module.name());
		
		new ThreadConsoleInput().start();
		botManager.joinChannel("#shocky");
	}
	
	public static void handle(Throwable t) {
		t.printStackTrace();
	}
	
	public void onMessage(MessageEvent<PircBotX> event) {
		//if (Data.isBlacklisted(event.getUser())) return;
		if (event.getMessage().length() <= 1) return;
		if (!Data.getString(event.getChannel().getName(),"bot->commandchars").contains(""+event.getMessage().charAt(0))) return;
		
		CommandCallback callback = new CommandCallback();
		callback.targetUser = event.getUser();
		callback.targetChannel = event.getChannel();
		Command cmd = Command.getCommand(event.getMessage().substring(1).split("\\s")[0]);
		if (cmd != null) cmd.call(event.getBot(),ETarget.Channel,callback,event.getChannel(),event.getUser(),event.getMessage());
		if (callback.length() > 0) {
			if (callback.type == ETarget.Channel) {
				callback.insert(0,": ");
				callback.insert(0,event.getUser().getNick());
			}
			//send(event.getBot(),callback.type == ETarget.Notice ? ETarget.Notice : ETarget.Channel,callback.targetChannel,callback.targetUser,callback.toString());
		}
	}
}