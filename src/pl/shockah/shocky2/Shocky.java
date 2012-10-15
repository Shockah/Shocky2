package pl.shockah.shocky2;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

public class Shocky extends ShockyListenerAdapter {
	public static BotManager botManager;
	public static String quitMessage = "";
	
	public static void main(String[] args) {
		Data.initMongo();
		botManager = new BotManager();
		for (Module module : Module.loadNewModules()) System.out.println("Loaded module: "+module.getName());
		botManager.listenerManager.addListener(new Shocky());
		
		ThreadConsoleInput tci = new ThreadConsoleInput();
		tci.setDaemon(true);
		tci.start();
		
		botManager.joinChannel("#shocky");
	}
	
	public static void handle(Throwable t) {
		t.printStackTrace();
	}
	
	public void onMessage(MessageEvent<PircBotX> event) {
		if (event.getMessage().length() <= 1) return;
		if (!((String)Data.getCollection().findOne(Data.document("key","bot->commandChars")).get("value")).contains(""+event.getMessage().charAt(0))) return;
		
		CommandCallback callback = new CommandCallback();
		callback.targetUser = event.getUser();
		callback.targetChannel = event.getChannel();
		Command cmd = Command.getCommand(event.getMessage().substring(1).split("\\s")[0],event.getChannel().getName());
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
		if ((Boolean)Data.get(channel == null ? null : channel.getName(),"bot->stripBeep")) message.replace("\7","");
		
		if (target != ETarget.Console) {
			int limit = (Integer)Data.get(channel == null ? null : channel.getName(),"bot->maxMessageLength");
			if (message.length() > limit) message = message.subSequence(0,limit-3)+"...";
		}
		
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
	
	public static void die() {
		die(null);
	}
	public static void die(String reason) {
		for (PircBotX bot : botManager.bots) for (Module module : Module.getModules()) module.onDie(bot);
		for (PircBotX bot : botManager.bots) if (reason == null) bot.quitServer(); else bot.quitServer(reason);
		killMe();
	}
	private static void killMe() {
		while (true) {
			for (PircBotX bot : botManager.bots) if (bot.isConnected()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {e.printStackTrace();}
				continue;
			}
			break;
		}
		
		System.exit(0);
	}
}