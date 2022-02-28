package io.github.SkywolfDragon.eventHandlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;

import io.github.SkywolfDragon.enchWorkers.EnchHelper;
import io.github.SkywolfDragon.fileHelpers.GroupsHelper;
import io.github.SkywolfDragon.fileHelpers.PermissionGroup;


public class GroupsCommandHandler implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 1) return false;

		sender.sendMessage(ChatColor.LIGHT_PURPLE+"[Enchantment Management]"+ChatColor.YELLOW+" GROUPS");

		switch (args[0].toLowerCase()){
			case "help":
				if (args[args.length-1].equals("help")) {
					if (args.length >= 3 && args.length < 6) sender.sendMessage(ChatColor.GRAY+"yes, yes, i get it, you need help");
					if (args.length >= 6 && args.length < 9) sender.sendMessage(ChatColor.GRAY+"there's no need to be so desperate, calm down, please");
					if (args.length >= 9 && args.length < 12) sender.sendMessage(ChatColor.GRAY+"i'm just a plugin's command, i don't know how you expect me to help");
					if (args.length >= 12 && args.length < 15) sender.sendMessage(ChatColor.GRAY+"look, pal, my job is dealing with groups on your behalf, i can't do more than that");
					if (args.length >= 15) sender.sendMessage(ChatColor.RED+""+ChatColor.UNDERLINE+""+ChatColor.BOLD+"please stop.");
					if (args.length < 6) sender.sendMessage(ChatColor.GRAY+"This command can be used to add, remove, and change costgroups.");
					break;
				}
				switch (args[1].toLowerCase()) {
					case "reload":
						sender.sendMessage(ChatColor.GRAY+"Reloads the costgroups.yml file, run this command after any changes have been made to it.");
						break;
					case "list":
						sender.sendMessage(ChatColor.GRAY + "Gives you a list of the currently loaded groups.");
						break;
					case "get":
						sender.sendMessage(ChatColor.GRAY + "Tells you the value of a group's setting.");
						break;
					case "set":
						sender.sendMessage(ChatColor.GRAY + "Allows you to change the value of a group's setting. it won't save to the costgroups.yml.");
						break;
					case "unset":
						sender.sendMessage(ChatColor.GRAY + "Allows you to remove the value of a group's setting, for more information see fallback. it won't save to the costgroups.yml.");
						break;
					case "create":
						sender.sendMessage(ChatColor.GRAY + "Allows you to make a new group, new groups are created empty, use the setting subcommand to fill it. it won't save to the costgroups.yml.");
						break;
					case "remove":
						sender.sendMessage(ChatColor.GRAY + "Allows you to delete a group. it won't save to the costgroups.yml.");
						break;
					case "save":
						sender.sendMessage(ChatColor.GRAY + "This command saves the group data to the costgroups.yml, use after modifying groups through commands, or use reload to effectively cancel changes.");
						break;
					case "settings":
						if (args.length<3) {
							sender.sendMessage(ChatColor.GRAY + "The possible settings are:");
							sender.sendMessage(ChatColor.WHITE + " - add.");
							sender.sendMessage(ChatColor.WHITE + " - transfer.");
							sender.sendMessage(ChatColor.WHITE + " - remove.");
							sender.sendMessage(ChatColor.WHITE + " - use_money_instead_of_xp.");
							sender.sendMessage(ChatColor.WHITE + " - max_enchantment_levels.");
							sender.sendMessage(ChatColor.WHITE + " - enchantment_add_weights.");
							sender.sendMessage(ChatColor.WHITE + " - min_player_levels.");
						} else {
							switch(args[2].toLowerCase()){
								case "add":
									sender.sendMessage(ChatColor.WHITE + "add" + ChatColor.GRAY + ":");
									sender.sendMessage(ChatColor.GRAY + "The add setting contains subsettings that are used to calculate the price of adding an enchantment to an item.");
									sender.sendMessage(ChatColor.GRAY + "They are as follows:");
									sender.sendMessage(ChatColor.WHITE + " - cost:" + ChatColor.GRAY + " the base price.");
									sender.sendMessage(ChatColor.WHITE + " - cost_multiplier:" + ChatColor.GRAY + " a value to multiply the base cost for each enchantment (or enchantment level, see scaling_per_enchantment_level).");
									sender.sendMessage(ChatColor.WHITE + " - cost_adder:" + ChatColor.GRAY + " a value to add to the base cost for each enchantment (or enchantment level, see scaling_per_enchantment_level).");
									sender.sendMessage(ChatColor.WHITE + " - scaling_per_enchantment_level:" + ChatColor.GRAY + " a value that decides whether to count only the number of enchantments, or also their levels for calculating prices.");
									sender.sendMessage(ChatColor.GRAY + "The price of adding an enchantment is calculated with the formula: ");
									sender.sendMessage(ChatColor.WHITE + "(cost+(cost_adder*numberOfEnchantments))*(cost_multiplier^numberOfEnchantments)");
									sender.sendMessage(ChatColor.GRAY + "where numberOfEnchantments depends on cost_scaling_per_enchantment_level");
									break;
								case "remove":
									sender.sendMessage(ChatColor.WHITE + "remove" + ChatColor.GRAY + ":");
									sender.sendMessage(ChatColor.GRAY + "The remove setting contains subsettings that are used to calculate the price of removing an enchantment to an item.");
									sender.sendMessage(ChatColor.GRAY + "They are as follows:");
									sender.sendMessage(ChatColor.WHITE + " - cost:" + ChatColor.GRAY + " the base price.");
									sender.sendMessage(ChatColor.WHITE + " - cost_multiplier:" + ChatColor.GRAY + " a value to multiply the base cost for each enchantment (or enchantment level, see scaling_per_enchantment_level).");
									sender.sendMessage(ChatColor.WHITE + " - cost_adder:" + ChatColor.GRAY + " a value to add to the base cost for each enchantment (or enchantment level, see scaling_per_enchantment_level).");
									sender.sendMessage(ChatColor.WHITE + " - scaling_per_enchantment_level:" + ChatColor.GRAY + " a value that decides whether to count only the number of enchantments, or also their levels for calculating prices.");
									sender.sendMessage(ChatColor.GRAY + "The price of removing an enchantment is calculated with the formula: ");
									sender.sendMessage(ChatColor.WHITE + "(cost+(cost_adder*numberOfEnchantments))*(cost_multiplier^numberOfEnchantments)");
									sender.sendMessage(ChatColor.GRAY + "where numberOfEnchantments depends on cost_scaling_per_enchantment_level");
									break;
								case "transfer":
									sender.sendMessage(ChatColor.WHITE + "remove" + ChatColor.GRAY + ":");
									sender.sendMessage(ChatColor.GRAY + "The remove setting contains subsettings that are used to calculate the price of removing an enchantment to an item.");
									sender.sendMessage(ChatColor.GRAY + "They are as follows:");
									sender.sendMessage(ChatColor.WHITE + " - cost:" + ChatColor.GRAY + " the base price.");
									sender.sendMessage(ChatColor.WHITE + " - cost_multiplier:" + ChatColor.GRAY + " a value to multiply the base cost for each enchantment (or enchantment level, see scaling_per_enchantment_level).");
									sender.sendMessage(ChatColor.WHITE + " - cost_adder:" + ChatColor.GRAY + " a value to add to the base cost for each enchantment (or enchantment level, see scaling_per_enchantment_level).");
									sender.sendMessage(ChatColor.WHITE + " - scaling_per_enchantment_level:" + ChatColor.GRAY + " a value that decides whether to count only the number of enchantments, or also their levels for calculating prices.");
									sender.sendMessage(ChatColor.GRAY + "The price of transfering an enchantment is calculated with the formula: ");
									sender.sendMessage(ChatColor.WHITE + "(cost+(cost_adder*numberOfEnchantments))*(cost_multiplier^numberOfEnchantments)");
									sender.sendMessage(ChatColor.GRAY + "where numberOfEnchantments depends on cost_scaling_per_enchantment_level");
									break;
								case "use_money_instead_of_xp":
									sender.sendMessage(ChatColor.WHITE + "use_money_instead_of_xp" + ChatColor.GRAY + ":");
									sender.sendMessage(ChatColor.GRAY + "This setting dictates whether to try to use money from a vault-compatible plugin or just regular xp.");
									sender.sendMessage(ChatColor.GRAY + "For this setting to take effect the plugin needs to find a vault-compatible economy plugin.");
									sender.sendMessage(ChatColor.GRAY + "If one isn't found, xp will be used.");
									break;
								case "enchantment_add_weights":
									sender.sendMessage(ChatColor.WHITE + "enchantment_add_weights" + ChatColor.GRAY + ":");
									sender.sendMessage(ChatColor.GRAY + "A list of enchantments and their probabilities of encountering them with the add feature (relative to each other)");
									sender.sendMessage(ChatColor.GRAY + "That is, if all enchantments have ths same weight, no matter what that weight is, they're equally likely to be found.");
									sender.sendMessage(ChatColor.GRAY + "This setting is only taken into account when adding enchantments.");
									sender.sendMessage(ChatColor.GRAY + "A weight of 0 means that the enchantment can't be found, but can be transferred or removed.");
									sender.sendMessage(ChatColor.GRAY + "There's also a default field, if you don't feel like defining all the weights.");
									sender.sendMessage(ChatColor.GRAY + "The default field will prevent fallback, even if it's 0.");
									break;
								case "max_enchantment_levels":
									sender.sendMessage(ChatColor.WHITE + "max_enchantment_levels" + ChatColor.GRAY + ":");
									sender.sendMessage(ChatColor.GRAY + "A list of enchantments and the maximum levels they can reach with the use of this plugin.");
									sender.sendMessage(ChatColor.GRAY + "This setting prevents adding or transfering enchantments when they would pass this max level.");
									sender.sendMessage(ChatColor.GRAY + "A max level of 0 means it can't be added or transferred.");
									break;
								case "min_player_levels":
									sender.sendMessage(ChatColor.WHITE + "min_player_levels" + ChatColor.GRAY + ":");
									sender.sendMessage(ChatColor.GRAY + "This has three subsettings:");
									sender.sendMessage(ChatColor.WHITE + " - add:" + ChatColor.GRAY + " the minimum level a player needs to be in order to add the enchantment.");
									sender.sendMessage(ChatColor.WHITE + " - transfer:" + ChatColor.GRAY + " the minimum level to transfer.");
									sender.sendMessage(ChatColor.WHITE + " - remove:" + ChatColor.GRAY + " the minimum level to remove.");
									sender.sendMessage(ChatColor.GRAY + "If you don't specify a subsetting, the number will be applied to all of them.");
									sender.sendMessage(ChatColor.GRAY + "Negative levels mean up to, that is, an enchantment with an add min level of 8 means you can remove it at or above level 9.");
									sender.sendMessage(ChatColor.GRAY + "Negative levels mean up to, that is, an enchantment with a remove min level of -8 means you can't remove it at level 9, but can at level 8 and below.");
									sender.sendMessage(ChatColor.GRAY + "A min level of 0 means it can't be added, transferred or removed, respectively.");
									break;
							}
						}
						break;
					case "fallback":
						sender.sendMessage(ChatColor.WHITE + "Normal Fallback rules:");
						sender.sendMessage(ChatColor.GRAY + "If a player has multiple enchmanagement.costgroup.[groupname] permissions the plugin will use the earliest defined in this file with the needed value.");
						sender.sendMessage(ChatColor.GRAY + "if a group lacks a setting or settings the plugin will fall back to the next group the player is in,");
						sender.sendMessage(ChatColor.GRAY + "if there aren't any more applicable groups, the plugin will fall back on the settings defined in config.yml.");
						break;
				}
				break;
			case "reload":
				sender.sendMessage(ChatColor.GRAY + "Reloading groups.");
				GroupsHelper.restartGroups(sender);
				break;

			case "save":
				GroupsHelper.saveGroupsFile();
				sender.sendMessage(ChatColor.GREEN + "Configuration saved.");
				break;


			case "list":
				if (GroupsHelper.groups.size() > 0) {
					sender.sendMessage(ChatColor.YELLOW + "Groups" + ChatColor.GRAY + " loaded, in order of priority:");
					for (String g : GroupsHelper.groups.keySet()) {
						sender.sendMessage(ChatColor.WHITE + " - " + ChatColor.YELLOW + g);
					}
				} else {
					sender.sendMessage(ChatColor.DARK_RED + "No cost groups found in costgroups.yml");
				}
				break;


			case "get":
				if (args.length < 2 || args[1].isBlank()) {
					sender.sendMessage(ChatColor.GRAY + "Please specify a group.");
				}else
					if (GroupsHelper.groups.containsKey(args[1])) {
						if (args.length < 3) {
							sender.sendMessage(ChatColor.GRAY + "Please specify a setting.");
							break;
						}
						switch(args[2].toLowerCase()){
							case "use_money_instead_of_xp":
								if (GroupsHelper.groups.get(args[1]).useCash == null)
									sender.sendMessage(ChatColor.YELLOW + args[1] + ChatColor.GRAY + " does not have " +
											ChatColor.WHITE + "use_money_instead_of_xp" + ChatColor.GRAY + " set.");
								else
									sender.sendMessage(ChatColor.WHITE + "use_money_instead_of_xp" + ChatColor.GRAY + " in group " +
											ChatColor.YELLOW + args[1] + ChatColor.GRAY + " has the value: "+ChatColor.WHITE+GroupsHelper.groups.get(args[1]).useCash);
								break;
							case "add":
							case "transfer":
							case "remove":
								if (args.length < 4) {
									sender.sendMessage(ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GRAY + ", " + ChatColor.WHITE + "cost" + ChatColor.GRAY + " in group " +
											ChatColor.YELLOW + args[1] + ChatColor.GRAY + " has the value: " + ChatColor.WHITE + GroupsHelper.groups.get(args[1]).get(args[2].toLowerCase()).cost);
									sender.sendMessage(ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GRAY + ", " + ChatColor.WHITE + "cost_multiplier" + ChatColor.GRAY + " in group " +
											ChatColor.YELLOW + args[1] + ChatColor.GRAY + " has the value: " + ChatColor.WHITE + GroupsHelper.groups.get(args[1]).get(args[2].toLowerCase()).add);
									sender.sendMessage(ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GRAY + ", " + ChatColor.WHITE + "cost_adder" + ChatColor.GRAY + " in group " +
											ChatColor.YELLOW + args[1] + ChatColor.GRAY + " has the value: " + ChatColor.WHITE + GroupsHelper.groups.get(args[1]).get(args[2].toLowerCase()).mult);
									sender.sendMessage(ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GRAY + ", " + ChatColor.WHITE + "scaling_per_enchantment_level" + ChatColor.GRAY + " in group " +
											ChatColor.YELLOW + args[1] + ChatColor.GRAY + " has the value: " + ChatColor.WHITE + GroupsHelper.groups.get(args[1]).get(args[2].toLowerCase()).perLevel);
								} else {
									switch (args[3]) {
										case "cost":
											sender.sendMessage(ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GRAY + ", " + ChatColor.WHITE + "cost" + ChatColor.GRAY + " in group " +
													ChatColor.YELLOW + args[1] + ChatColor.GRAY + " has the value: " + ChatColor.WHITE + GroupsHelper.groups.get(args[1]).get(args[2].toLowerCase()).cost);
											break;
										case "cost_multiplier":
											sender.sendMessage(ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GRAY + ", " + ChatColor.WHITE + "cost_multiplier" + ChatColor.GRAY + " in group " +
													ChatColor.YELLOW + args[1] + ChatColor.GRAY + " has the value: " + ChatColor.WHITE + GroupsHelper.groups.get(args[1]).get(args[2].toLowerCase()).add);
											break;
										case "cost_adder":
											sender.sendMessage(ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GRAY + ", " + ChatColor.WHITE + "cost_adder" + ChatColor.GRAY + " in group " +
													ChatColor.YELLOW + args[1] + ChatColor.GRAY + " has the value: " + ChatColor.WHITE + GroupsHelper.groups.get(args[1]).get(args[2].toLowerCase()).mult);
											break;
										case "scaling_per_enchantment_level":
											sender.sendMessage(ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GRAY + ", " + ChatColor.WHITE + "scaling_per_enchantment_level" + ChatColor.GRAY + " in group " +
													ChatColor.YELLOW + args[1] + ChatColor.GRAY + " has the value: " + ChatColor.WHITE + GroupsHelper.groups.get(args[1]).get(args[2].toLowerCase()).perLevel);
											break;
									}
								}
								break;
							case "max_enchantment_levels":
								if (args.length < 4)
									for (Map.Entry<String, Integer> s: GroupsHelper.groups.get(args[1]).maxLevels.entrySet())
										sender.sendMessage(ChatColor.WHITE+" - "+ChatColor.DARK_PURPLE+s.getKey()+ChatColor.WHITE+": "+ChatColor.GRAY+s.getValue());
								else
									if (GroupsHelper.groups.get(args[1]).maxLevels.containsKey(NamespacedKey.fromString(args[3].toLowerCase()).toString()))
										sender.sendMessage(ChatColor.WHITE + args[3] + ChatColor.GRAY + "enchantment has a max level of "+ChatColor.WHITE+ GroupsHelper.groups.get(args[1]).maxLevels.get(NamespacedKey.fromString(args[3].toLowerCase()).toString())+ChatColor.GRAY+" for group " + ChatColor.YELLOW + args[1] + ChatColor.GRAY + ".");
									else
										if ((Enchantment.getByKey(NamespacedKey.fromString(args[3].toLowerCase()))) == null)
											sender.sendMessage(ChatColor.WHITE + args[3] + ChatColor.GRAY +" does not seem to be a valid enchantment.");
										else
											sender.sendMessage(ChatColor.WHITE + args[3] + ChatColor.GRAY +" enchantment isn't defined for group "+ChatColor.YELLOW+args[1]+ChatColor.GRAY+".");

								break;
							case "enchantment_add_weights":
								if (args.length < 4)
									for (Map.Entry<String, Integer> s : GroupsHelper.groups.get(args[1]).addWeights.entrySet())
										sender.sendMessage(ChatColor.WHITE + " - " + ChatColor.DARK_PURPLE + s.getKey() + ChatColor.WHITE + ": " + ChatColor.GRAY + s.getValue());
								else if (GroupsHelper.groups.get(args[1]).addWeights.containsKey(NamespacedKey.fromString(args[3].toLowerCase()).toString()))
									sender.sendMessage(ChatColor.WHITE + args[3] + ChatColor.GRAY + "enchantment has a max level of " + ChatColor.WHITE + GroupsHelper.groups.get(args[1]).addWeights.get(NamespacedKey.fromString(args[3].toLowerCase()).toString()) + ChatColor.GRAY + " for group " + ChatColor.YELLOW + args[1] + ChatColor.GRAY + ".");
								else if (args[3].equalsIgnoreCase("default"))
									sender.sendMessage(ChatColor.WHITE + args[1] + ChatColor.GRAY + " does not have a default weight.");
								else if ((Enchantment.getByKey(NamespacedKey.fromString(args[3].toLowerCase()))) == null)
									sender.sendMessage(ChatColor.WHITE + args[3] + ChatColor.GRAY + " does not seem to be a valid enchantment.");
								else
									sender.sendMessage(ChatColor.WHITE + args[3] + ChatColor.GRAY + " enchantment isn't defined for group " + ChatColor.YELLOW + args[1] + ChatColor.GRAY + ".");

								break;
							case "min_player_levels":
								if (args.length < 4)
									for (Map.Entry<String, HashMap<String, Integer>> s : GroupsHelper.groups.get(args[1]).minLevels.entrySet()) {
										sender.sendMessage(ChatColor.WHITE + " - " + ChatColor.DARK_PURPLE + s.getKey() + ChatColor.WHITE + ": ");
										for (Map.Entry<String, Integer> t : s.getValue().entrySet()) {
											sender.sendMessage(ChatColor.GRAY + "   - " + ChatColor.WHITE + t.getKey() + ChatColor.GRAY + ": " + ChatColor.WHITE + t.getValue());
										}
									}
								else if (GroupsHelper.groups.get(args[1]).minLevels.containsKey(NamespacedKey.fromString(args[3].toLowerCase()).toString())) {
									if (args.length < 5) {
										sender.sendMessage(ChatColor.WHITE + args[3] + ChatColor.GRAY + " in group " + ChatColor.YELLOW + args[1] + ChatColor.GRAY + " enchantment has min levels of: ");
										for (Map.Entry<String, Integer> s : GroupsHelper.groups.get(args[1]).minLevels.get(NamespacedKey.fromString(args[3].toLowerCase()).toString()).entrySet())
											sender.sendMessage(ChatColor.GRAY + " - " + ChatColor.WHITE + s.getKey() + ChatColor.GRAY + " for group " + ChatColor.YELLOW + args[1] + ChatColor.GRAY + ".");
									} else {
										if (GroupsHelper.groups.get(args[1]).minLevels.get(NamespacedKey.fromString(args[3].toLowerCase()).toString()).containsKey(args[4].toLowerCase()))
											sender.sendMessage(ChatColor.WHITE + args[4].toLowerCase() + ChatColor.GRAY + " for group " + ChatColor.YELLOW + args[1] + ChatColor.GRAY + " is " + GroupsHelper.groups.get(args[1]).minLevels.get(NamespacedKey.fromString(args[3].toLowerCase()).toString()).get(args[4].toLowerCase()) + ".");
										else
											sender.sendMessage(ChatColor.WHITE + args[4].toLowerCase() + ChatColor.GRAY + " isn't a recognized field.");
									}
								} else if ((Enchantment.getByKey(NamespacedKey.fromString(args[3].toLowerCase()))) == null)
									sender.sendMessage(ChatColor.WHITE + args[3] + ChatColor.GRAY + " does not seem to be a valid enchantment.");
								else
									sender.sendMessage(ChatColor.WHITE + args[3] + ChatColor.GRAY + " enchantment isn't defined in min_player_levels for group " + ChatColor.YELLOW + args[1] + ChatColor.GRAY + ".");

								break;
							default:
								sender.sendMessage(ChatColor.DARK_RED+"Couldn't recognize setting.");
								break;
						}
					} else {
					sender.sendMessage(ChatColor.DARK_RED + "Couldn't find group \""+args[1]+"\", group names are case sensitive");
				}
				break;

			case "set":
				if (args.length < 2 || args[1].isBlank()) {
					sender.sendMessage(ChatColor.GRAY + "Please specify a group.");
				} else if (GroupsHelper.groups.containsKey(args[1])) {
					if (args.length < 3) {
						sender.sendMessage(ChatColor.GRAY + "Please specify a setting to change.");
						break;
					}
					switch (args[2].toLowerCase()) {
						case "use_money_instead_of_xp":
							if (args.length < 4) {
								sender.sendMessage("Please specify a value to set use_money_instead_of_xp to.");
								break;
							}
							GroupsHelper.groups.get(args[1]).useCash = Boolean.parseBoolean(args[3]);
							sender.sendMessage(ChatColor.WHITE + "use_money_instead_of_xp" + ChatColor.GRAY + " for group " + ChatColor.YELLOW + args[1] +
									ChatColor.GRAY + " has been set to "+ ChatColor.WHITE + GroupsHelper.groups.get(args[1]).useCash + ChatColor.GRAY +
									", if you want this change to affect the costgroups.yml, use /enchmanagementgroups save.");
							break;
						case "add":
						case "transfer":
						case "remove":
							if (args.length < 4) {
								sender.sendMessage("Please specify a field to change.");
								break;
							} else if(args.length < 5) {
								sender.sendMessage("Please specify a value to set the field to.");
								break;
							} else {
								switch (args[3]) {
									case "cost":
										GroupsHelper.groups.get(args[1]).get(args[2].toLowerCase()).cost = Double.parseDouble(args[4]);
										sender.sendMessage(ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GRAY + ", " + ChatColor.WHITE + "cost" + ChatColor.GRAY + " in group " +
												ChatColor.YELLOW + args[1] + ChatColor.GRAY + " have been set to "+ChatColor.WHITE+GroupsHelper.groups.get(args[1]).get(args[2].toLowerCase()).cost+ ChatColor.GRAY+", if you want this change to affect the costgroups.yml, use /enchmanagementgroups save.");
										break;
									case "cost_multiplier":
										GroupsHelper.groups.get(args[1]).get(args[2].toLowerCase()).mult = Double.parseDouble(args[4]);
										sender.sendMessage(ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GRAY + ", " + ChatColor.WHITE + "cost_multiplier" + ChatColor.GRAY + " in group " +
												ChatColor.YELLOW + args[1] + ChatColor.GRAY + " have been set to "+ChatColor.WHITE+GroupsHelper.groups.get(args[1]).get(args[2].toLowerCase()).mult+ ChatColor.GRAY+", if you want this change to affect the costgroups.yml, use /enchmanagementgroups save.");
										break;
									case "cost_adder":
										GroupsHelper.groups.get(args[1]).get(args[2].toLowerCase()).add = Double.parseDouble(args[4]);
										sender.sendMessage(ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GRAY + ", " + ChatColor.WHITE + "cost_adder" + ChatColor.GRAY + " in group " +
												ChatColor.YELLOW + args[1] + ChatColor.GRAY + " have been set to "+ChatColor.WHITE+GroupsHelper.groups.get(args[1]).get(args[2].toLowerCase()).add+ ChatColor.GRAY+", if you want this change to affect the costgroups.yml, use /enchmanagementgroups save.");
										break;
									case "scaling_per_enchantment_level":
										GroupsHelper.groups.get(args[1]).get(args[2].toLowerCase()).perLevel = Boolean.parseBoolean(args[4]);
										sender.sendMessage(ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GRAY + ", " + ChatColor.WHITE + "scaling_per_enchantment_level" + ChatColor.GRAY + " in group " +
												ChatColor.YELLOW + args[1] + ChatColor.GRAY + " have been set to "+ChatColor.WHITE+GroupsHelper.groups.get(args[1]).get(args[2].toLowerCase()).perLevel+ ChatColor.GRAY+", if you want this change to affect the costgroups.yml, use /enchmanagementgroups save.");
										break;
								}
							}
							break;

						case "max_enchantment_levels":
							if (args.length < 4) {
								sender.sendMessage("Please specify an enchantment to change the max_enchantment_levels of.");
								break;
							} else if (args.length < 5) {
								sender.sendMessage("Please specify a value to set that enchantment's max level to.");
								break;
							}else if ((Enchantment.getByKey(NamespacedKey.fromString(args[3].toLowerCase()))) == null) {
								sender.sendMessage(ChatColor.WHITE + args[3] + ChatColor.GRAY + " does not seem to be a valid enchantment.");
							} else {
								GroupsHelper.groups.get(args[1]).maxLevels.put(NamespacedKey.fromString(args[3].toLowerCase()).toString(),Integer.parseInt(args[4]));
								sender.sendMessage(ChatColor.WHITE + args[3].toLowerCase() + ChatColor.GRAY + " in " + ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GRAY + " for group " + ChatColor.YELLOW + args[1] +
										ChatColor.GRAY + " have been set to "+GroupsHelper.groups.get(args[1]).maxLevels.get(NamespacedKey.fromString(args[3].toLowerCase()).toString())+
										", if you want this change to affect the costgroups.yml, use /enchmanagementgroups save.");
							}
							break;

						case "enchantment_add_weights":
							if (args.length < 4) {
								sender.sendMessage("Please specify an enchantment to change the weight of (or default).");
								break;
							} else if (args.length < 5) {
								sender.sendMessage("Please specify a value to set that enchantment's (or default's) weight to.");
								break;
							} else if (args[3].equalsIgnoreCase("default")) {
								GroupsHelper.groups.get(args[1]).addWeights.put("default", Integer.parseInt(args[4]));
								sender.sendMessage(ChatColor.WHITE + args[3].toLowerCase() + ChatColor.GRAY + " in " + ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GRAY + " for group " + ChatColor.YELLOW + args[1] +
										ChatColor.GRAY + " has been set to "+GroupsHelper.groups.get(args[1]).addWeights.get(NamespacedKey.fromString(args[3].toLowerCase()).toString())+
										", if you want this change to affect the costgroups.yml, use /enchmanagementgroups save.");
							} else if ((Enchantment.getByKey(NamespacedKey.fromString(args[3].toLowerCase()))) == null) {
								sender.sendMessage(ChatColor.WHITE + args[3] + ChatColor.GRAY + " does not seem to be a valid enchantment.");
							} else {
								GroupsHelper.groups.get(args[1]).addWeights.put(NamespacedKey.fromString(args[3].toLowerCase()).toString(),Integer.parseInt(args[4]));
								sender.sendMessage(ChatColor.WHITE + args[3].toLowerCase() + ChatColor.GRAY + " in " + ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GRAY + " for group " + ChatColor.YELLOW + args[1] +
										ChatColor.GRAY + " has been set to "+GroupsHelper.groups.get(args[1]).addWeights.get(NamespacedKey.fromString(args[3].toLowerCase()).toString())+
										", if you want this change to affect the costgroups.yml, use /enchmanagementgroups save.");
							}
							break;

						case "min_player_levels":
							if (args.length < 4) {
								sender.sendMessage("Please specify an enchantment and setting to change the min_player_levels of.");
								break;
							} else if ((Enchantment.getByKey(NamespacedKey.fromString(args[3].toLowerCase()))) == null) {
								sender.sendMessage(ChatColor.WHITE + args[3] + ChatColor.GRAY + " does not seem to be a valid enchantment.");
							} else if (args.length < 5) {
								if (Arrays.asList("add","transfer","remove").contains(args[4].toLowerCase())) {
									sender.sendMessage("Please specify a value to set that enchantment's min_player_levels to.");
								} else {
									GroupsHelper.groups.get(args[1]).minLevels.get(NamespacedKey.fromString(args[3].toLowerCase()).toString()).put("add",Integer.parseInt(args[4]));
									GroupsHelper.groups.get(args[1]).minLevels.get(NamespacedKey.fromString(args[3].toLowerCase()).toString()).put("transfer",Integer.parseInt(args[4]));
									GroupsHelper.groups.get(args[1]).minLevels.get(NamespacedKey.fromString(args[3].toLowerCase()).toString()).put("remove",Integer.parseInt(args[4]));
									sender.sendMessage(ChatColor.GRAY + "All minimum player levels for enchantment "+ChatColor.WHITE+args[3].toLowerCase()+ChatColor.GRAY+" for group " + ChatColor.YELLOW + args[1] +
											ChatColor.GRAY+" have been set to "+ChatColor.WHITE+GroupsHelper.groups.get(args[1]).minLevels.get(NamespacedKey.fromString(args[3].toLowerCase()).toString()).get("add")+ ChatColor.GRAY+
									", if you want this change to affect the costgroups.yml, use /enchmanagementgroups save.");
								}
								break;
							} else if (Arrays.asList("add","transfer","remove").contains(args[4].toLowerCase())) {
								GroupsHelper.groups.get(args[1]).minLevels.get(NamespacedKey.fromString(args[3].toLowerCase()).toString()).put(args[4].toLowerCase(),Integer.parseInt(args[4]));
									sender.sendMessage(ChatColor.GRAY+"The "+ChatColor.WHITE+args[4].toLowerCase()+ChatColor.GRAY + " minimum player level for enchantment "+ChatColor.WHITE+args[3].toLowerCase()+ChatColor.GRAY+" for group " + ChatColor.YELLOW + args[1] +
											ChatColor.GRAY+" have been set to "+ChatColor.WHITE+GroupsHelper.groups.get(args[1]).minLevels.get(NamespacedKey.fromString(args[3].toLowerCase()).toString()).get(args[4].toLowerCase())+ ChatColor.GRAY+
									", if you want this change to affect the costgroups.yml, use /enchmanagementgroups save.");

							} else {
								sender.sendMessage(ChatColor.GRAY + "Couldn't recognize " + ChatColor.WHITE + args[4].toLowerCase() + ChatColor.GRAY + " as a valid setting.");
							}
							break;

						default:
							sender.sendMessage(ChatColor.DARK_RED + "Couldn't recognize setting.");
							break;
					}
				} else {
					sender.sendMessage(ChatColor.DARK_RED + "Couldn't find group \"" + args[1] + "\", group names are case sensitive");
				}
				break;

			case "unset":
				if (args.length < 2 || args[1].isBlank()) {
					sender.sendMessage(ChatColor.GRAY + "Please specify a group.");
				} else if (GroupsHelper.groups.containsKey(args[1])) {
					if (args.length < 3) {
						sender.sendMessage(ChatColor.GRAY + "Please specify a setting, you can't clear a whole group in one command, this is intentional.");
						break;
					}
					switch (args[2].toLowerCase()) {
						case "use_money_instead_of_xp":
							GroupsHelper.groups.get(args[1]).useCash = null;
							sender.sendMessage(ChatColor.WHITE + "use_money_instead_of_xp" + ChatColor.GRAY + " for group " + ChatColor.YELLOW + args[1] +
									ChatColor.GRAY + " has been set to null, if you want this change to affect the costgroups.yml, use /enchmanagementgroups save.");
							break;
						case "add":
						case "transfer":
						case "remove":
							if (args.length < 4) {
								GroupsHelper.groups.get(args[1]).get(args[2].toLowerCase()).cost = null;
								GroupsHelper.groups.get(args[1]).get(args[2].toLowerCase()).mult = null;
								GroupsHelper.groups.get(args[1]).get(args[2].toLowerCase()).add = null;
								GroupsHelper.groups.get(args[1]).get(args[2].toLowerCase()).perLevel = null;
								sender.sendMessage(ChatColor.GRAY + "All subsettings in " + ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GRAY + " for group " + ChatColor.YELLOW + args[1] +
										ChatColor.GRAY + " have been set to null, if you want this change to affect the costgroups.yml, use /enchmanagementgroups save.");
							} else {
								switch (args[3]) {
									case "cost":
										GroupsHelper.groups.get(args[1]).get(args[2].toLowerCase()).cost = null;
										sender.sendMessage(ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GRAY + ", " + ChatColor.WHITE + "cost" + ChatColor.GRAY + " in group " +
												ChatColor.YELLOW + args[1] + ChatColor.GRAY + " have been set to null, if you want this change to affect the costgroups.yml, use /enchmanagementgroups save.");
										break;
									case "cost_multiplier":
										GroupsHelper.groups.get(args[1]).get(args[2].toLowerCase()).mult = null;
										sender.sendMessage(ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GRAY + ", " + ChatColor.WHITE + "cost_multiplier" + ChatColor.GRAY + " in group " +
												ChatColor.YELLOW + args[1] + ChatColor.GRAY + " have been set to null, if you want this change to affect the costgroups.yml, use /enchmanagementgroups save.");
										break;
									case "cost_adder":
										GroupsHelper.groups.get(args[1]).get(args[2].toLowerCase()).add = null;
										sender.sendMessage(ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GRAY + ", " + ChatColor.WHITE + "cost_adder" + ChatColor.GRAY + " in group " +
												ChatColor.YELLOW + args[1] + ChatColor.GRAY + " have been set to null, if you want this change to affect the costgroups.yml, use /enchmanagementgroups save.");
										break;
									case "scaling_per_enchantment_level":
										GroupsHelper.groups.get(args[1]).get(args[2].toLowerCase()).perLevel = null;
										sender.sendMessage(ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GRAY + ", " + ChatColor.WHITE + "scaling_per_enchantment_level" + ChatColor.GRAY + " in group " +
												ChatColor.YELLOW + args[1] + ChatColor.GRAY + " have been set to null, if you want this change to affect the costgroups.yml, use /enchmanagementgroups save.");
										break;
								}
							}
							break;
						case "max_enchantment_levels":
							if (args.length < 4) {
								GroupsHelper.groups.get(args[1]).maxLevels.clear();
								sender.sendMessage(ChatColor.GRAY + "All the " + ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GRAY + " for group " + ChatColor.YELLOW + args[1] +
										ChatColor.GRAY + " have been removed, if you want this change to affect the costgroups.yml, use /enchmanagementgroups save.");
							} else if (GroupsHelper.groups.get(args[1]).maxLevels.containsKey(NamespacedKey.fromString(args[3].toLowerCase()).toString())) {
								GroupsHelper.groups.get(args[1]).maxLevels.remove(NamespacedKey.fromString(args[3].toLowerCase()).toString());
								sender.sendMessage(ChatColor.WHITE + args[3].toLowerCase() + ChatColor.GRAY + " in " + ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GRAY + " for group " + ChatColor.YELLOW + args[1] +
										ChatColor.GRAY + " have been removed, if you want this change to affect the costgroups.yml, use /enchmanagementgroups save.");
							} else if ((Enchantment.getByKey(NamespacedKey.fromString(args[3].toLowerCase()))) == null) {
								sender.sendMessage(ChatColor.WHITE + args[3] + ChatColor.GRAY + " does not seem to be a valid enchantment.");
							} else {
								sender.sendMessage(ChatColor.WHITE + args[3] + ChatColor.GRAY + " enchantment isn't defined for group " + ChatColor.YELLOW + args[1] + ChatColor.GRAY + ", can't undefine it more.");
							}
							break;
						case "enchantment_add_weights":
							if (args.length < 4) {
								GroupsHelper.groups.get(args[1]).addWeights.clear();
								sender.sendMessage(ChatColor.GRAY + "All the " + ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GRAY + " for group " + ChatColor.YELLOW + args[1] +
										ChatColor.GRAY + " have been removed, if you want this change to affect the costgroups.yml, use /enchmanagementgroups save.");
							} else if (GroupsHelper.groups.get(args[1]).addWeights.containsKey(NamespacedKey.fromString(args[3].toLowerCase()).toString())) {
								GroupsHelper.groups.get(args[1]).addWeights.remove(NamespacedKey.fromString(args[3].toLowerCase()).toString());
								sender.sendMessage(ChatColor.WHITE + args[3].toLowerCase() + ChatColor.GRAY + " in " + ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GRAY + " for group " + ChatColor.YELLOW + args[1] +
										ChatColor.GRAY + " have been removed, if you want this change to affect the costgroups.yml, use /enchmanagementgroups save.");
							} else if (args[3].equalsIgnoreCase("default")) {
								sender.sendMessage(ChatColor.WHITE + args[1] + ChatColor.GRAY + " does not have a default weight defined.");
							} else if ((Enchantment.getByKey(NamespacedKey.fromString(args[3].toLowerCase()))) == null) {
								sender.sendMessage(ChatColor.WHITE + args[3] + ChatColor.GRAY + " does not seem to be a valid enchantment.");
							} else {
								sender.sendMessage(ChatColor.WHITE + args[3] + ChatColor.GRAY + " enchantment isn't defined for group " + ChatColor.YELLOW + args[1] + ChatColor.GRAY + ", can't undefine it more.");
							}
							break;
						case "min_player_levels":
							if (args.length < 4) {
								GroupsHelper.groups.get(args[1]).minLevels.clear();
								sender.sendMessage(ChatColor.GRAY + "All the " + ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GRAY + " for group " + ChatColor.YELLOW + args[1] +
										ChatColor.GRAY + " have been removed, if you want this change to affect the costgroups.yml, use /enchmanagementgroups save.");
							} else if (GroupsHelper.groups.get(args[1]).minLevels.containsKey(NamespacedKey.fromString(args[3].toLowerCase()).toString())) {
								if (args.length < 5) {
									GroupsHelper.groups.get(args[1]).minLevels.remove(NamespacedKey.fromString(args[3].toLowerCase()).toString());
									sender.sendMessage(ChatColor.GRAY + "All the " + ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GRAY + " for enchantment " + ChatColor.WHITE + args[3].toLowerCase() + ChatColor.GRAY + " for group " + ChatColor.YELLOW + args[1] +
											ChatColor.GRAY + " have been removed, if you want this change to affect the costgroups.yml, use /enchmanagementgroups save.");
								} else {
									if (GroupsHelper.groups.get(args[1]).minLevels.get(NamespacedKey.fromString(args[3].toLowerCase()).toString()).containsKey(args[4].toLowerCase())) {
										GroupsHelper.groups.get(args[1]).minLevels.get(NamespacedKey.fromString(args[3].toLowerCase()).toString()).remove(args[4].toLowerCase());
										sender.sendMessage(ChatColor.WHITE + args[2].toLowerCase() + ChatColor.GRAY + ", " + ChatColor.WHITE + args[4] + ChatColor.GRAY + " for enchantment " + ChatColor.WHITE + args[3].toLowerCase() + ChatColor.GRAY + " for group " + ChatColor.YELLOW + args[1] +
												ChatColor.GRAY + " has been removed, if you want this change to affect the costgroups.yml, use /enchmanagementgroups save.");
									} else
										sender.sendMessage(ChatColor.WHITE + args[4].toLowerCase() + ChatColor.GRAY + " isn't a recognized field.");
								}
							} else if ((Enchantment.getByKey(NamespacedKey.fromString(args[3].toLowerCase()))) == null)
								sender.sendMessage(ChatColor.WHITE + args[3] + ChatColor.GRAY + " does not seem to be a valid enchantment.");
							else
								sender.sendMessage(ChatColor.WHITE + args[3] + ChatColor.GRAY + " enchantment isn't defined in min_player_levels for group " + ChatColor.YELLOW + args[1] + ChatColor.GRAY + ".");

							break;
						default:
							sender.sendMessage(ChatColor.DARK_RED + "Couldn't recognize setting.");
							break;
					}
				} else {
					sender.sendMessage(ChatColor.DARK_RED + "Couldn't find group \"" + args[1] + "\", group names are case sensitive");
				}
				break;

			case "create":
				if (args.length < 2) {
					sender.sendMessage(ChatColor.GRAY+"Please specify a group name to create.");
					break;
				}
				if (GroupsHelper.groups.containsKey(args[1])) {
					sender.sendMessage(ChatColor.GRAY+"Group "+ChatColor.YELLOW+args[1]+ChatColor.GRAY+" already exists.");
					break;
				}
				GroupsHelper.groups.put(args[1],new PermissionGroup(args[1]));
				sender.sendMessage(ChatColor.GRAY+"Group "+ChatColor.YELLOW+args[1]+ChatColor.GRAY+" created, use /enchmanagementgroups set to configure it, remember to save changes with /enchmanagementgroups save.");
				break;
			case "remove":
				if (args.length < 2) {
					sender.sendMessage(ChatColor.GRAY+"Please specify a group name to remove.");
					break;
				}
				if (GroupsHelper.groups.containsKey(args[1])) {
					GroupsHelper.groups.remove(args[1]);
					sender.sendMessage(ChatColor.GRAY+"Group "+ChatColor.YELLOW+args[1]+ChatColor.GRAY+" deleted, remember to save changes with /enchmanagementgroups save.");
					break;
				}
				sender.sendMessage(ChatColor.GRAY+"Group "+ChatColor.YELLOW+args[1]+ChatColor.GRAY+" already doesn't exist.");
				break;
			default:
				return false;
		}

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length > 2)
			if (args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("help")) return List.of("help");
		
		if (args.length == 1) {
			return filterArgs(Arrays.asList("help","reload","list","get","set","unset","create","remove","save"), args[0]);
		} else if (args.length == 2) {
			switch (args[0].toLowerCase()) {
			case "help":
				return filterArgs(Arrays.asList("help","reload","list","get","set","unset","create","remove","save","settings", "fallback"),args[1]);
			case "get":
			case "set":
			case "unset":
			case "remove":
				return filterArgs(new ArrayList<String>(GroupsHelper.groups.keySet()), args[1]);
			case "create":
				return List.of("<group>");
			default:
				break;
			}
			
		} else if (args.length == 3) {
			switch (args[0].toLowerCase()) {
				case "help":
					if (!args[1].equalsIgnoreCase("settings"))
						return List.of("");
				case "get":
				case "set":
				case "unset":
					return filterArgs(Arrays.asList("add","transfer","remove","use_money_instead_of_xp","max_enchantment_levels","enchantment_add_weights","min_player_levels"),args[2]);
				default:
					break;
				}
			
		} else if (args.length == 4) {
			switch (args[0].toLowerCase()) {
				case "set":
					if (args[2].equalsIgnoreCase("use_money_instead_of_xp"))
						return filterArgs(Arrays.asList("true", "false"), args[3]);
				case "get":
				case "unset":
					switch(args[2].toLowerCase()) {
					case "add":
					case "remove":
					case "transfer":
						return filterArgs(Arrays.asList("cost", "cost_multiplier", "cost_adder", "scaling_per_enchantment_level"), args[3]);
					case "enchantment_add_weights":
						return filterArgs(EnchHelper.enchNamesWDefault, args[3]);
					case "max_enchantment_levels":
					case "min_player_levels":
						return filterArgs(EnchHelper.enchNames,args[3]);
					default:
						break;
					}
				default:
					break;
			}
		} else if (args.length == 5) {
			switch (args[0].toLowerCase()) {
			case "set":
				switch (args[2].toLowerCase()) {
					case "add":
					case "remove":
					case "transfer":
						switch (args[3].toLowerCase()){
							case "cost":
							case "cost_multiplier":
							case "cost_adder":
								return List.of("<double>");
							case "scaling_per_enchantment_level":
								return Arrays.asList("true","false");
						}
						return filterArgs(Arrays.asList("cost", "cost_multiplier", "cost_adder", "scaling_per_enchantment_level"), args[3]);
					case "enchantment_add_weights":
					case "max_enchantment_levels":
						return List.of("<int>");
					case "min_player_levels":
						return filterArgs(Arrays.asList("add","transfer","remove","<int>"), args[3]);
					default:
						break;
				}
			case "get":
			case "unset":
				if (args[2].equalsIgnoreCase("min_player_levels")) {
					return filterArgs(Arrays.asList("add", "transfer", "remove"), args[4]);
				}
			}
		} else if (args.length == 6) {
			if ("set".equalsIgnoreCase(args[3])) {
				if ("min_player_levels".equalsIgnoreCase(args[0])) {
					return List.of("<int>");
				}
			}
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
