package io.github.SkywolfDragon.eventHandlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.SkywolfDragon.EnchManagerPlugin;
import io.github.SkywolfDragon.enchWorkers.PayHelper;
import io.github.SkywolfDragon.fileHelpers.ConfigHelper;
import io.github.SkywolfDragon.fileHelpers.GroupsHelper;
import io.github.SkywolfDragon.fileHelpers.PermissionGroup;
import net.md_5.bungee.api.ChatColor;

public class CostCommandHandler implements CommandExecutor, TabCompleter  {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only players can use this command.");
			sender.sendMessage("You're not an instanceof org.bukkit.entity.Player.");
			return true;
		}
		
		sender.sendMessage(ChatColor.LIGHT_PURPLE+"[Enchantment Management]");
		if (ConfigHelper.getUsePermissions() && !sender.hasPermission("enchantmentmanagement.commands.cost")) {
			sender.sendMessage(ChatColor.DARK_RED+"You don't have permission to use this command.");
			return true;
		}
		
		if (args.length < 1) {
			tellAddPrice((Player)sender);
			tellTransPrice((Player)sender);
			tellRemPrice((Player)sender);
			return true;
		}
		switch (args[0].toLowerCase()) {
		case "help":
			if (args.length > 1) {
				//the command is help [something]
				switch(args[1].toLowerCase()) {
				case "help":
					if (args[args.length-1].equals("help")) {
						if (args.length >= 3 && args.length < 6) sender.sendMessage(ChatColor.GRAY+"yes, yes, i get it, you need help");
						if (args.length >= 6 && args.length < 9) sender.sendMessage(ChatColor.GRAY+"there's no need to be so desperate, calm down, please");
						if (args.length >= 9 && args.length < 12) sender.sendMessage(ChatColor.GRAY+"i'm just a plugin's command, i don't know how you expect me to help");
						if (args.length >= 12 && args.length < 15) sender.sendMessage(ChatColor.GRAY+"look, pal, i'm a command, i'm not even the plugin itself, i just hand out info. whatever you need help with, i just can't");
						if (args.length >= 15) sender.sendMessage(ChatColor.RED+""+ChatColor.UNDERLINE+""+ChatColor.BOLD+"please stop.");
						if (args.length < 6) sender.sendMessage(ChatColor.GRAY+"This command tells you information about the arguments of the command.");
						break;
					}
					return false;
				case "add":
					if (ConfigHelper.getFeature("add")) {
						sender.sendMessage(ChatColor.GRAY+"This will tell you the "+ChatColor.GOLD+"price"+ChatColor.GRAY+" of "+ChatColor.DARK_GREEN+"adding"+ChatColor.GRAY+" an enchantment to the "+ChatColor.AQUA+"item"+ChatColor.GRAY+" in your main hand (or off hand, if main is empty).");
					} else {
						sender.sendMessage(ChatColor.DARK_RED+"This feature has been deactivated.");
					}
					break;
				case "transfer":
					if (ConfigHelper.getFeature("transfer")) {
						sender.sendMessage(ChatColor.GRAY+"This will tell you the "+ChatColor.GOLD+"price"+ChatColor.GRAY+" of "+ChatColor.DARK_GREEN+"transfering"+ChatColor.GRAY+" an enchantment from the "+ChatColor.AQUA+"item"+ChatColor.GRAY+" in your main hand, to the "+ChatColor.AQUA+"item"+ChatColor.GRAY+" in your off hand.");
					} else {
						sender.sendMessage(ChatColor.DARK_RED+"This feature has been deactivated.");
					}
					break;
				case "remove":
					if (ConfigHelper.getFeature("remove")) {
						sender.sendMessage(ChatColor.GRAY+"This will tell you the "+ChatColor.GOLD+"price"+ChatColor.GRAY+" of "+ChatColor.DARK_GREEN+"removing"+ChatColor.GRAY+" an enchantment from the "+ChatColor.AQUA+"item"+ChatColor.GRAY+" in your main hand (or off hand, if main is empty).");
					} else {
						sender.sendMessage(ChatColor.DARK_RED+"This feature has been deactivated.");
					}
					break;
				case "groups":
					sender.sendMessage(ChatColor.GRAY+"This will tell you the "+ChatColor.YELLOW+"Cost Groups"+ChatColor.GRAY+" you are in");
					break;
				case "setup":
					if (args.length < 3) {
						sender.sendMessage(ChatColor.GRAY+"This will tell you the "+ChatColor.AQUA+"blocks"+ChatColor.GRAY+" you have to place to use this plugin.");
					} else {
						if (!ConfigHelper.getFeature(args[2])) {
							sender.sendMessage(ChatColor.DARK_RED+"This feature is unavailable.");
							break;
						}
						switch (args[2].toLowerCase()) {
						case "add":
							sender.sendMessage(ChatColor.GRAY+"To "+ChatColor.DARK_GREEN+"add"+ChatColor.GRAY+" an enchantment to a held "+ChatColor.AQUA+"item"+ChatColor.GRAY+":");
							String blockA = WordUtils.capitalizeFully(ConfigHelper.getBlock("add").toLowerCase().replace("minecraft:", "").replace("_", " "));
							String aanA = (blockA.startsWith("a")||blockA.startsWith("e")||blockA.startsWith("i")||blockA.startsWith("o"))? " an "+ChatColor.DARK_AQUA:" a "+ChatColor.DARK_AQUA;
							String endA = blockA.endsWith("block")? ChatColor.GRAY+"":ChatColor.GRAY+" block";
							sender.sendMessage(ChatColor.GRAY+"Place"+aanA+blockA+endA);
							break;
						case "transfer":
							sender.sendMessage(ChatColor.GRAY+"To "+ChatColor.DARK_GREEN+"transfer"+ChatColor.GRAY+" an enchantment from the "+ChatColor.AQUA+"item"+ChatColor.GRAY+" in your main hand to the "+ChatColor.AQUA+"item"+ChatColor.GRAY+" in your off hand:");
							String blockT = WordUtils.capitalizeFully(ConfigHelper.getBlock("transfer").toLowerCase().replace("minecraft:", "").replace("_", " "));
							String aanT = (blockT.startsWith("a")||blockT.startsWith("e")||blockT.startsWith("i")||blockT.startsWith("o"))? " an "+ChatColor.DARK_AQUA:" a "+ChatColor.DARK_AQUA;
							String endT = blockT.endsWith("block")? ChatColor.GRAY+"":ChatColor.GRAY+" block";
							sender.sendMessage(ChatColor.GRAY+"Place"+aanT+blockT+endT);
							break;
						case "remove":
							sender.sendMessage(ChatColor.GRAY+"To "+ChatColor.DARK_GREEN+"remove"+ChatColor.GRAY+" an enchantment from a held "+ChatColor.AQUA+"item"+ChatColor.GRAY+":");
							String blockR = WordUtils.capitalizeFully(ConfigHelper.getBlock("remove").toLowerCase().replace("minecraft:", "").replace("_", " "));
							String aanR = (blockR.startsWith("a")||blockR.startsWith("e")||blockR.startsWith("i")||blockR.startsWith("o"))? " an "+ChatColor.DARK_AQUA:" a "+ChatColor.DARK_AQUA;
							String endR = blockR.endsWith("block")? ChatColor.GRAY+"":ChatColor.GRAY+" block";
							sender.sendMessage(ChatColor.GRAY+"Place"+aanR+blockR+endR);
							break;
						default:
							return false;
						}
						String block = WordUtils.capitalizeFully(ConfigHelper.getBlock("common").toLowerCase().replace("minecraft:", "").replace("_", " "));
						String aan = (block.startsWith("a")||block.startsWith("e")||block.startsWith("i")||block.startsWith("o"))? " an "+ChatColor.DARK_AQUA:" a "+ChatColor.DARK_AQUA;
						String end = block.endsWith("block")? ChatColor.GRAY+".":ChatColor.GRAY+" block.";
						sender.sendMessage(ChatColor.GRAY+"on top of"+aan+block+end);
						String top = ConfigHelper.getClickCommon()? ChatColor.WHITE+"bottom"+ChatColor.GRAY:ChatColor.WHITE+"top"+ChatColor.GRAY;
						sender.sendMessage(ChatColor.GRAY+"then activate the "+top+" block.");
					}
					break;
				default:
					return false;
				}
			} else {
				//the command is just help
				sender.sendMessage(ChatColor.GRAY+"This command tells you the "+ChatColor.GOLD+"price"+ChatColor.GRAY+" of "+ChatColor.DARK_GREEN+"adding"+ChatColor.GRAY+", "+ChatColor.DARK_GREEN+"transfering"+ChatColor.GRAY+" or "+ChatColor.DARK_GREEN+"removing"+ChatColor.GRAY+" enchantments.");
				sender.sendMessage(ChatColor.GRAY+"You can specify an "+ChatColor.DARK_GREEN+"operation"+ChatColor.GRAY+", you'll get the "+ChatColor.GOLD+"price"+ChatColor.GRAY+" of that "+ChatColor.DARK_GREEN+"operation"+ChatColor.GRAY+" on the "+ChatColor.AQUA+"item"+ChatColor.GRAY+" in your main hand (or off hand if main is empty).");
				sender.sendMessage(ChatColor.GRAY+"If you specify nothing, you'll get the "+ChatColor.GOLD+"price"+ChatColor.GRAY+" of all the "+ChatColor.DARK_GREEN+"operations"+ChatColor.GRAY+" for that "+ChatColor.AQUA+"item"+ChatColor.GRAY+".");
			}
			return true;
			
		case "add":
			tellAddPrice((Player)sender);
			break;
		case "transfer":
			tellTransPrice((Player)sender);
			break;
		case "remove":
			tellRemPrice((Player)sender);
			break;
		case "groups":
			tellGroups((Player)sender);
			break;
		default:
			return false;
		}
		return true;
	}
	
	private void tellGroups(Player p) {
		
		ArrayList<String> tmp = new ArrayList<String>();
		for (PermissionGroup group:GroupsHelper.groups.values()) {
				if (p.hasPermission("enchantmentmanagement.costgroup."+group.name)) tmp.add(group.name);
		}
		
		if (tmp.size() > 0) {
			p.sendMessage(ChatColor.GRAY+"The loaded "+ChatColor.YELLOW+"Cost Groups"+ChatColor.GRAY+" you're currently in are, in order of priority:");
			for (String g:tmp) {
				p.sendMessage(ChatColor.BLACK+" - "+ChatColor.YELLOW+g);
			}
			return;
		}
		p.sendMessage(ChatColor.GRAY+"You're currently not in any loaded "+ChatColor.YELLOW+"Cost Groups"+ChatColor.GRAY+".");
		
	}

	private void tellAddPrice(Player p) {
		if (!ConfigHelper.getFeature("add")) {
			p.sendMessage(ChatColor.DARK_RED+"The "+ChatColor.DARK_GREEN+"add"+ChatColor.DARK_RED+" feature has been disabled.");
			return;
		}
		if (ConfigHelper.getUsePermissions() && !p.hasPermission("enchantmentmanagement.use.add")) {
			p.sendMessage(ChatColor.DARK_GRAY+"You don't have permission to "+ChatColor.DARK_GREEN+"add"+ChatColor.DARK_GRAY+" enchantments.");
			return;
		}
		if (p.getInventory().getItemInMainHand().getType().equals(Material.AIR) && p.getInventory().getItemInOffHand().getType().equals(Material.AIR)) {
			p.sendMessage(ChatColor.GRAY+"You don't have an "+ChatColor.AQUA+"item"+ChatColor.GRAY+" in either of your hands, can't calculate "+ChatColor.DARK_GREEN+"add "+ChatColor.GOLD+"price"+ChatColor.GRAY+".");
			return;
		}
		ItemStack item = p.getInventory().getItemInMainHand().getType().equals(Material.AIR)? p.getInventory().getItemInOffHand() : p.getInventory().getItemInMainHand();
		String hand = p.getInventory().getItemInMainHand().getType().equals(Material.AIR)? "off":"main";
		boolean cash = EnchManagerPlugin.vaultFound && GroupsHelper.getUseCash(p);
		double price = PayHelper.findPrice(p, item, "add");
		String block = WordUtils.capitalizeFully(item.getType().getKey().getKey().replace("_", " "));
		String money = cash? EnchManagerPlugin.econ.format(price):Long.toString(Math.round(Math.ceil(price)))+" xp";
		
		p.sendMessage(ChatColor.DARK_GREEN+"Adding"+ChatColor.GRAY+" an enchantment to the "+ChatColor.AQUA+""+block+ChatColor.GRAY+" in your "+hand+" hand would cost "+ChatColor.GOLD+money+ChatColor.GRAY+".");
	}
	
	private void tellTransPrice(Player p) {
		if (!ConfigHelper.getFeature("transfer")) {
			p.sendMessage(ChatColor.DARK_RED+"The "+ChatColor.DARK_GREEN+"transfer"+ChatColor.DARK_RED+" feature has been disabled.");
			return;
		}
		if (ConfigHelper.getUsePermissions() && !p.hasPermission("enchantmentmanagement.use.transfer")) {
			p.sendMessage(ChatColor.DARK_GRAY+"You don't have permission to "+ChatColor.DARK_GREEN+"transfer"+ChatColor.DARK_GRAY+" enchantments.");
			return;
		}
		ItemStack from = p.getInventory().getItemInMainHand();
		ItemStack to = p.getInventory().getItemInOffHand();
		if (from.getType().equals(Material.AIR)){
			p.sendMessage(ChatColor.GRAY+"You don't have an "+ChatColor.AQUA+"item"+ChatColor.GRAY+" in your main hand, can't calculate "+ChatColor.DARK_GREEN+"transfer "+ChatColor.GOLD+"price"+ChatColor.GRAY+".");
			return;
		}
		if (to.getType().equals(Material.AIR)){
			p.sendMessage(ChatColor.GRAY+"You don't have an "+ChatColor.AQUA+"item"+ChatColor.GRAY+" in your off hand, can't calculate "+ChatColor.DARK_GREEN+"transfer "+ChatColor.GOLD+"price"+ChatColor.GRAY+".");
			return;
		}
		boolean cash = EnchManagerPlugin.vaultFound && GroupsHelper.getUseCash(p);
		double price = PayHelper.findPrice(p, from, to);
		String fromName = WordUtils.capitalizeFully(from.getType().getKey().getKey().toLowerCase().replace("_", " "));
		String toName = WordUtils.capitalizeFully(to.getType().getKey().getKey().toLowerCase().replace("_", " "));
		String money = cash? EnchManagerPlugin.econ.format(price):Long.toString(Math.round(Math.ceil(price)))+" xp";
		
		p.sendMessage(ChatColor.GRAY+"Transfering an enchantment from the "+ChatColor.AQUA+fromName+ChatColor.GRAY+" in your main hand to the "+ChatColor.AQUA+toName+ChatColor.GRAY+" in your off hand would cost "+ChatColor.GOLD+money+ChatColor.GRAY+".");
	}
	
	private void tellRemPrice(Player p) {
		if (!ConfigHelper.getFeature("remove")) {
			p.sendMessage(ChatColor.DARK_RED+"The "+ChatColor.DARK_GREEN+"remove"+ChatColor.DARK_RED+" feature has been disabled.");
			return;
		}
		if (ConfigHelper.getUsePermissions() && !p.hasPermission("enchantmentmanagement.use.transfer")) {
			p.sendMessage(ChatColor.DARK_GRAY+"You don't have permission to "+ChatColor.DARK_GREEN+"remove"+ChatColor.DARK_GRAY+" enchantments.");
			return;
		}
		if (p.getInventory().getItemInMainHand().getType().equals(Material.AIR) && p.getInventory().getItemInOffHand().getType().equals(Material.AIR)) {
			p.sendMessage(ChatColor.GRAY+"You don't have an "+ChatColor.AQUA+"item"+ChatColor.GRAY+" in either of your hands, can't calculate "+ChatColor.DARK_GREEN+"remove "+ChatColor.GOLD+"price"+ChatColor.GRAY+".");
			return;
		}
		ItemStack item = p.getInventory().getItemInMainHand().getType().equals(Material.AIR)? p.getInventory().getItemInOffHand() : p.getInventory().getItemInMainHand();
		String hand = p.getInventory().getItemInMainHand().getType().equals(Material.AIR)? "off":"main";
		boolean cash = EnchManagerPlugin.vaultFound && GroupsHelper.getUseCash(p);
		double price = PayHelper.findPrice(p, item, "remove");
		String block = WordUtils.capitalizeFully(item.getType().getKey().getKey().replace("_", " "));
		String money = cash? EnchManagerPlugin.econ.format(price):Long.toString(Math.round(Math.ceil(price)))+" xp";
		
		p.sendMessage(ChatColor.GRAY+"Removing an enchantment from the "+ChatColor.AQUA+""+block+ChatColor.GRAY+" in your "+hand+" hand would cost "+ChatColor.GOLD+money+ChatColor.GRAY+".");
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1) {
			return filterArgs(Arrays.asList("add","transfer","remove","help","groups"), args[0]);
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("help"))
				return filterArgs(Arrays.asList("add","transfer","remove","setup","groups","help"),args[1]);
			else
				return List.of("");
			
		} else if (args.length > 2) {
			if (args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("help")) return List.of("help");
			if (args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("setup") && args.length == 3) return filterArgs(Arrays.asList("add","transfer","remove"), args[2]);
		}
		return List.of("");
	}
	
	private List<String> filterArgs(List<String> possibles, String text) {
		if (text.isBlank()) {
			return possibles;
		}
		ArrayList<String> l = new ArrayList<String>();
		for (String s:possibles) {
			if (s.toLowerCase().contains(text.toLowerCase())) {
				l.add(s);
			}
		}
		return l;
	}

}
