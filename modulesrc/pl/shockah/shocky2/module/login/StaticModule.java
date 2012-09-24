package pl.shockah.shocky2.module.login;

public class StaticModule extends pl.shockah.shocky2.StaticModule {
	public String name() {return "login[static]";}
	public String info() {return null;}
	protected void loadClasses() {
		String[] ret = new String[]{"LoginData"};
		for (int i = 0; i < ret.length; i++) {
			try {
				Class.forName(getClass().getPackage().getName()+"."+ret[i]);
			} catch (Exception e) {}
		}
	}
}