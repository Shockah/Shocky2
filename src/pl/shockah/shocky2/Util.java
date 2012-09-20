package pl.shockah.shocky2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Util {
	public static <T> List<T> syncedList(Class<? extends T> cls) {
		return Collections.synchronizedList(new ArrayList<T>());
	}
}