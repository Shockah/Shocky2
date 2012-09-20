package pl.shockah.shocky2;

import java.util.List;

public abstract class Parser {
	private static final List<Parser> parsers = Util.syncedList(Parser.class);
	
	public static void registerParsers(Parser... parsers) {
		for (int i = 0; i < parsers.length; i++) Parser.parsers.add(parsers[i]);
	}
	public static void unregisterParsers(Parser... parsers) {
		for (int i = 0; i < parsers.length; i++) Parser.parsers.remove(parsers[i]);
	}
	
	protected final String id;
	
	public Parser(String id) {
		this.id = id;
	}
	
	public abstract String parse(String text);
}