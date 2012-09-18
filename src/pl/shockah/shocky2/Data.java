package pl.shockah.shocky2;

import pl.shockah.Config;

public class Data {
	protected static Config cfg = new Config();
	
	public static void fillDefault() {
		cfg.set("bot->verbose",true);
		cfg.set("bot->server","irc.esper.net");
		cfg.set("bot->encoding","UTF8");
		cfg.set("bot->name","Shocky");
		cfg.set("bot->login","Shocky");
		cfg.set("bot->messagedelay",500);
	}
	
	public static Config getConfig() {return cfg;}
	public static Config getConfig(String channel) {return cfg.getConfig(channel);}
	
	public static String getString(String key) {return cfg.getString(key);}
	public static boolean getBoolean(String key) {return cfg.getBoolean(key);}
	public static int getInt(String key) {return cfg.getInt(key);}
	public static long getLong(String key) {return cfg.getLong(key);}
	public static float getFloat(String key) {return cfg.getFloat(key);}
	public static double getDouble(String key) {return cfg.getDouble(key);}
	
	public static String getString(String channel, String key) {
		if (cfg.existsConfig(channel)) {
			Config c = getConfig(channel);
			if (c.exists(key)) return c.getString(key);
		}
		return cfg.getString(key);
	}
	public static boolean getBoolean(String channel, String key) {
		if (cfg.existsConfig(channel)) {
			Config c = getConfig(channel);
			if (c.exists(key)) return c.getBoolean(key);
		}
		return cfg.getBoolean(key);
	}
	public static int getInt(String channel, String key) {
		if (cfg.existsConfig(channel)) {
			Config c = getConfig(channel);
			if (c.exists(key)) return c.getInt(key);
		}
		return cfg.getInt(key);
	}
	public static long getLong(String channel, String key) {
		if (cfg.existsConfig(channel)) {
			Config c = getConfig(channel);
			if (c.exists(key)) return c.getLong(key);
		}
		return cfg.getLong(key);
	}
	public static float getFloat(String channel, String key) {
		if (cfg.existsConfig(channel)) {
			Config c = getConfig(channel);
			if (c.exists(key)) return c.getFloat(key);
		}
		return cfg.getFloat(key);
	}
	public static double getDouble(String channel, String key) {
		if (cfg.existsConfig(channel)) {
			Config c = getConfig(channel);
			if (c.exists(key)) return c.getDouble(key);
		}
		return cfg.getDouble(key);
	}
}