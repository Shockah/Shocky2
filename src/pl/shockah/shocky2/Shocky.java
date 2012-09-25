package pl.shockah.shocky2;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

public class Shocky extends ShockyListenerAdapter {
	public static BotManager botManager;
	public static String quitMessage = "";
	
	public static void main(String[] args) {
		botManager = new BotManager();
		Data.fillDefault();
		botManager.listenerManager.addListener(new Shocky());
		for (Module module : Module.loadNewModules()) System.out.println("Loaded module: "+module.name());
		
		ThreadConsoleInput tci = new ThreadConsoleInput();
		tci.setDaemon(true);
		tci.start();
		
		botManager.joinChannel("#shocky");
		
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run() {
				for (PircBotX bot : botManager.bots) bot.quitServer(quitMessage);
				for (Module module : Module.getModules()) {
					module.onDataSave();
					module.onDisable();
				}
			}
		});
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
			if (callback.target == ETarget.Channel) {
				callback.insert(0,": ");
				callback.insert(0,event.getUser().getNick());
			}
			send(event.getBot(),callback);
		}
	}
	
	public static void send(PircBotX bot, CommandCallback callback) {
		send(bot,callback.target,callback.targetChannel,callback.targetUser,callback.toString());
	}
	public static void send(PircBotX bot, ETarget target, Channel channel, User user, String message) {
		String[] lines = message.split("\\\n");
		
		for (String line : lines) {
			switch (target) {
				case Console: System.out.println(line); break;
				case Channel: bot.sendMessage(channel,line); break;
				case Private: bot.sendMessage(user,line); break;
				case Notice: bot.sendNotice(user,line); break;
			}
		}
	}
}