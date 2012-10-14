package pl.shockah.shocky2;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

public class Data {
	protected static Mongo mongo;
	protected static DB db;
	
	protected static void initMongo() {
		try {
			mongo = new Mongo();
			db = mongo.getDB("shocky2");
			
			DBCollection c = getCollection();
			String sub = "bot";
			Object[] o = new Object[]{
					"verbose",true,
					"server","irc.esper.net",
					"port",6667,
					"encoding","UTF8",
					"name","ShockyDev",
					"login","Shocky",
					"messageDelay",500L,
					"maxChannels",50,
					"commandChars","."
			};
			for (int i = 0; i < o.length; i += 2) {
				BasicDBObject doc = document("key",sub+"->"+o[i]);
				if (c.findOne(doc) == null) {
					doc.put("value",o[i+1] instanceof Object[] ? document((Object[])o[i+1]) : o[i+1]);
					c.insert(doc);
				}
			}
		} catch (Exception e) {Shocky.handle(e);}
	}
	
	public static Mongo getMongo() {
		return mongo;
	}
	public static DB getDB() {
		return db;
	}
	public static DBCollection getCollection() {
		return db.getCollection("config");
	}
	
	public static BasicDBObject document(Object... o) {
		BasicDBObject doc = new BasicDBObject();
		
		for (int i = 0; i < o.length; i += 2) {
			assert o[i] instanceof String;
			doc.put((String)o[i],o[i+1] instanceof Object[] ? document((Object[])o[i+1]) : o[i+1]);
		}
		
		return doc;
	}
}