package pl.shockah.shocky2;

import java.util.List;

public abstract class ModuleWithCommands extends Module {
	private List<Command> commands = Util.syncedList(Command.class);
	
	public abstract void onEnable();
	public void onDisable() {
		clearCommands();
	}
	
	public final void addCommand(Command cmd) {
		Command.addCommands(cmd);
		commands.add(cmd);
	}
	public final void clearCommands() {
		Command.removeCommands(commands.toArray(new Command[commands.size()]));
		commands.clear();
	}
}