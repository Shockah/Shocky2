package pl.shockah.shocky2.module.login;

public final class Module extends pl.shockah.shocky2.Module {
	public String name() {return "login";}
	public String info() {return "Adds a login system";}
	protected boolean canDisable() {return false;}
	protected String[] staticClasses() {
		String[] ret = new String[]{"LoginData"};
		for (int i = 0; i < ret.length; i++) ret[i] = getClass().getPackage().getName()+"."+ret[i];
		return ret;
	}
}