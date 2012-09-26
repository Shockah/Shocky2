package pl.shockah.shocky2;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

public class ParentLastURLClassLoader extends ClassLoader {
	private ChildURLClassLoader childClassLoader;
	
	private static class FindClassClassLoader extends ClassLoader {
		public FindClassClassLoader(ClassLoader parent) {
			super(parent);
		}

		public Class<?> findClass(String name) throws ClassNotFoundException {
			return super.findClass(name);
		}
	}
	
	private static class ChildURLClassLoader extends URLClassLoader {
		private FindClassClassLoader realParent;

		public ChildURLClassLoader(URL[] urls, FindClassClassLoader realParent) {
			super(urls, null);
			this.realParent = realParent;
		}

		public Class<?> findClass(String name) throws ClassNotFoundException {
			try {
				return super.findClass(name);
			} catch (ClassNotFoundException e) {
				return realParent.loadClass(name);
			}
		}
	}

	public ParentLastURLClassLoader(List<URL> classpath) {
		super(Thread.currentThread().getContextClassLoader());
		URL[] urls = classpath.toArray(new URL[classpath.size()]);
		childClassLoader = new ChildURLClassLoader(urls,new FindClassClassLoader(this.getParent()));
	}

	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		try {
			return childClassLoader.findClass(name);
		} catch (ClassNotFoundException e) {
			return super.loadClass(name, resolve);
		}
	}
}
