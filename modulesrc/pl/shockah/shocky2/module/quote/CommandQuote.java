package pl.shockah.shocky2.module.quote;

import java.util.Random;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import pl.shockah.StringTools;
import pl.shockah.shocky2.BotUtils;
import pl.shockah.shocky2.CommandCallback;
import pl.shockah.shocky2.CommandWithAliases;
import pl.shockah.shocky2.Data;
import pl.shockah.shocky2.ETarget;
import pl.shockah.shocky2.Module;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class CommandQuote extends CommandWithAliases {
	public CommandQuote(Module module) {
		super(module);
	}
	
	public String command() {return "quote";}
	public String help() {
		return ".quote [channel] [tags] [id] - returns a quote";
	}
	protected void onAdd() {
		addAlias("q");
		super.onAdd();
	}
	public void call(PircBotX bot, ETarget target, CommandCallback callback, Channel channel, User sender, String message) {
		String[] split = message.split("\\s");
		
		String aChannel = channel == null ? null : channel.getName(), aTags = null, aId = null;
		if (split.length == 2) {
			if ("#.".contains(""+split[1].charAt(0))) aChannel = split[1];
			else if (StringTools.isInt(split[1])) aId = split[1];
			else aTags = split[1];
		} else if (split.length == 3) {
			if ("#.".contains(""+split[1].charAt(0))) {
				aChannel = split[1];
				if (StringTools.isInt(split[2])) aId = split[2];
				else aTags = split[2];
			} else {
				aTags = split[1];
				aId = split[2];
			}
		} else if (split.length == 4) {
			aChannel = split[1];
			aTags = split[2];
			aId = split[3];
		}
		
		if (".".equals(aChannel)) aChannel = channel == null ? null : channel.getName();
		if (aChannel != null) aChannel = aChannel.toLowerCase();
		if (aTags != null) aTags = aTags.toLowerCase();
		
		if (aChannel != null) {
			BasicDBObject toFind = Data.document("channel",aChannel);
			if (aTags != null) {
				BasicDBObject findAnd = new BasicDBObject();
				findAnd.put("$and",aTags.contains(";") ? aTags.split(";") : new String[]{aTags});
				toFind.put("tags",findAnd);
			}
			DBCursor find = module.getCollection().find(toFind);
			
			int count = find.count();
			
			if (count == 0) {
				callback.append("No quotes.");
				return;
			}
			
			int aIntId = aId == null ? (count == 1 ? 1 : new Random().nextInt(count)+1) : Integer.parseInt(aId);
			if (aIntId != 0) {
				find = find.sort(Data.document("_id",aIntId > 0 ? 1 : -1));
				int oaIntId = aIntId;
				aIntId = Math.abs(aIntId);
				
				if (aIntId > count) {
					callback.append("No quotes.");
					return;
				}
				find.skip(aIntId-1);
				
				DBObject doc = find.next();
				if (doc == null) {
					callback.append("No quotes.");
					return;
				}
				
				callback.append("["+aChannel+": "+(oaIntId < 0 ? count+oaIntId+1 : oaIntId)+"/"+count+"] "+BotUtils.mungeAllNicks(channel,(String)doc.get("quote")));
				return;
			}
		}
		
		if (callback.target != ETarget.Console) callback.target = ETarget.Notice;
		callback.append(help());
	}
}