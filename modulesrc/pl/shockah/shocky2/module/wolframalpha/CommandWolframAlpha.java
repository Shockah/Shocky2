package pl.shockah.shocky2.module.wolframalpha;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import pl.shockah.shocky2.BotUtils;
import pl.shockah.shocky2.CommandCallback;
import pl.shockah.shocky2.CommandWithAliases;
import pl.shockah.shocky2.ETarget;
import pl.shockah.shocky2.Module;
import pl.shockah.shocky2.Util;

public class CommandWolframAlpha extends CommandWithAliases {
	public CommandWolframAlpha(Module module) {
		super(module);
	}
	
	public String command() {return "wolframalpha";}
	public String help() {
		return ".wolframalpha {query} - returns WolframAlpha result";
	}
	public void onAdd() {
		addAlias("wa");
		super.onAdd();
	}
	public void call(PircBotX bot, ETarget target, CommandCallback callback, Channel channel, User sender, String message) {
		String[] split = message.split("\\s");
		
		if (split.length == 0) {
			if (callback.target != ETarget.Console) callback.target = ETarget.Notice;
			callback.append(help());
			return;
		}
		
		if (callback.target == ETarget.Private) callback.target = ETarget.Notice;
		
		String result = pl.shockah.shocky2.module.wolframalpha.Module.getResult(channel,Util.implode(split,1," "));
		if (result == null) {
			callback.append("No results.");
			return;
		}
		
		result = BotUtils.mungeAllNicks(channel,result,sender.getNick());
		callback.append(result);
	}
}