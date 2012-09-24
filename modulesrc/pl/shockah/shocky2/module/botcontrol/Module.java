package pl.shockah.shocky2.module.botcontrol;

public final class Module extends pl.shockah.shocky2.ModuleWithCommands {
	public String name() {return "botcontrol";}
	public String info() {return "Allows basic bot control";}
	
	public void onEnable() {
		addCommand(new CommandJoin());
	}
}