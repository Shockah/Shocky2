package pl.shockah.shocky2.module.choose;

public final class Module extends pl.shockah.shocky2.ModuleWithCommands {
	public String getName() {return "choose";}
	public String getInfo() {return "Commands: .choose";}
	
	public void onEnable() {
		addCommands(new CommandChoose(this));
	}
}