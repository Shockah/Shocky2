package pl.shockah.shocky2.module.quote;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import com.mongodb.BasicDBObject;
import pl.shockah.shocky2.CommandCallback;
import pl.shockah.shocky2.CommandWithAliases;
import pl.shockah.shocky2.Data;
import pl.shockah.shocky2.ETarget;
import pl.shockah.shocky2.Module;
import pl.shockah.shocky2.Util;

public class CommandQuoteAdd extends CommandWithAliases {
	public CommandQuoteAdd(Module module) {
		super(module);
	}
	
	public String command() {return "quoteadd";}
	public String help() {
		return ".quoteadd {tags} {quote} - adds a quote";
	}
	protected void onAdd() {
		addAlias("qadd");
		super.onAdd();
	}
	public void call(PircBotX bot, ETarget target, CommandCallback callback, Channel channel, User sender, String message) {
		String[] split = message.split("\\s");
		
		if (callback.target != ETarget.Console) callback.target = ETarget.Notice;
		if (split.length < 3 || channel == null) {
			callback.append(help());
			return;
		}
		
		BasicDBObject doc = Data.document("channel",channel.getName(),"quote",Util.implode(split,2," "));
		doc.put("tags",split[1].contains(";") ? split[1].split(";") : new String[]{split[1]});
		module.getCollection().insert(doc);
		callback.append("Added quote.");
	}
}