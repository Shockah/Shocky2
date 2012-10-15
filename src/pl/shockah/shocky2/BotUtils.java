package pl.shockah.shocky2;

import java.util.*;
import java.util.regex.Pattern;
import org.pircbotx.Channel;
import org.pircbotx.User;
import pl.shockah.*;

public class BotUtils {
	public static final Pattern
		patternURL = Pattern.compile("[a-z]+://(www\\.)?[a-z0-9]+(\\.[a-z]+)+/([^/:]+/)*([^/]*)?"),
		patternNick = Pattern.compile("[a-zA-Z0-9\\Q_-\\[]{}^`|\\E]+");
	private static final String
		mungeOriginal =	"abcdefghijklmnoprstuwxyzABCDEGHIJKLMORSTUWYZ0123456789",
		mungeReplace =	"äḃċđëƒġħíĵķĺṁñöρŗšţüωχÿźÅḂÇĎĒĠĦÍĴĶĹṀÖŖŠŢŮŴỲŻ０１２３４５６７８９";
	private static final String
		oddOriginal =	"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ",
		oddReplace =	"αвcđєfġнίנкlмиoρqяsтυvωxуzαвcđєfġнίנкlмиoρqяsтυvωxуz";
	private static final String
		flipOriginal =	"!().12345679<>?ABCDEFGJKLMPQRTUVWY[]_abcdefghijklmnpqrtuvwy{},'\"┳",
		flipReplace =	"¡)(˙⇂ᄅƐㄣϛ9Ɫ6><¿∀ℇƆ◖ƎℲפſ丬˥WԀΌᴚ⊥∩ΛMλ][‾ɐqɔpǝɟɓɥıɾʞlɯudbɹʇnʌʍʎ}{',„┻";
	
	//public static final List<PasteService> services = new LinkedList<PasteService>();
	
	public static ArrayList<String> getAllUrls(String text) {
		String[] spl = text.split(" ");
		ArrayList<String> urls = new ArrayList<String>();
		for (String s : spl) if (patternURL.matcher(s).find()) urls.add(s);
		return urls;
	}
	public static String shortenAllUrls(String text, String channel) {
		ArrayList<String> urls = getAllUrls(text);
		for (String url : urls) text = text.replace(url,shortenUrl(url,channel));
		return text;
	}
	public static String shortenUrl(String url, String channel) {
		try {
			String bitLyUser = (String)Data.get(channel,"bitly->user"), bitLyAPIKey = (String)Data.get(channel,"bitly->apiKey");
			if (bitLyUser == null || bitLyAPIKey == null) return url;
			
			HTTPQuery q = new HTTPQuery("http://api.bitly.com/v3/shorten?"+HTTPQuery.parseArgs("format","txt","login",bitLyUser,"apiKey",bitLyAPIKey,"longUrl",url));
			q.connect(true,true,false);
			String line = q.read().get(0);
			q.close();
			
			if (line.startsWith("http://")) return line;
		} catch (Exception e) {e.printStackTrace();}
		return url;
	}
	
	/*public static void initPasteServices() {
		String key = null;
		services.clear();
		services.add(new ServicePasteKdeOrg());
		key = Data.config.getString("api-pastebin.com");
		if (key != null)
			services.add(new ServicePastebinCom(key));
		key = Data.config.getString("api-pastebin.ca");
		if (key != null)
			services.add(new ServicePastebinCa(key));
	}
	
	public static String paste(CharSequence data) {
		String link = null;
		for (PasteService service : services) {
			link = service.paste(data);
			if (link == null) continue;
			if (link.isEmpty() || link.startsWith("http://")) break;
		}
		return link;
	}*/
	
	public static String mungeAllNicks(Channel channel, String message, String... dontMunge) {
		if (channel == null) return message;
		String[] spl = message.split(" ");
l1:		for (int i = 0; i < spl.length; i++) {
			for (String dont : dontMunge) if (regexNick(dont.toLowerCase()).matcher(spl[i].toLowerCase()).find()) continue l1;
			for (User user : channel.getUsers()) {
				if (regexNick(user.getNick().toLowerCase()).matcher(spl[i].toLowerCase()).find()) spl[i] = mungeNick(spl[i]);
			}
		}
		return StringTools.implode(spl," ");
	}
	public static String mungeNick(String nick) {
		char[] chars = nick.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			int iof = mungeOriginal.indexOf(chars[i]);
			if (iof == -1) continue;
			chars[i] = mungeReplace.charAt(iof);
		}
		return String.copyValueOf(chars);
	}
	private static Pattern regexNick(String nick) {
		return Pattern.compile("^[<+@\\(]*"+Pattern.quote(nick)+"(?!"+patternNick.pattern()+")[>\\)]*");
	}
	
	public static String flip(String str) {
		char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			int iof1 = flipOriginal.indexOf(chars[i]);
			int iof2 = flipReplace.indexOf(chars[i]);
			if (iof1 == -1 && iof2 == -1) continue;
			if (iof1 != -1)
				chars[i] = flipReplace.charAt(iof1);
			else if (iof2 != -1)
				chars[i] = flipOriginal.charAt(iof2);
		}
		return String.copyValueOf(chars);
	}
	
	public static String odd(String str) {
		char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			int iof1 = oddOriginal.indexOf(chars[i]);
			int iof2 = oddReplace.indexOf(chars[i]);
			if (iof1 == -1 && iof2 == -1) continue;
			if (iof1 != -1)
				chars[i] = oddReplace.charAt(iof1);
			else if (iof2 != -1)
				chars[i] = oddOriginal.charAt(iof2);
		}
		return String.copyValueOf(chars);
	}
	
	public static String timeAgo(Date date) {return timeAgo(date,new Date());}
	public static String timeAgo(Date from, Date to) {
		long dif = (to.getTime()-from.getTime())/1000;
		int s,m,h,d,w;
		StringBuilder sb = new StringBuilder();
		
		s = (int)dif%60; dif /= 60;
		m = (int)dif%60; dif /= 60;
		h = (int)dif%24; dif /= 24;
		d = (int)dif%7; dif /= 7;
		w = (int)dif;
		
		if (w > 0) {if (sb.length() != 0) sb.append(" "); sb.append(""+w+"w");}
		if (w+d > 0) {if (sb.length() != 0) sb.append(" "); sb.append(""+d+"d");}
		if (w+d+h > 0) {if (sb.length() != 0) sb.append(" "); sb.append(""+h+"h");}
		if (w+d+h+m > 0) {if (sb.length() != 0) sb.append(" "); sb.append(""+m+"m");}
		if (w+d+h+m+s > 0) {if (sb.length() != 0) sb.append(" "); sb.append(""+s+"s");}
		if (sb.length() == 0) return "now";
		sb.append(" ago");
		return sb.toString();
	}
}