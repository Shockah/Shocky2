package pl.shockah.shocky2.module.youtube;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import pl.shockah.shocky2.BotUtils;
import pl.shockah.shocky2.CommandCallback;
import pl.shockah.shocky2.CommandWithAliases;
import pl.shockah.shocky2.ETarget;
import pl.shockah.shocky2.Module;
import pl.shockah.shocky2.Util;

public class CommandYouTube extends CommandWithAliases {
	public CommandYouTube(Module module) {
		super(module);
	}
	
	public String command() {return "youtube";}
	public String help() {
		return ".youtube {query} - returns YouTube search result";
	}
	public void onAdd() {
		addAlias("yt");
		super.onAdd();
	}
	public void call(PircBotX bot, ETarget target, CommandCallback callback, Channel channel, User sender, String message) {
		String[] split = message.split("\\s");
		
		if (split.length == 1) {
			if (callback.target != ETarget.Console) callback.target = ETarget.Notice;
			callback.append(help());
			return;
		}
		
		if (callback.target == ETarget.Private) callback.target = ETarget.Notice;
		
		String search = pl.shockah.shocky2.module.youtube.Module.getVideoSearch(Util.implode(split,1," "),true,true);
		if (search != null && !search.isEmpty()) {
			search = BotUtils.mungeAllNicks(channel,search,sender.getNick());
			callback.append(search);
		} else callback.append("No results were found.");
	}
}