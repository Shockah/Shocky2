package pl.shockah.shocky2.module.botcontrol;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.InviteEvent;
import pl.shockah.shocky2.Shocky;

public final class Module extends pl.shockah.shocky2.ModuleWithCommands {
	public String getName() {return "botcontrol";}
	public String getInfo() {return "Allows basic bot control";}
	protected boolean isListener() {return true;}
	public boolean canDisable() {return false;}
	
	public void onEnable() {
		addCommands(new CommandHelp(),new CommandJoin(),new CommandPart(),new CommandDie(),new CommandGet(),new CommandSet(),new CommandModule());
	}
	
	public void onInvite(InviteEvent<PircBotX> event) {
		Shocky.botManager.joinChannel(event.getChannel());
	}
}