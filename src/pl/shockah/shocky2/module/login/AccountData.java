package pl.shockah.shocky2.module.login;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.mongodb.DBObject;

public final class AccountData {
	protected String pass;
	protected List<String> privileges = Collections.synchronizedList(new ArrayList<String>());
	
	public AccountData(String pass) {
		this.pass = pass;
	}
	public AccountData(DBObject doc) {
		pass = (String)doc.get("pass");
		privileges.addAll(Arrays.asList((String[])doc.get("privileges")));
	}
}