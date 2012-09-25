package pl.shockah.shocky2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Util {
	public static <T> List<T> syncedList(Class<? extends T> cls) {
		return Collections.synchronizedList(new ArrayList<T>());
	}
	public static <K,V> Map<K,V> syncedMap(Class<? extends K> cls1, Class<? extends V> cls2) {
		return Collections.synchronizedMap(new HashMap<K,V>());
	}
	
	public static <T> String implode(T[] spl, String separator) {return implode(spl,0,spl.length-1,separator);}
	public static <T> String implode(T[] spl, int a, String separator) {return implode(spl,a,spl.length-1,separator);}
	public static <T> String implode(T[] spl, int a, int b, String separator) {
		StringBuffer sb = new StringBuffer();
		while (a <= b) {
			if (sb.length() != 0) sb.append(separator);
			sb.append(spl[a++]);
		}
		return sb.toString();
	}
}