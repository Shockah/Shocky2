package pl.shockah.shocky2.module.login;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Group implements IGroup {
	protected static List<Group> groups = Collections.synchronizedList(new ArrayList<Group>());
	
	protected String name;
	protected final IPrivileges privileges = new Privileges();
	
	public Group(String name) {
		this.name = name;
		groups.add(this);
	}
	
	public IPrivileges getPrivileges() {
		return privileges;
	}
}