package pl.shockah.shocky2;

import java.io.Console;

public class ThreadConsoleInput extends Thread {
	public void run() {
		Console c = System.console();
		if (c == null) return;
		
		String line;
		while (true) {
			line = c.readLine();
			if (line != null) {
				CommandCallback callback = new CommandCallback();
				Command cmd = Command.getCommand(line.split("\\s")[0]);
				if (cmd != null) cmd.call(null,ETarget.Console,callback,null,null,line);
				if (callback.length() > 0) System.out.println(callback.toString());
			}
		}
	}
}