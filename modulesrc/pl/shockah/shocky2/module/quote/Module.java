package pl.shockah.shocky2.module.quote;

public final class Module extends pl.shockah.shocky2.ModuleWithCommands {
	public String getName() {return "quote";}
	public String getInfo() {return "Quote system";}
	
	public void onEnable() {
		addCommands(new CommandQuote(this),new CommandQuoteAdd(this),new CommandQuoteDelete(this));
	}
}