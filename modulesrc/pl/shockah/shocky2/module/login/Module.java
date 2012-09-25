package pl.shockah.shocky2.module.login;

import java.util.Random;
import pl.shockah.shocky2.Data;

public final class Module extends pl.shockah.shocky2.ModuleWithCommands {
	public String name() {return "login";}
	public String info() {return "Adds a login system";}
	
	public void onEnable() {
		addCommand(new CommandLogin());
		addCommand(new CommandRegister());
		
		Random rnd = new Random();
		String chars = "0123456789abcdef";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 32; i++) sb.append(chars.charAt(rnd.nextInt(chars.length())));
		Data.getConfig().setNotExists("login->md5extra",sb);
	}
}