package pl.shockah.shocky2.module.wolframalpha;

import java.net.URLEncoder;
import java.util.ArrayList;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import pl.shockah.HTTPQuery;
import pl.shockah.XMLObject;
import pl.shockah.shocky2.BotUtils;
import pl.shockah.shocky2.Data;

public final class Module extends pl.shockah.shocky2.ModuleWithCommands {
	protected static String getResult(Channel channel, String query) {
		String apiKey = (String)Data.get(channel == null ? null : channel.getName(),"wolframalpha->apiKey");
		if (apiKey == null || apiKey.isEmpty()) return ">>> WolframAlpha module can't be used without setting up an API key. Get one at http://products.wolframalpha.com/developers/ <<<";
		
		try {
			HTTPQuery q = new HTTPQuery("http://api.wolframalpha.com/v2/query?appid="+apiKey+"&format=Plaintext&input="+URLEncoder.encode(query,"UTF8"));
			q.connect(true,false);
			XMLObject xBase = XMLObject.deserialize(q.readWhole());
			q.close();
			
			if (xBase.getBaseElement().getAttribute("error").equals("true")) return null;
			if (xBase.getBaseElement().getAttribute("success").equals("false")) return "No result | "+BotUtils.shortenUrl("http://www.wolframalpha.com/input/?i="+URLEncoder.encode(query,"UTF8"),channel == null ? null : channel.getName());
			
			ArrayList<String> parts = new ArrayList<String>();
			parts.add(BotUtils.shortenUrl("http://www.wolframalpha.com/input/?i="+URLEncoder.encode(query,"UTF8"),channel == null ? null : channel.getName()));
			
			ArrayList<XMLObject> xPods = xBase.getElement("queryresult").get(0).getElement("pod");
			for (XMLObject xPod : xPods) {
				if (!"true".equals(xPod.getAttribute("primary")) && !xPod.getAttribute("title").equals("Alternate form")) continue;
				
				StringBuilder sb = new StringBuilder();
				ArrayList<XMLObject> xSubpods = xPod.getElement("subpod");
				for (XMLObject xSubpod : xSubpods) {
					if (xSubpod.getElement("plaintext").isEmpty()) continue;
					if (sb.length() != 0) sb.append("  ");
					sb.append(xSubpod.getElement("plaintext").get(0).getValue());
				}
				if (sb.length() != 0) parts.add(Colors.BOLD+xPod.getAttribute("title")+Colors.NORMAL+": "+sb.toString().replace("\n"," "));
			}
			
			StringBuilder sb = new StringBuilder();
			for (String part : parts) {
				if (sb.length() != 0) sb.append(" | ");
				sb.append(part);
			}
			
			return sb.toString();
		} catch (Exception e) {e.printStackTrace();}
		
		return null;
	}
	
	public String getName() {return "wolframalpha";}
	public String getInfo() {return "WolframAlpha module";}
	
	public void onEnable() {
		addCommands(new CommandWolframAlpha(this));
	}
}