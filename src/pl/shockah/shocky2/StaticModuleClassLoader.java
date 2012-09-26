package pl.shockah.shocky2;

import java.net.URL;
import java.net.URLClassLoader;

public class StaticModuleClassLoader extends URLClassLoader {
	public StaticModuleClassLoader() {
		this(new URL[]{});
	}
	public StaticModuleClassLoader(URL[] urls) {
		super(urls);
	}
	
	public void addURL(URL url) {
		super.addURL(url);
	}
}