package pl.shockah.shocky2;

import java.util.List;

public abstract class CommandWithAliases extends Command {
	protected final List<String> aliasName = Util.syncedList(String.class);
	protected final List<CommandAlias> aliases = Util.syncedList(CommandAlias.class);
	
	public void addAlias(String name) {
		aliasName.add(name);
	}
	public void removeAlias(String name) {
		aliasName.remove(name);
	}
	
	protected void onAdd() {
		super.onAdd();
		
		aliases.clear();
		for (int i = 0; i < aliasName.size(); i++) {
			CommandAlias ca = new CommandAlias(this,aliasName.get(i));
			aliases.add(ca);
			addCommands(ca);
		}
	}
	protected void onRemove() {
		super.onRemove();
		
		for (int i = 0; i < aliases.size(); i++) removeCommands(aliases.get(i));
		aliases.clear();
	}
}