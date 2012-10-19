package pl.shockah.shocky2.module.tell;

import java.util.List;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.ModeEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.TopicEvent;
import org.pircbotx.hooks.events.UserModeEvent;
import com.mongodb.DBCursor;
import pl.shockah.shocky2.BotUtils;
import pl.shockah.shocky2.Command;
import pl.shockah.shocky2.ETarget;
import pl.shockah.shocky2.Shocky;
import pl.shockah.shocky2.Util;

public final class Module extends pl.shockah.shocky2.ModuleWithCommands {
	protected static final List<Tell> tells = Util.syncedList(Tell.class);
	
	public String getName() {return "tell";}
	public String getInfo() {return "Allows sending messages to an user, that he'll receive on activity";}
	public boolean isListener() {return true;}
	
	public void onEnable() {
		addCommands(new CommandTell(this));
		
		tells.clear();
		DBCursor cursor = getCollection().find();
		while (cursor.hasNext()) tells.add(new Tell(cursor.next()));
	}
	
	private void sendTells(PircBotX bot, User user) {
		for (int i = 0; i < tells.size(); i++) {
			if (tells.get(i).to.equalsIgnoreCase(user.getNick())) {
				Tell tell = tells.remove(i--);
				getCollection().remove(tell.intoDocument());
				Shocky.send(bot,ETarget.Notice,null,user,tell.from+" said "+BotUtils.timeAgo(tell.date)+": "+tell.message);
			}
		}
	}
	
	public void onMessage(MessageEvent<PircBotX> event) {
		String[] args = event.getMessage().split("\\s");
		if (args.length > 0 && args[0].length() > 0 && Command.getCommand(args[0].substring(1),event.getChannel().getName()) == null) sendTells(event.getBot(),event.getUser());}
	public void onPrivateMessage(PrivateMessageEvent<PircBotX> event) {
		String[] args = event.getMessage().split("\\s");
		if (args.length > 0 && args[0].length() > 0 && Command.getCommand(args[0],null) == null) sendTells(event.getBot(),event.getUser());
	}
	public void onNotice(NoticeEvent<PircBotX> event) {
		String[] args = event.getMessage().split("\\s");
		if (args.length > 0 && args[0].length() > 0 && Command.getCommand(args[0],null) == null) sendTells(event.getBot(),event.getUser());
	}
	public void onAction(ActionEvent<PircBotX> event) {
		sendTells(event.getBot(),event.getUser());
	}
	public void onTopic(TopicEvent<PircBotX> event) {
		if (event.isChanged()) sendTells(event.getBot(),event.getUser());
	}
	public void onKick(KickEvent<PircBotX> event) {
		sendTells(event.getBot(),event.getSource());
	}
	public void onMode(ModeEvent<PircBotX> event) {
		sendTells(event.getBot(),event.getUser());
	}
	public void onUserMode(UserModeEvent<PircBotX> event) {
		sendTells(event.getBot(),event.getSource());
	}
}