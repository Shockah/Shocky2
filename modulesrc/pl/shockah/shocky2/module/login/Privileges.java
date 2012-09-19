package pl.shockah.shocky2.module.login;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Privileges implements IPrivileges {
	protected static boolean checkPrivilege(String mask, String privilege) {
		if (mask == null) return true;
		String[] splM = mask.split("\\."), splP = privilege.split("\\.");
		
		if (splM.length > splP.length) return false;
		for (int i = 0; i < splM.length; i++) {
			if (i == splM.length-1 && splM[i].equals("*")) return true;
			else if (!splM[i].equals(splP[i])) return false;
		}
		return true;
	}
	
	protected List<String> privileges = Collections.synchronizedList(new ArrayList<String>());
	
	public void addPrivileges(String... privileges) {
		L: for (String s : privileges) {
			for (int i = 0; i < this.privileges.size(); i++) if (checkPrivilege(s,this.privileges.get(i))) continue L;
			this.privileges.add(s);
		}
	}
	public void removePrivileges(String... privileges) {
		for (String s : privileges) for (int i = 0; i < this.privileges.size(); i++) if (checkPrivilege(s,this.privileges.get(i))) this.privileges.remove(i--);
	}
	public void clearPrivileges() {
		privileges.clear();
	}
	
	public boolean hasPrivilege(String privilege) {
		for (int i = 0; i < this.privileges.size(); i++) if (checkPrivilege(privilege,this.privileges.get(i))) return true;
		return false;
	}
}