package pl.shockah.shocky2;

public class Shocky {
	public static BotManager botManager;
	
	public static void main(String[] args) {
		botManager = new BotManager();
	}
	
	public static void handle(Throwable t) {}
}