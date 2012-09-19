package pl.shockah.shocky2.module.login;

public interface IPrivileges {
	public void addPrivileges(String... privileges);
	public void removePrivileges(String... privileges);
	public void clearPrivileges();
	public boolean hasPrivilege(String privilege);
}