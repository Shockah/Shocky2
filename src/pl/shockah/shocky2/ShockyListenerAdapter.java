package pl.shockah.shocky2;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import pl.shockah.shocky2.events.ActionOutEvent;
import pl.shockah.shocky2.events.MessageOutEvent;
import pl.shockah.shocky2.events.NoticeOutEvent;
import pl.shockah.shocky2.events.PrivateMessageOutEvent;

@SuppressWarnings({"rawtypes","unchecked"}) public class ShockyListenerAdapter extends ListenerAdapter<PircBotX> {
	static {
		for (Method curMethod : ShockyListenerAdapter.class.getDeclaredMethods()) {
			if (curMethod.getName().equals("onEvent")) continue;
			Class curClass = curMethod.getParameterTypes()[0];
			if (!curClass.isInterface()) {
				Set methods = new HashSet();
				methods.add(curMethod);
				eventToMethod.put(curClass, methods);
			}
		}
		for (Method curMethod : ShockyListenerAdapter.class.getDeclaredMethods()) {
			Class curClass = curMethod.getParameterTypes()[0];
			if (!curClass.isInterface()) continue;
			for (Class curEvent : eventToMethod.keySet()) if (curClass.isAssignableFrom(curEvent)) (eventToMethod.get(curEvent)).add(curMethod);
		}
	}
	
	public void onMessageOut(MessageOutEvent<PircBotX> event) throws Exception {}
	public void onActionOut(ActionOutEvent<PircBotX> event) throws Exception {}
	public void onPrivateMessageOut(PrivateMessageOutEvent<PircBotX> event) throws Exception {}
	public void onNoticeOut(NoticeOutEvent<PircBotX> event) throws Exception {}
}