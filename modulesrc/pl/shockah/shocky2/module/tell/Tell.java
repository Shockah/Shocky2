package pl.shockah.shocky2.module.tell;

import java.util.Date;
import pl.shockah.shocky2.Data;
import com.mongodb.DBObject;

public final class Tell {
	public final String from, to, message;
	public final Date date;
	
	public Tell(String from, String to, String message, Date date) {
		this.from = from;
		this.to = to;
		this.message = message;
		this.date = date;
	}
	public Tell(String from, String to, String message, long stamp) {
		this(from,to,message,new Date(stamp));
	}
	public Tell(DBObject doc) {
		this((String)doc.get("from"),(String)doc.get("to"),(String)doc.get("message"),(Long)doc.get("stamp"));
	}
	
	public DBObject intoDocument() {
		return Data.document("from",from,"to",to,"message",message,"stamp",date.getTime());
	}
}