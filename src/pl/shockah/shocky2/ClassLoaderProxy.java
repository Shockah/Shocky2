package pl.shockah.shocky2;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

public class ClassLoaderProxy extends ClassLoader {
	private ClassLoader myClassLoader;
	
	public ClassLoaderProxy(ClassLoader cl) {
		myClassLoader = cl;
	}
	
	public void setClassLoader(ClassLoader cl) {
		myClassLoader = cl;
	}
	public ClassLoader getClassLoader() {
		return myClassLoader;
	}
	
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return myClassLoader.loadClass(name);
	}
	public URL getResource(String name) {
		return myClassLoader.getResource(name);
	}
	public Enumeration<URL> getResources(String name) throws IOException {
		return myClassLoader.getResources(name);
	}
	public InputStream getResourceAsStream(String name) {
		return myClassLoader.getResourceAsStream(name);
	}
	public void setDefaultAssertionStatus(boolean enabled) {
		myClassLoader.setDefaultAssertionStatus(enabled);
	}
	public void setPackageAssertionStatus(String packageName, boolean enabled) {
		myClassLoader.setPackageAssertionStatus(packageName,enabled);
	}
	public void setClassAssertionStatus(String className, boolean enabled) {
		myClassLoader.setClassAssertionStatus(className,enabled);
	}
	public void clearAssertionStatus() {
		myClassLoader.clearAssertionStatus();
	}
}