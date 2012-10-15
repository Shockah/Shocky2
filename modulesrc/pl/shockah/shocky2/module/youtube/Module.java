package pl.shockah.shocky2.module.youtube;

import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.MessageEvent;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import pl.shockah.HTTPQuery;
import pl.shockah.StringTools;
import pl.shockah.shocky2.BotUtils;
import pl.shockah.shocky2.Data;
import pl.shockah.shocky2.ETarget;
import pl.shockah.shocky2.Shocky;

public final class Module extends pl.shockah.shocky2.ModuleWithCommands {
	protected static String getVideoInfo(String vID) {
		HTTPQuery q = null;
		
		try {
			q = new HTTPQuery("http://gdata.youtube.com/feeds/api/videos/"+URLEncoder.encode(vID,"UTF8")+"?v=2&alt=jsonc");
			q.connect(true,false);
			
			JSONObject jItem = new JSONObject(q.readWhole()).getJSONObject("data");
			q.close();
			
			return videoResult(StringTools.unicodeParse(jItem.getString("title")),jItem.getInt("duration"),jItem.has("rating") ? (float)jItem.getDouble("rating") : -1,jItem.getInt("viewCount"),jItem.getString("uploader"),null);
		} catch (Exception e) {e.printStackTrace();}
		return null;
	}
	protected static String getVideoSearch(String query, boolean data, boolean url) {
		HTTPQuery q = null;
		
		try {
			q = new HTTPQuery("http://gdata.youtube.com/feeds/api/videos?max-results=1&v=2&alt=jsonc&q="+URLEncoder.encode(query,"UTF8"));
			q.connect(true,false);
			
			JSONObject jItem = new JSONObject(q.readWhole()).getJSONObject("data");
			q.close();
			
			if (jItem.getInt("totalItems") == 0) return null;
			jItem = jItem.getJSONArray("items").getJSONObject(0);
			
			return videoResult(StringTools.unicodeParse(jItem.getString("title")),jItem.getInt("duration"),jItem.has("rating") ? (float)jItem.getDouble("rating") : -1,jItem.getInt("viewCount"),jItem.getString("uploader"),"http://youtu.be/"+jItem.getString("id"));
		} catch (Exception e) {e.printStackTrace();}
		return null;
	}
	protected static String videoResult(String title, int durationS, float rating, int views, String uploader, String url) {
		StringBuilder sb = new StringBuilder();
		
		if (title != null) {
			if (sb.length() != 0) sb.append(" | ");
			sb.append(Colors.BOLD+title+Colors.NORMAL);
		}
		if (durationS >= 0) {
			if (sb.length() != 0) sb.append(" | ");
			int durationH = durationS/3600, durationM = (durationS/60)%60;
			durationS %= 60;
			sb.append("length "+Colors.BOLD+(durationH > 0 ? durationH+"h " : "")+(durationM > 0 ? durationM+"m " : "")+durationS+"s"+Colors.NORMAL);
		}
		if (rating >= 0) {
			if (sb.length() != 0) sb.append(" | ");
			sb.append("rated "+Colors.BOLD+String.format("%.2f",rating).replace(",",".")+"/5.00"+Colors.NORMAL);
		}
		if (views >= 0) {
			if (sb.length() != 0) sb.append(" | ");
			sb.append(Colors.BOLD+views+Colors.NORMAL+" view"+(views >= 2 ? "s" : ""));
		}
		if (uploader != null) {
			if (sb.length() != 0) sb.append(" | ");
			sb.append("by "+Colors.BOLD+uploader+Colors.NORMAL);
		}
		if (url != null) {
			if (sb.length() != 0) sb.append(" | ");
			sb.append(url);
		}
		
		return sb.toString();
	}
	
	private Pattern patternURL = Pattern.compile("https?://(?:(?:(?:www\\.)?youtube\\.com/watch\\?.*?v=([a-zA-Z0-9_\\-]+))|(?:(?:www\\.)?youtu\\.be/([a-zA-Z0-9_\\-]+)))");
	
	public String getName() {return "youtube";}
	public String getInfo() {return "YouTube search";}
	public boolean isListener() {return true;}
	
	public void onEnable() {
		addCommands(new CommandYouTube(this));
		
		DBCollection c = Data.getDB().getCollection("config");
		Object[] o = new Object[]{
				"nowPlayingMessage",true,
				"nowPlayingAction",true,
				"urlAnnounce",true,
				"regexNowPlayingMessage","^np: (.*)$",
				"regexNowPlayingAction","^.*?(?:(?:playing)|(?:listening (?:to)?)): (.+)$"
		};
		for (int i = 0; i < o.length; i += 2) {
			BasicDBObject doc = Data.document("key",getName()+"->"+o[i]);
			if (c.findOne(doc) == null) {
				doc.put("value",o[i+1] instanceof Object[] ? Data.document((Object[])o[i+1]) : o[i+1]);
				c.insert(doc);
			}
		}
	}
	
	public void onMessage(MessageEvent<PircBotX> event) {
		if ((Boolean)Data.get(event.getChannel().getName(),getName()+"->nowPlayingMessage")) {
			for (String regex : ((String)Data.get(event.getChannel().getName(),getName()+"->regexNowPlayingMessage")).split("\\\\n")) {
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(event.getMessage());
				if (m.find()) {
					String s = m.group(1);
					if (s.startsWith("http://") || s.startsWith("www.//") || s.startsWith("youtu.be/") || s.startsWith("youtube/")) return;
					String result = getVideoSearch(s,true,true);
					if (result == null) return;
					s = BotUtils.mungeAllNicks(event.getChannel(),result);
					Shocky.send(event.getBot(),ETarget.Channel,event.getChannel(),null,event.getUser().getNick()+": "+s);
					break;
				}
			}
		}
		
		if ((Boolean)Data.get(event.getChannel().getName(),getName()+"->urlAnnounce")) {
			Matcher m = patternURL.matcher(event.getMessage());
			while (m.find()) {
				String vID = m.group(1);
				if (vID == null) vID = m.group(2);
				String result = getVideoInfo(vID);
				if (result == null) return;
				Shocky.send(event.getBot(),ETarget.Channel,event.getChannel(),null,event.getUser().getNick()+": "+result);
			}
		}
	}
	public void onAction(ActionEvent<PircBotX> event) {
		if ((Boolean)Data.get(event.getChannel().getName(),getName()+"->nowPlayingAction")) {
			for (String regex : ((String)Data.get(event.getChannel().getName(),getName()+"->regexNowPlayingAction")).split("\\\\n")) {
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(event.getAction());
				if (m.find()) {
					String s = m.group(1);
					if (s.startsWith("http://") || s.startsWith("www.//") || s.startsWith("youtu.be/") || s.startsWith("youtube/")) return;
					String result = getVideoSearch(s,true,true);
					if (result == null) return;
					s = BotUtils.mungeAllNicks(event.getChannel(),result);
					Shocky.send(event.getBot(),ETarget.Channel,event.getChannel(),null,event.getUser().getNick()+": "+s);
					break;
				}
			}
		}
	}
}