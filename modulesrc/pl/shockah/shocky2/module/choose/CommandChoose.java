package pl.shockah.shocky2.module.choose;

import java.util.Random;
import java.util.regex.Pattern;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import pl.shockah.shocky2.Command;
import pl.shockah.shocky2.CommandCallback;
import pl.shockah.shocky2.ETarget;
import pl.shockah.shocky2.Module;
import pl.shockah.shocky2.Util;

public class CommandChoose extends Command {
	public CommandChoose(Module module) {
		super(module);
	}
	
	public String command() {return "choose";}
	public String help() {
		return "choose {1} {2} ... {n} - makes a decision (uses either `;`, `,` or ` ` as separator)";
	}
	public void call(PircBotX bot, ETarget target, CommandCallback callback, Channel channel, User sender, String message) {
		String[] split = message.split("\\s");
		if (split.length == 1) {
			if (callback.target != ETarget.Console) callback.target = ETarget.Notice;
			callback.append(help());
			return;
		}
		
		if (callback.target == ETarget.Private) callback.target = ETarget.Notice;
		
		message = Util.implode(split,1," ");
		
		String sep;
		sep = ";"; if (message.contains(sep)) {
			callback.append(choose(message.split(Pattern.quote(sep))));
			return;
		}
		sep = ","; if (message.contains(sep)) {
			callback.append(choose(message.split(Pattern.quote(sep))));
			return;
		}
		sep = " "; if (message.contains(sep)) {
			callback.append(choose(message.split(Pattern.quote(sep))));
			return;
		}
		callback.append(choose(new String[]{message}));
	}
	
	private String choose(String[] choices) {
		if (choices.length == 1) {
			String c = choices[0];
			choices = new String[]{
				"Hmm... Let's see... "+c+"?",
				"Definitely not "+c
			};
		}
		return choices[new Random().nextInt(choices.length)].trim();
	}
}