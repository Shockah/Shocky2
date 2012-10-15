package pl.shockah.shocky2;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.pircbotx.PircBotX;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public abstract class Module extends ShockyListenerAdapter implements Comparable<Module> {
	private static final List<Module> modules = Util.syncedList(Module.class), modulesOn = Util.syncedList(Module.class);
	private static final Map<String,List<Module>> disabledModules = Collections.synchronizedMap(new HashMap<String,List<Module>>());
	
	public static ClassLoader createClassLoader(URL... extraURLs) {
		ArrayList<URL> urls = new ArrayList<URL>();
		try {
			urls.add(new File("modules").toURI().toURL());
			urls.addAll(Arrays.asList(extraURLs));
		} catch (Exception e) {Shocky.handle(e);}
		return new URLClassLoader(urls.toArray(new URL[urls.size()]));
	}
	
	public static Module load(ModuleSource<?> source) {
		return load(source,true);
	}
	private static Module load(ModuleSource<?> source, boolean breakIfAlreadyLoaded) {
		Module module = tryToLoad(source);
		
		if (module != null) {
			if (breakIfAlreadyLoaded) for (int i = 0; i < modules.size(); i++) if (modules.get(i).getName().equals(module.getName())) return null;
			modules.add(module);
			
			DBCollection c = Data.getDB().getCollection("modules");
			BasicDBObject doc = Data.document("name",module.getName());
			if (c.findOne(doc) == null) c.insert(Data.document("name",module.getName(),"enabled",new Object[]{"^",true}));
			
			DBObject find = c.findOne(doc);
			DBObject enabled = (DBObject)find.get("enabled");
			if ((Boolean)enabled.get("^")) enable(module,null);
			for (String key : enabled.keySet()) if (key.startsWith("#") && !(Boolean)enabled.get(key)) disable(module,key);
		}
		return module;
	}
	private static Module tryToLoad(ModuleSource<?> source) {
		Module module = null;
		try {
			Class<?> c = null;
			if (source.source instanceof String) {
				c = createClassLoader().loadClass((String)source.source);
			} else if (source.source instanceof File) {
				File file = (File)source.source;
				String moduleName = file.getName(); 
				if (moduleName.equals("Module.class") || moduleName.equals("StaticModule.class")) moduleName = new StringBuilder(moduleName).reverse().delete(0,6).reverse().toString();
				else return null;
				
				c = createClassLoader().loadClass(moduleName);
			} else if (source.source instanceof URL) {
				URL url = (URL)source.source;
				String moduleName = url.toString();
				StringBuilder sb = new StringBuilder(moduleName).reverse();
				moduleName = new StringBuilder(sb.substring(0,sb.indexOf("/"))).reverse().toString();
				String modulePath = new StringBuilder(url.toString()).delete(0,url.toString().length()-moduleName.length()).toString();
				if (moduleName.equals("Module.class") || moduleName.equals("StaticModule.class")) moduleName = new StringBuilder(moduleName).reverse().delete(0,6).reverse().toString();
				else return null;
				
				c = createClassLoader(new URL(modulePath)).loadClass(moduleName);
			}
			
			if (c != null && Module.class.isAssignableFrom(c)) module = (Module)c.newInstance();
			module.source = source;
		} catch (Exception e) {Shocky.handle(e);}
		return module;
	}
	public static boolean unload(Module module) {
		if (module == null) return false;
		if (!modules.contains(module)) return false;
		if (modulesOn.contains(module)) disable(module,null);
		modules.remove(module);
		return true;
	}
	public static boolean reload(Module module) {
		if (module == null) return false;
		ModuleSource<?> src = module.source;
		Module m = load(src,false);
		if (m != null) {
			unload(module);
			return true;
		} else return false;
	}
	
	public static boolean enable(Module module, String channel) {
		if (module == null) return false;
		if (channel != null) {
			List<Module> disabled;
			if (!disabledModules.containsKey(channel)) {
				disabled = new ArrayList<Module>();
				disabledModules.put(channel, disabled);
			} else disabled = disabledModules.get(channel);
			if (disabled.remove(module)) {
				DBCollection c = Data.getDB().getCollection("modules");
				BasicDBObject doc = Data.document("name",module.getName());
				DBObject find = c.findOne(doc);
				((DBObject)find.get("enabled")).put(channel,true);
				c.update(doc,find);
				return true;
			} else return false;
		} else {
			if (modulesOn.contains(module)) return false;
			module.onEnable();
			if (module.isListener()) Shocky.botManager.listenerManager.addListener(module);
			modulesOn.add(module);
			
			DBCollection c = Data.getDB().getCollection("modules");
			BasicDBObject doc = Data.document("name",module.getName());
			DBObject find = c.findOne(doc);
			((DBObject)find.get("enabled")).put("^",true);
			c.update(doc,find);
			return true;
		}
	}
	public static boolean disable(Module module, String channel) {
		if (module == null) return false;
		if (channel != null) {
			List<Module> disabled;
			if (!disabledModules.containsKey(channel)) {
				disabled = new ArrayList<Module>();
				disabledModules.put(channel, disabled);
			} else disabled = disabledModules.get(channel);
			if (disabled.contains(module)) return false;
			if (disabled.add(module)) {
				DBCollection c = Data.getDB().getCollection("modules");
				BasicDBObject doc = Data.document("name",module.getName());
				DBObject find = c.findOne(doc);
				((DBObject)find.get("enabled")).put(channel,false);
				c.update(doc,find);
				return true;
			} else return false;
		} else {
			if (!modulesOn.contains(module)) return false;
			if (module.isListener()) Shocky.botManager.listenerManager.removeListener(module);
			module.onDisable();
			modulesOn.remove(module);
			
			DBCollection c = Data.getDB().getCollection("modules");
			BasicDBObject doc = Data.document("name",module.getName());
			DBObject find = c.findOne(doc);
			((DBObject)find.get("enabled")).put("^",false);
			c.update(doc,find);
			return true;
		}
	}
	
	public static ArrayList<Module> getModules() {
		return new ArrayList<Module>(modules);
	}
	public static Module getModuleByName(String name) {
		for (int i = 0; i < modules.size(); i++) if (modules.get(i).getName().equals(name)) return modules.get(i);
		return null;
	}
	
	public static ArrayList<Module> loadNewModules() {
		ArrayList<String> loadClasses = new ArrayList<String>();
		ArrayList<Module> ret = new ArrayList<Module>();
		File dir = new File("modules"); dir.mkdir();
		
		ArrayList<File> dirs = new ArrayList<File>();
		dirs.add(dir);
		
		while (!dirs.isEmpty()) {
			dir = dirs.remove(0);
			for (File f : dir.listFiles()) {
				if (f.getName().matches("\\.{1,2}")) continue;
				if (f.isDirectory()) dirs.add(f);
				else {
					if (f.getName().matches("Module\\.class")) {
						String cname = "Module";
						File _f = f;
						while (!_f.equals(new File("modules"))) {
							if (!_f.equals(f)) cname = _f.getName()+"."+cname;
							_f = _f.getParentFile();
						}
						loadClasses.add(cname);
					}
				}
			}
		}
		
		for (int i = 0; i < loadClasses.size(); i++) {
			String cname = loadClasses.get(i);
			Module m = load(new ModuleSource<String>(cname));
			if (m != null) ret.add(m);
		}
		
		Collections.sort(ret);
		return ret;
	}
	
	public static DBCollection getCollection(String moduleName) {
		return Data.getDB().getCollection("module-"+moduleName);
	}
	
	private ModuleSource<?> source;
	
	public abstract String getName();
	public abstract String getInfo();
	protected boolean isListener() {return false;}
	public boolean canDisable() {return true;}
	
	public void onEnable() {}
	public void onDisable() {}
	public void onDataSave() {}
	public void onDie(PircBotX bot) {}
	
	public final int compareTo(Module module) {
		return getName().compareTo(module.getName());
	}
	
	public final boolean isEnabled(String channel) {
		if (disabledModules.containsKey(channel) && disabledModules.get(channel).contains(this)) return false;
		return modulesOn.contains(this);
	}
	public final DBCollection getCollection() {
		return getCollection(getName());
	}
	
	public final boolean unload() {
		return unload(this);
	}
	public final boolean reload() {
		return reload(this);
	}
	public final boolean enable(String channel) {
		return enable(this,channel);
	}
	public final boolean disable(String channel) {
		return disable(this,channel);
	}
}