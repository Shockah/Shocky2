package pl.shockah.shocky2.module.google;

import java.net.URLEncoder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import pl.shockah.HTTPQuery;
import pl.shockah.StringTools;
import pl.shockah.shocky2.CommandCallback;
import pl.shockah.shocky2.CommandWithAliases;
import pl.shockah.shocky2.ETarget;
import pl.shockah.shocky2.Shocky;
import pl.shockah.shocky2.Util;

public class CommandGoogle extends CommandWithAliases {
	public String command() {return "google";}
	public String help() {
		return ".google {query} - returns Google search result";
	}
	public void onAdd() {
		addAlias("g");
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
		
		try {
			HTTPQuery q = new HTTPQuery("http://ajax.googleapis.com/ajax/services/search/web?v=1.0&safe=off&q="+URLEncoder.encode(Util.implode(split,1," "),"UTF8"));
			StringBuilder result = new StringBuilder();
			q.connect(true,false);
			String line = q.readWhole();
			
			JSONObject json = new JSONObject(line);
			JSONArray results = json.getJSONObject("responseData").getJSONArray("results");
			if (results.length() == 0) {
				callback.append("No results.");
				return;
			}
			JSONObject r = results.getJSONObject(0);
			String title = StringTools.ircFormatted(r.getString("titleNoFormatting"),true);
			String url = StringTools.ircFormatted(r.getString("unescapedUrl"),false);
			String content = StringTools.ircFormatted(r.getString("content"),true);
			result.append(url);
			result.append(" -- ");result.append(title);
			result.append(": ");
			if (!content.isEmpty()) result.append(content);
			else result.append("No description available.");
			callback.append(result);
		} catch (Exception e) {Shocky.handle(e);}
	}
}