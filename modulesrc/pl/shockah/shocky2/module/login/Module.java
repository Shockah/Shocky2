package pl.shockah.shocky2.module.login;

import java.util.Random;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import pl.shockah.shocky2.Data;

public final class Module extends pl.shockah.shocky2.ModuleWithCommands {
	public String getName() {return "login";}
	public String getInfo() {return "Adds a login system";}
	protected boolean canDisable() {return false;}
	
	public void onEnable() {
		addCommands(new CommandLogin(this),new CommandRegister(this),new CommandPrivileges());
		
		Random rnd = new Random();
		String chars = "0123456789abcdef";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 32; i++) sb.append(chars.charAt(rnd.nextInt(chars.length())));
		
		DBCollection c = getCollection();
		Object[] o = new Object[]{
				"md5extra",sb.toString()
		};
		for (int i = 0; i < o.length; i += 2) {
			BasicDBObject doc = Data.document("key",o[i]);
			if (c.find(doc) == null) {
				doc.put("value",o[i+1] instanceof Object[] ? Data.document((Object[])o[i+1]) : o[i+1]);
				c.insert(doc);
			}
		}
	}
}