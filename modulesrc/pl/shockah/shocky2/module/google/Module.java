package pl.shockah.shocky2.module.google;

public final class Module extends pl.shockah.shocky2.ModuleWithCommands {
	public String getName() {return "google";}
	public String getInfo() {return "Google (Image) search";}
	
	public void onEnable() {
		addCommands(new CommandGoogle(this),new CommandGoogleImages(this));
	}
}