package pl.shockah.shocky2.module.botcontrol;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.InviteEvent;
import pl.shockah.shocky2.Shocky;

public final class Module extends pl.shockah.shocky2.ModuleWithCommands {
	public String name() {return "botcontrol";}
	public String info() {return "Allows basic bot control";}
	protected boolean isListener() {return true;}
	protected boolean canDisable() {return false;}
	
	public void onEnable() {
		addCommand(new CommandJoin());
		addCommand(new CommandPart());
		addCommand(new CommandDie());
	}
	
	public void onInvite(InviteEvent<PircBotX> event) {
		Shocky.botManager.joinChannel(event.getChannel());
	}
}