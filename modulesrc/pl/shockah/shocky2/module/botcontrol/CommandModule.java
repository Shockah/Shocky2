package pl.shockah.shocky2.module.botcontrol;

import java.util.ArrayList;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import pl.shockah.shocky2.Command;
import pl.shockah.shocky2.CommandCallback;
import pl.shockah.shocky2.ETarget;
import pl.shockah.shocky2.Module;
import pl.shockah.shocky2.smodule.login.LoginData;

public class CommandModule extends Command {
	public CommandModule(Module module) {
		super(module);
	}
	
	public String command() {return "module";}
	public String help() {
		StringBuilder sb = new StringBuilder();
		sb.append(".module [./channel] [on/off] - lists modules (. = for current channel)\n");
		sb.append(".module info {module} - shows info about module\n");
		sb.append(".module loadnew - loads new modules\n");
		sb.append(".module enable/on|disable/off [.] {module} - changes module's state (. = for current channel)\n");
		sb.append(".module unload/reload {module} - unloads/reloads module\n");
		sb.append(".module reloadall - reloads all modules");
		return sb.toString();
	}
	public void call(PircBotX bot, ETarget target, CommandCallback callback, Channel channel, User sender, String message) {
		if (callback.target != ETarget.Console) callback.target = ETarget.Notice;
		
		String[] split = message.split("\\s");
		LoginData ld = sender == null ? null : LoginData.getLoginData(sender.getNick());
		if (split.length == 1) {
			appendList(callback,null,true,true);
			return;
		} else if (split.length == 2) {
			if (split[1].equals(".")) {
				appendList(callback,channel.getName(),true,true);
				return;
			} else if (split[1].charAt(0) == '#') {
				appendList(callback,split[1],true,true);
				return;
			} else if (split[1].equalsIgnoreCase("on")) {
				appendList(callback,null,true,false);
				return;
			} else if (split[1].equalsIgnoreCase("off")) {
				appendList(callback,null,false,true);
				return;
			} else if (split[1].equalsIgnoreCase("loadnew")) {
				if (!ld.isController()) callback.append("Restricted command."); else {
					ArrayList<pl.shockah.shocky2.Module> modules = pl.shockah.shocky2.Module.loadNewModules();
					if (modules.isEmpty()) callback.append("No new modules."); else {
						callback.append("Loaded: ");
						for (int i = 0; i < modules.size(); i++) {
							if (i != 0) callback.append(", ");
							callback.append(modules.get(i).getName());
						}
					}
				}
				return;
			} else if (split[1].equalsIgnoreCase("reloadall")) {
				if (!ld.isController()) callback.append("Restricted command."); else {
					ArrayList<pl.shockah.shocky2.Module> failed = new ArrayList<pl.shockah.shocky2.Module>();
					for (pl.shockah.shocky2.Module module : pl.shockah.shocky2.Module.getModules()) if (!module.reload()) failed.add(module);
					if (failed.isEmpty()) callback.append("Reloaded all modules."); else {
						callback.append("Tried to reload, failed on: ");
						for (int i = 0; i < failed.size(); i++) {
							if (i != 0) callback.append(", ");
							callback.append(failed.get(i).getName());
						}
					}
				}
				return;
			}
		} else if (split.length == 3) {
			if (split[1].equals(".")) {
				if (split[2].equalsIgnoreCase("on")) {
					appendList(callback,channel.getName(),true,false);
				} else if (split[2].equalsIgnoreCase("off")) {
					appendList(callback,channel.getName(),false,true);
				}
				return;
			} else if (split[1].charAt(0) == '#') {
				if (split[2].equalsIgnoreCase("on")) {
					appendList(callback,split[1],true,false);
				} else if (split[2].equalsIgnoreCase("off")) {
					appendList(callback,split[1],false,true);
				}
				return;
			} else if (split[1].equalsIgnoreCase("info")) {
				pl.shockah.shocky2.Module module = pl.shockah.shocky2.Module.getModuleByName(split[2]);
				if (module == null) callback.append(module == null ? "No such module." : module.getInfo());
				return;
			} else if (split[1].equalsIgnoreCase("enable") || split[1].equalsIgnoreCase("on")) {
				if (!ld.isController()) callback.append("Restricted command."); else {
					pl.shockah.shocky2.Module module = pl.shockah.shocky2.Module.getModuleByName(split[2]);
					callback.append(module == null ? "No such module." : (module.enable(null) ? "Enabled." : "Failed."));
				}
				return;
			} else if (split[1].equalsIgnoreCase("disable") || split[1].equalsIgnoreCase("off")) {
				if (!ld.isController()) callback.append("Restricted command."); else {
					pl.shockah.shocky2.Module module = pl.shockah.shocky2.Module.getModuleByName(split[2]);
					callback.append(module == null ? "No such module." : (module.disable(null) ? "Enabled." : "Failed."));
				}
				return;
			} else if (split[1].equalsIgnoreCase("unload")) {
				if (!ld.isController()) callback.append("Restricted command."); else {
					pl.shockah.shocky2.Module module = pl.shockah.shocky2.Module.getModuleByName(split[2]);
					callback.append(module == null ? "No such module." : (!module.canDisable() ? "This module can't be disabled." : (module.unload() ? "Unloaded." : "Failed.")));
				}
				return;
			} else if (split[1].equalsIgnoreCase("reload")) {
				if (!ld.isController()) callback.append("Restricted command."); else {
					pl.shockah.shocky2.Module module = pl.shockah.shocky2.Module.getModuleByName(split[2]);
					callback.append(module == null ? "No such module." : (module.reload() ? "Reloaded." : "Failed."));
				}
				return;
			}
		} else if (split.length == 4) {
			if (split[1].equalsIgnoreCase("enable") || split[1].equalsIgnoreCase("on")) {
				String aChannel = split[2].equals(".") ? channel.getName() : split[2];
				if (!ld.isOp(aChannel)) callback.append("Restricted command."); else {
					pl.shockah.shocky2.Module module = pl.shockah.shocky2.Module.getModuleByName(split[3]);
					callback.append(module == null ? "No such module." : (module.enable(aChannel) ? "Enabled." : "Failed."));
				}
				return;
			} else if (split[1].equalsIgnoreCase("disable") || split[1].equalsIgnoreCase("off")) {
				String aChannel = split[2].equals(".") ? channel.getName() : split[2];
				if (!ld.isOp(aChannel)) callback.append("Restricted command."); else {
					pl.shockah.shocky2.Module module = pl.shockah.shocky2.Module.getModuleByName(split[3]);
					callback.append(module == null ? "No such module." : (module.disable(aChannel) ? "Disabled." : "Failed."));
				}
				return;
			}
		}
	}
	
	private void appendList(CommandCallback callback, String channel, boolean on, boolean off) {
		if (!on && !off) return;
		
		boolean first = true;
		for (pl.shockah.shocky2.Module module : pl.shockah.shocky2.Module.getModules()) {
			boolean enabled = module.isEnabled(null);
			if (enabled && on) {
				if (first) first = false; else callback.append(", ");
				if (on && off) callback.append(enabled ? Colors.DARK_GREEN : Colors.BROWN);
				callback.append(module.getName());
				if (on && off) callback.append("\017");
			}
		}
		
		if (first) callback.append("No modules.");
	}
}