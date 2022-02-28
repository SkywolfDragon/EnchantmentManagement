package io.github.SkywolfDragon.eventHandlers;

import io.github.SkywolfDragon.EnchManagerPlugin;
import io.github.SkywolfDragon.enchWorkers.EnchHelper;
import io.github.SkywolfDragon.fileHelpers.ConfigHelper;
import io.github.SkywolfDragon.fileHelpers.GroupsHelper;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;

import java.util.*;

public class ManageCommandHandler implements CommandExecutor, TabCompleter  {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(ChatColor.DARK_RED + "Not enough arguments.");
		} else
			sender.sendMessage(ChatColor.LIGHT_PURPLE+"[Enchantment Management]"+ChatColor.WHITE+" CONFIGURATION");
			switch (args[0].toLowerCase()) {
				case "help":
					//region help
					if (args.length < 2) {
						sender.sendMessage(ChatColor.GRAY + "With this command you can change the plugin's settings in game!");
						sender.sendMessage(ChatColor.GRAY + "Remember to use /enchmanagementconfig save to write any changes to the config.yml file.");
						sender.sendMessage(ChatColor.GRAY + "If you would like to reverse the changes, /enchmanagementconfig reload reads the config.yml file and loads the data in it. " +
								"Also useful when the file has been modified and the changes are to be loaded.");
					} else
						switch (args[1].toLowerCase()){
							case "help":
								if (args[args.length-1].equals("help")) {
									if (args.length >= 3 && args.length < 6) sender.sendMessage(ChatColor.GRAY+"yes, yes, i get it, you need help");
									if (args.length >= 6 && args.length < 9) sender.sendMessage(ChatColor.GRAY+"there's no need to be so desperate, calm down, please");
									if (args.length >= 9 && args.length < 12) sender.sendMessage(ChatColor.GRAY+"i'm just a plugin's command, i don't know how you expect me to help");
									if (args.length >= 12 && args.length < 15) sender.sendMessage(ChatColor.GRAY+"look, pal, my job is dealing with groups on your behalf, i can't do more than that");
									if (args.length >= 15) sender.sendMessage(ChatColor.RED+""+ChatColor.UNDERLINE+""+ChatColor.BOLD+"please stop.");
									if (args.length < 6) sender.sendMessage(ChatColor.GRAY+"This subcommand tells you information abut other subcommands");
									break;
								}
								break;
							case "reload":
								sender.sendMessage(ChatColor.GRAY+"This reloads the configuration from the config.yml file.");
								sender.sendMessage(ChatColor.GRAY+"It should be run when the file has been modified, or to undo any changes made with commands.");
								sender.sendMessage(ChatColor.GRAY+"It also rechecks for a vault-compatible economy plugin.");
								break;
							case "save":
								sender.sendMessage(ChatColor.GRAY+"This writes the currently loaded settings to the config.yml file.");
								sender.sendMessage(ChatColor.GRAY+"It should be run to commit to the file any changes mades with this command.");
								break;
							case "info":
								sender.sendMessage(ChatColor.GRAY+"This gives you a description of the effects of a given setting or group of settings.");
								break;
							case "get":
								sender.sendMessage(ChatColor.GRAY+"This tells you the current value of a setting.");
								break;
							case "set":
								sender.sendMessage(ChatColor.GRAY+"This allows you to set the value of a setting.");
								sender.sendMessage(ChatColor.GRAY+"Remember to use /enchmanagementconfig save to save the new settings to the config.yml file.");
								break;
							case "remove":
								sender.sendMessage(ChatColor.GRAY+"This allows you to remove an enchantment from enchantment_add_weights, max_enchantment_levels, custom_conflicts or min_player_levels.");
								sender.sendMessage(ChatColor.GRAY+"Remember to use /enchmanagementconfig save to save the new settings to the config.yml file.");
								break;
							default:
								sender.sendMessage(ChatColor.GRAY+"Setting invalid.");
								break;
						}
					break;
					//endregion
				case "reload":
					//region reload
					try {
						ConfigHelper.startUpConfiguration();
						sender.sendMessage(ChatColor.GRAY+"Configuration reloaded successfuly.");
					} catch (Exception e){
						sender.sendMessage(ChatColor.DARK_RED+"Couldn't reload config because:");
						sender.sendMessage(ChatColor.GRAY+e.getLocalizedMessage());
					}
					sender.sendMessage(ChatColor.GRAY+EnchManagerPlugin.PLUGIN_INSTANCE.checkForVault());
					break;
					//endregion
				case "save":
					//region save
					try {
						ConfigHelper.saveConfig();
						sender.sendMessage(ChatColor.GRAY+"Configuration saved successfuly.");
					} catch (Exception e){
						sender.sendMessage(ChatColor.DARK_RED+"Couldn't save config because:");
						sender.sendMessage(ChatColor.GRAY+e.getLocalizedMessage());
					}
					break;
					//endregion
				case "info":
					//region info
					if (args.length < 2)
						sender.sendMessage(ChatColor.GRAY+"Please be more specific.");
					else
						switch (args[1].toLowerCase()) {
							case "feature_active":
								if (args.length < 3)
									sender.sendMessage(ChatColor.GRAY+"In this group reside the settings that enable or disable features of the plugin.");
								else if (args.length == 3)
									switch (args[2]) {
										case "add":
											sender.sendMessage(ChatColor.DARK_GREEN + "feature_active.add");
											sender.sendMessage(ChatColor.GRAY + "Whether the add enchantment feature is usable, if false this feature will be completely disabled.");
											break;
										case "transfer":
											sender.sendMessage(ChatColor.DARK_GREEN + "feature_active.transfer");
											sender.sendMessage(ChatColor.GRAY + "Whether the transfer enchantment feature is usable, if false this feature will be completely disabled.");
											break;
										case "remove":
											sender.sendMessage(ChatColor.DARK_GREEN + "feature_active.remove");
											sender.sendMessage(ChatColor.GRAY + "Whether the remove enchantment feature is usable, if false this feature will be completely disabled.");
											break;
										default:
											sender.sendMessage(ChatColor.GRAY+"Setting invalid.");
											break;
									}
								else
									sender.sendMessage(ChatColor.GRAY+"Too many arguments.");

								break;
							case "costs":
								if (args.length < 3) {
									sender.sendMessage(ChatColor.GRAY + "The settings in this group dictate the default cost of actions within the plugin (see costgroups.yml for permission-dependant costs).");
									sender.sendMessage(ChatColor.GRAY + "Costs can be negative, that gives experience instead of taking away, negative multipliers will act funky, negative adders will substract.");
									sender.sendMessage(ChatColor.GRAY + "The formula for the total cost of an item is: " + ChatColor.WHITE + "(cost+(cost_adder*numberOfEnchantments))*(cost_multiplier^numberOfEnchantments)" + ChatColor.GRAY + " where numberOfEnchantments depends on cost_scaling_per_enchantment_level.");
								} else if (args.length == 3)
									switch (args[2]) {
										case "add":
											sender.sendMessage(ChatColor.DARK_GREEN + "costs.add");
											sender.sendMessage(ChatColor.GRAY + "The cost settings for adding enchantments.");
											break;
										case "transfer":
											sender.sendMessage(ChatColor.DARK_GREEN+"costs.transfer");
											sender.sendMessage(ChatColor.GRAY+"The cost settings for adding enchantments.");
											break;
										case "remove":
											sender.sendMessage(ChatColor.DARK_GREEN+"costs.remove");
											sender.sendMessage(ChatColor.GRAY+"The cost settings for adding enchantments.");
											break;
										case "use_money_instead_of_xp":
											sender.sendMessage(ChatColor.DARK_GREEN+"costs.use_money_instead_of_xp");
											sender.sendMessage(ChatColor.GRAY+"When true "+ChatColor.UNDERLINE+"and"+ChatColor.RESET+ChatColor.GRAY+" a vault-hooked economy plugin is detected, enchantments will be paid with money, otherwise they'll be paid with xp.");
											break;
										default:
											sender.sendMessage(ChatColor.GRAY+"Setting invalid.");
											break;

									}
								else  if (args.length == 4) {
									if (!(args[2].equalsIgnoreCase("add") || args[2].equalsIgnoreCase("remove") || args[2].equalsIgnoreCase("transfer"))) {
										sender.sendMessage(ChatColor.GRAY + "Too many arguments.");
										break;
									}
									switch (args[3].toLowerCase()) {
										case "cost":
											sender.sendMessage(ChatColor.DARK_GREEN + "costs." + args[2].toLowerCase() + ".cost");
											sender.sendMessage(ChatColor.GRAY + "The base cost for adding enchantments, a flat fee for every operation.");
											sender.sendMessage(ChatColor.GRAY + "The formula for the total cost of an item is: " + ChatColor.WHITE + "(cost+(cost_adder*numberOfEnchantments))*(cost_multiplier^numberOfEnchantments)");
											break;
										case "cost_multiplier":
											sender.sendMessage(ChatColor.DARK_GREEN + "costs." + args[2].toLowerCase() + ".cost_multiplier");
											sender.sendMessage(ChatColor.GRAY + "the multiplier that gets applied to the cost for each enchantment and level already in the item, set to 1.0 to disable this type of scaling.");
											sender.sendMessage(ChatColor.GRAY + "The formula for the total cost of an item is: " + ChatColor.WHITE + "(cost+(cost_adder*numberOfEnchantments))*(cost_multiplier^numberOfEnchantments)");
											break;
										case "cost_adder":
											sender.sendMessage(ChatColor.DARK_GREEN + "costs." + args[2].toLowerCase() + ".cost_adder");
											sender.sendMessage(ChatColor.GRAY + "How much gets added to the total cost for each enchantment and level that the item already has, set to 0.0 to disable this type of scaling.");
											sender.sendMessage(ChatColor.GRAY + "The formula for the total cost of an item is: " + ChatColor.WHITE + "(cost+(cost_adder*numberOfEnchantments))*(cost_multiplier^numberOfEnchantments)");
											break;
										case "scaling_per_enchantment_level":
											sender.sendMessage(ChatColor.DARK_GREEN + "costs." + args[2].toLowerCase() + ".scaling_per_enchantment_level");
											sender.sendMessage(ChatColor.GRAY + "This controls the numberOfEnchantments part of the formula, true means that every level of every enchantment will be counted, false only counts number of enchantments.");
											sender.sendMessage(ChatColor.GRAY + "The formula for the total cost of an item is: " + ChatColor.WHITE + "(cost+(cost_adder*numberOfEnchantments))*(cost_multiplier^numberOfEnchantments)");
											break;
										default:
											sender.sendMessage(ChatColor.GRAY+"Setting invalid.");
											break;
									}
								} else
									sender.sendMessage(ChatColor.GRAY+"Too many arguments.");
								break;
							case "interaction":
								if (args.length < 3)
									sender.sendMessage(ChatColor.GRAY+"Here be settings that define the player's interaction with the plugin.");
								else if (args.length == 3)
									switch (args[2].toLowerCase()) {
										case "use_permissions":
											sender.sendMessage(ChatColor.DARK_GREEN+"interaction.use_permissions");
											sender.sendMessage(ChatColor.GRAY+"When true the following permissions will be checked:");
											sender.sendMessage(ChatColor.WHITE+"  enchantmentmanagement.commands.cost"+ChatColor.GRAY+" for using /enchcost.");
											sender.sendMessage(ChatColor.WHITE+"  enchantmentmanagement.use.add"+ChatColor.GRAY+" for using the add feature.");
											sender.sendMessage(ChatColor.WHITE+"  enchantmentmanagement.use.transfer"+ChatColor.GRAY+" for using the transfer feature.");
											sender.sendMessage(ChatColor.WHITE+"  enchantmentmanagement.use.remove"+ChatColor.GRAY+" for using the remove feature.");
											sender.sendMessage(ChatColor.WHITE+"  enchantmentmanagement.use (or enchantmentmanagement.use.*)"+ChatColor.GRAY+" can be used to give all the use permissions.");
											sender.sendMessage(ChatColor.GRAY+"All other permissions are always checked.");
											break;
										case "tell_player":
											sender.sendMessage(ChatColor.DARK_GREEN+"interaction.tell_player");
											sender.sendMessage(ChatColor.GRAY+"When this is true, a player will be notified of the result of their interaction with this plugin.");
											sender.sendMessage(ChatColor.GRAY+"Not used for command outputs, those are safe.");
											break;
										case "click_common":
											sender.sendMessage(ChatColor.DARK_GREEN+"interaction.click_common");
											sender.sendMessage(ChatColor.GRAY+"When true you use the plugin by activating the common block(the one below), when false you use the block on top of the common.");
											sender.sendMessage(ChatColor.GRAY+"If set to false, it's recommended you set the cancel_interactions to true too.");
											break;
										case "main_hand_only":
											sender.sendMessage(ChatColor.DARK_GREEN+"interaction.main_hand_only");
											sender.sendMessage(ChatColor.GRAY+"Whether this plugin should check the off hand for all (except transfer) operations.");
											sender.sendMessage(ChatColor.GRAY+"When false, the plugin looks for an item in the main hand, then the off hand, when true only the main hand will be checked.");
											sender.sendMessage(ChatColor.GRAY+"Could help prevent accidental enchanting/disenchanting.");
											break;
										case "creative_free_enchantments":
											sender.sendMessage(ChatColor.DARK_GREEN+"interaction.creative_free_enchantments");
											sender.sendMessage(ChatColor.GRAY+"If operations are free of cost whilst the player's gamemode is creative, useful for fine-tuning weights and the such.");
											break;
										case "blocks":
											 sender.sendMessage(ChatColor.DARK_GREEN + "interaction.blocks");
											 sender.sendMessage(ChatColor.GRAY + "The blocks that can be used to interact with this plugin.");
											break;
										case "smart_enchantment_rolls":
											sender.sendMessage(ChatColor.DARK_GREEN+"interaction.smart_enchantment_rolls");
											sender.sendMessage(ChatColor.GRAY+"This makes it so that only valid enchantments for the item are in the pool of options to randomly pick from");
											sender.sendMessage(ChatColor.GRAY+"Disabling it might improve performance, but it will lead to the add feature being much less reliable");
											break;
										case "dumb_roll_tries":
											sender.sendMessage(ChatColor.DARK_GREEN+"interaction.dumb_roll_tries");
											sender.sendMessage(ChatColor.GRAY+"This value is only used when smart_enchantment_rolls is false");
											sender.sendMessage(ChatColor.GRAY+"It dictates the amount of times a random enchantment will be rolled before giving up");
											sender.sendMessage(ChatColor.GRAY+"If a valid enchantment isn't found before this limit is reached, the add operation will fail");
											sender.sendMessage(ChatColor.GRAY+"Higher values lead to the add feature being more reliable when smart_enchantment_rolls is false, but will affect performance");
											break;
										case "enchantment_add_weights":
											sender.sendMessage(ChatColor.DARK_GREEN + "interaction.enchantment_add_weights");
										 	sender.sendMessage(ChatColor.GRAY + "This section assigns a weight to each enchantment.");
										 	sender.sendMessage(ChatColor.GRAY + "A weight determines the chance an enchantment will appear through the add funcionality of this plugin.");
										 	sender.sendMessage(ChatColor.GRAY + "Weights must be integers, but can add up to any number (they're normalized).");
										 	sender.sendMessage(ChatColor.GRAY + "The default weight is used when an enchantment doesn't have another defined weight.");
										 	sender.sendMessage(ChatColor.GRAY + "An enchantment with a weight of 0 won't be added with the add functionality, but can be transferred.");
											sender.sendMessage(ChatColor.GRAY + "A 0 in the default means all enchantments not otherwise defined won't be added, but can be transferred.");
											break;
										default:
											sender.sendMessage(ChatColor.GRAY+"Setting invalid.");
											break;
									}
								else if (args.length == 4)
									if ("blocks".equals(args[2]))
										switch (args[3].toLowerCase()) {
											case "common":
												sender.sendMessage(ChatColor.DARK_GREEN + "interaction.blocks.common");
												sender.sendMessage(ChatColor.GRAY + "The blocck that goes below the other blocks in order for the plugin to know it's Interaction Time.");
												break;
											case "add":
												sender.sendMessage(ChatColor.DARK_GREEN + "interaction.blocks.add");
												sender.sendMessage(ChatColor.GRAY + "The block that sits on top of the common block for adding enchantments to the item in the player's main or off hand.");
												break;
											case "transfer":
												sender.sendMessage(ChatColor.DARK_GREEN + "interaction.blocks.transfer");
												sender.sendMessage(ChatColor.GRAY + "The block that sits on top of the common block for transfering enchantments from the item in the player's main hand to the item in the player's off hand.");
												sender.sendMessage(ChatColor.GRAY + "If activated with only one item, it will shuffle the enchantments on it for free (if the transfer feature is active).");
												break;
											case "remove":
												sender.sendMessage(ChatColor.DARK_GREEN + "interaction.blocks.remove");
												sender.sendMessage(ChatColor.GRAY + "The block that sits on top of the common block for removing enchantments from the item in the player's main or off hand.");
												break;
											default:
												sender.sendMessage(ChatColor.GRAY+"Setting invalid.");
												break;
										}
									else
										sender.sendMessage(ChatColor.GRAY+"Too many arguments.");
								else
									sender.sendMessage(ChatColor.GRAY+"Too many arguments.");
								break;
							case "limits":
								if (args.length < 3)
									sender.sendMessage(ChatColor.GRAY+"These settings dictate the limits for the enchantments.");
								else if (args.length == 3)
									switch (args[2].toLowerCase()) {
										case "safe_mode":
											sender.sendMessage(ChatColor.DARK_GREEN + "limits.safe_mode");
											sender.sendMessage(ChatColor.GRAY + "Only adds or transfers enchantments where compatible in vanilla minecraft " + ChatColor.DARK_GRAY + "(boring)" + ChatColor.GRAY + ", does not check the enchantment's level or enchantment conflicts.");
											sender.sendMessage(ChatColor.GRAY + "For example, sharpness won't be added to a pickaxe if this is true.");
											break;
										case "enforce_vanilla_conflicts":
											sender.sendMessage(ChatColor.DARK_GREEN + "limits.enforce_vanilla_conflicts");
											sender.sendMessage(ChatColor.GRAY + "when true, conflicting enchantments won't be added into the same item (example: silk touch conflicts with fortune), doesn't affect custom_conflicts.");
											break;
										case "vanilla_level_limits":
											sender.sendMessage(ChatColor.DARK_GREEN + "limits.vanilla_level_limits");
											sender.sendMessage(ChatColor.GRAY + "Ef the levels of enchantments should be limited to their vanilla minecraft maximum levels " + ChatColor.DARK_GRAY + "(boring)" + ChatColor.GRAY + ".");
											sender.sendMessage(ChatColor.GRAY + "Doesn't affect max_enchantment_levels if the level set in that section is less than vanilla minecraft's.");
											break;
										case "prevent_dupe":
											sender.sendMessage(ChatColor.DARK_GREEN + "limits.prevent_dupe");
											sender.sendMessage(ChatColor.GRAY + "Prevents the use of more than one item in a stack when adding, removing, or transfering enchantments.");
											break;
										case "one_level_at_a_time":
											sender.sendMessage(ChatColor.DARK_GREEN + "limits.one_level_at_a_time");
											sender.sendMessage(ChatColor.GRAY + "When true, enchantments will be removed/transfered one level at a time instead of all levels at once.");
											sender.sendMessage(ChatColor.GRAY + "When false, enchantments will be removed/transfered one whole enchantment, with all its levels, at a time.");
											break;
										case "max_enchantment_levels":
											sender.sendMessage(ChatColor.DARK_GREEN + "limits.max_enchantment_levels");
											sender.sendMessage(ChatColor.GRAY + "Here you can set the max level for any enchantment, one enchantment per line, this list will be respected even if safe_mode and limit_levels is disabled.");
											sender.sendMessage(ChatColor.GRAY + "Setting a max level of 0 will prevent that enchantment from being added or transfered (though not removed).");
											break;
										case "custom_conflicts":
											sender.sendMessage(ChatColor.DARK_GREEN + "limits.custom_conflicts");
											sender.sendMessage(ChatColor.GRAY + "Here you can set pairs (or groups) of enchantments that conflict with each other (won't be placed in the same item together), this will be respected even if enforce_vanilla_conflicts is false.");
											sender.sendMessage(ChatColor.GRAY + "The conflicts work both ways, there's no need to define both conflicts.");
											sender.sendMessage(ChatColor.GRAY + "Groups also work both ways, but they only do so between the base enchantment for that group and each sub-enchantment one, that is, sub-enchantments defined in a group don't conflict with each other, but do conflict with the base enchantment for that group.");
											break;
										case "min_player_levels":
											sender.sendMessage(ChatColor.DARK_GREEN + "limits.min_player_levels");
											sender.sendMessage(ChatColor.GRAY + "Here you can set the minimum experience level for a player to add/transfer/remove an enchantment through this plugin.");
											sender.sendMessage(ChatColor.GRAY + "A " + ChatColor.WHITE + "positive" + ChatColor.GRAY + " number means you can only manipulate it AT OR AFTER that level.");
											sender.sendMessage(ChatColor.GRAY + "A " + ChatColor.WHITE + "negative" + ChatColor.GRAY + " number means you can only manipulate it BEFORE OR AT that level.");
											sender.sendMessage(ChatColor.GRAY + "A 0 here means the enchantment won't be added/transfered/removed, depending on what subsections the entry has.");
											sender.sendMessage(ChatColor.GRAY + "A positive number means you can only manipulate it AT OR AFTER that level.");
											break;
										default:
											sender.sendMessage(ChatColor.GRAY+"Setting invalid.");
											break;
									}
								else
									sender.sendMessage(ChatColor.GRAY+"Too many arguments.");
								break;
							case "event_cancelling":
								if (args.length < 3)
									sender.sendMessage(ChatColor.GRAY+"Whether the plugin tries to cancel the normal block interaction in favor of this plugin's.");
								else if (args.length == 3)
									switch (args[2].toLowerCase()) {
										case "cancel_interaction":
											sender.sendMessage(ChatColor.DARK_GREEN + "event_cancelling.cancel_interaction");
											sender.sendMessage(ChatColor.GRAY + "Events (clicking the block) are canceled if the interaction with this plugin is possible.");
											sender.sendMessage(ChatColor.GRAY + "That is, events are not canceled if the player doesn't have permission or the feature is disabled.");
											break;
										case "force_cancel_interaction":
											sender.sendMessage(ChatColor.DARK_GREEN + "event_cancelling.force_cancel_interaction");
											sender.sendMessage(ChatColor.GRAY + "Events (clicking the block) are cancelled when the functional block is on top of the common one, regardless of use ability.");
											sender.sendMessage(ChatColor.GRAY + "Events are not canceled if feature is disabled.");
											break;
										case "super_ultra_force_cancel_interaction_absolutely_for_sure":
											sender.sendMessage(ChatColor.DARK_GREEN + "event_cancelling.super_ultra_force_cancel_interaction_absolutely_for_sure");
											sender.sendMessage(ChatColor.GRAY + "Events (clicking the block) are cancelled UNDER ALL CIRCUMSTANCES, this includes placing on these blocks (but not shift_placing).");
											sender.sendMessage(ChatColor.DARK_RED + "Will cancel even if the corresponding feature is disabled, USE WITH CARE.");
											break;
										default:
											sender.sendMessage(ChatColor.GRAY+"Setting invalid.");
											break;
									}
								else
									sender.sendMessage(ChatColor.GRAY+"Too many arguments.");
								break;
						}
					break;
					//endregion
				case "get":
					//region get
					if (args.length < 2)
						sender.sendMessage(ChatColor.GRAY+"Please be more specific.");
					else
						switch (args[1].toLowerCase()) {
							case "feature_active":
								if (args.length < 3) {
									sender.sendMessage(ChatColor.DARK_GREEN + "feature_active.add" + ChatColor.GRAY + ": " + ConfigHelper.getFeature("add"));
									sender.sendMessage(ChatColor.DARK_GREEN + "feature_active.transfer" + ChatColor.GRAY + ": " + ConfigHelper.getFeature("transfer"));
									sender.sendMessage(ChatColor.DARK_GREEN + "feature_active.remove" + ChatColor.GRAY + ": " + ConfigHelper.getFeature("remove"));
								} else if (args.length == 3)
									switch (args[2]) {
										case "add":
											sender.sendMessage(ChatColor.DARK_GREEN+"feature_active.add"+ChatColor.GRAY+": "+ConfigHelper.getFeature("add"));
											break;
										case "transfer":
											sender.sendMessage(ChatColor.DARK_GREEN+"feature_active.transfer"+ChatColor.GRAY+": "+ConfigHelper.getFeature("transfer"));
											break;
										case "remove":
											sender.sendMessage(ChatColor.DARK_GREEN+"feature_active.remove"+ChatColor.GRAY+": "+ConfigHelper.getFeature("remove"));
											break;
										default:
											sender.sendMessage(ChatColor.GRAY+"Setting invalid.");
									}
								else
									sender.sendMessage(ChatColor.GRAY+"Too many arguments.");
								break;
							case "costs":
								if (args.length < 3)
									sender.sendMessage(ChatColor.GRAY + "Please be more specific.");
								else if (args.length == 3)
									switch (args[2]) {
										case "add":
											sender.sendMessage(ChatColor.DARK_GREEN +"costs.add.cost"+ChatColor.GRAY+": "+GroupsHelper.defaultGroup.add.cost);
											sender.sendMessage(ChatColor.DARK_GREEN +"costs.add.cost_multiplier"+ChatColor.GRAY+": "+GroupsHelper.defaultGroup.add.mult);
											sender.sendMessage(ChatColor.DARK_GREEN +"costs.add.cost_adder"+ChatColor.GRAY+": "+GroupsHelper.defaultGroup.add.add);
											sender.sendMessage(ChatColor.DARK_GREEN +"costs.add.scaling_per_enchantment_level"+ChatColor.GRAY+": "+GroupsHelper.defaultGroup.add.perLevel);
											break;
										case "transfer":
											sender.sendMessage(ChatColor.DARK_GREEN +"costs.transfer.cost"+ChatColor.GRAY+": "+GroupsHelper.defaultGroup.tra.cost);
											sender.sendMessage(ChatColor.DARK_GREEN +"costs.transfer.cost_multiplier"+ChatColor.GRAY+": "+GroupsHelper.defaultGroup.tra.mult);
											sender.sendMessage(ChatColor.DARK_GREEN +"costs.transfer.cost_adder"+ChatColor.GRAY+": "+GroupsHelper.defaultGroup.tra.add);
											sender.sendMessage(ChatColor.DARK_GREEN +"costs.transfer.scaling_per_enchantment_level"+ChatColor.GRAY+": "+GroupsHelper.defaultGroup.tra.perLevel);
											break;
										case "remove":
											sender.sendMessage(ChatColor.DARK_GREEN +"costs.remove.cost"+ChatColor.GRAY+": "+GroupsHelper.defaultGroup.rem.cost);
											sender.sendMessage(ChatColor.DARK_GREEN +"costs.remove.cost_multiplier"+ChatColor.GRAY+": "+GroupsHelper.defaultGroup.rem.mult);
											sender.sendMessage(ChatColor.DARK_GREEN +"costs.remove.cost_adder"+ChatColor.GRAY+": "+GroupsHelper.defaultGroup.rem.add);
											sender.sendMessage(ChatColor.DARK_GREEN +"costs.remove.scaling_per_enchantment_level"+ChatColor.GRAY+": "+GroupsHelper.defaultGroup.rem.perLevel);
											break;
										case "use_money_instead_of_xp":
											sender.sendMessage(ChatColor.DARK_GREEN+"costs.use_money_instead_of_xp"+ChatColor.GRAY+": "+GroupsHelper.defaultGroup.useCash);
											break;
										default:
											sender.sendMessage(ChatColor.GRAY+"Setting invalid.");
									}
								else if (args.length == 4) {
									if (!(args[2].equalsIgnoreCase("add") || args[2].equalsIgnoreCase("remove") || args[2].equalsIgnoreCase("transfer"))) {
										sender.sendMessage(ChatColor.GRAY + "Too many arguments.");
										break;
									}
									switch (args[3].toLowerCase()) {
										case "cost":
											sender.sendMessage(ChatColor.DARK_GREEN + "costs." + args[2].toLowerCase() + ".cost" + ChatColor.GRAY + ": " + GroupsHelper.defaultGroup.get(args[2].toLowerCase()).cost);
											break;
										case "cost_multiplier":
											sender.sendMessage(ChatColor.DARK_GREEN + "costs." + args[2].toLowerCase() + ".cost_multiplier" + ChatColor.GRAY + ": " + GroupsHelper.defaultGroup.get(args[2].toLowerCase()).mult);
											break;
										case "cost_adder":
											sender.sendMessage(ChatColor.DARK_GREEN + "costs." + args[2].toLowerCase() + ".cost_adder" + ChatColor.GRAY + ": " + GroupsHelper.defaultGroup.get(args[2].toLowerCase()).add);
											break;
										case "scaling_per_enchantment_level":
											sender.sendMessage(ChatColor.DARK_GREEN + "costs." + args[2].toLowerCase() + ".scaling_per_enchantment_level" + ChatColor.GRAY + ": " + GroupsHelper.defaultGroup.get(args[2].toLowerCase()).perLevel);
											break;
										default:
											sender.sendMessage(ChatColor.GRAY+"Setting invalid.");
									}
								} else
									sender.sendMessage(ChatColor.GRAY+"Too many arguments.");
								break;
							case "interaction":
								if (args.length < 3)
									sender.sendMessage(ChatColor.GRAY+"Please be more specific.");
								else if (args.length == 3)
									switch (args[2].toLowerCase()) {
										case "use_permissions":
											sender.sendMessage(ChatColor.DARK_GREEN+"interaction.use_permissions"+ChatColor.GRAY+": "+ConfigHelper.getUsePermissions());
											break;
										case "tell_player":
											sender.sendMessage(ChatColor.DARK_GREEN+"interaction.tell_player"+ChatColor.GRAY+": "+ConfigHelper.getVerbose());
											break;
										case "click_common":
											sender.sendMessage(ChatColor.DARK_GREEN+"interaction.click_common"+ChatColor.GRAY+": "+ConfigHelper.getClickCommon());
											break;
										case "main_hand_only":
											sender.sendMessage(ChatColor.DARK_GREEN+"interaction.main_hand_only"+ChatColor.GRAY+": "+ConfigHelper.getMainHandOnly());
											break;
										case "creative_free_enchantments":
											sender.sendMessage(ChatColor.DARK_GREEN+"interaction.creative_free_enchantments"+ChatColor.GRAY+": "+ConfigHelper.getCreativeFree());
											break;
										case "blocks":
											sender.sendMessage(ChatColor.DARK_GREEN+"interaction.blocks.common"+ChatColor.GRAY+": "+ConfigHelper.getBlock("common"));
											sender.sendMessage(ChatColor.DARK_GREEN+"interaction.blocks.add"+ChatColor.GRAY+": "+ConfigHelper.getBlock("add"));
											sender.sendMessage(ChatColor.DARK_GREEN+"interaction.blocks.transfer"+ChatColor.GRAY+": "+ConfigHelper.getBlock("transfer"));
											sender.sendMessage(ChatColor.DARK_GREEN+"interaction.blocks.remove"+ChatColor.GRAY+": "+ConfigHelper.getBlock("remove"));
											break;
										case "smart_enchantment_rolls":
											sender.sendMessage(ChatColor.DARK_GREEN+"interaction.smart_enchantment_rolls"+ChatColor.GRAY+": "+ConfigHelper.rollSmart());
											break;
										case "dumb_roll_tries":
											sender.sendMessage(ChatColor.DARK_GREEN+"interaction.dumb_roll_tries"+ChatColor.GRAY+": "+ConfigHelper.rollTries());
											break;
										case "enchantment_add_weights":
											sender.sendMessage(ChatColor.DARK_GREEN+"interaction.enchantment_add_weights.default"+ChatColor.GRAY+": "+GroupsHelper.defaultGroup.defaultWeight);
											sender.sendMessage(ChatColor.DARK_GREEN+"interaction.enchantment_add_weights"+ChatColor.GRAY+": ");
											for (Map.Entry<String, Integer> s: GroupsHelper.defaultGroup.addWeights.entrySet())
												sender.sendMessage(ChatColor.GRAY+" - "+ChatColor.DARK_GREEN+s.getKey()+ChatColor.GRAY+": "+ChatColor.WHITE+s.getValue());
											break;
										default:
											sender.sendMessage(ChatColor.GRAY+"Setting invalid.");
									}
								else if (args.length == 4)
									if ("blocks".equalsIgnoreCase(args[2]))
										switch (args[3].toLowerCase()){
											case "common":
											case "add":
											case "transfer":
											case "remove":
												sender.sendMessage(ChatColor.DARK_GREEN+"interaction.blocks."+args[3].toLowerCase()+ChatColor.GRAY+": "+ConfigHelper.getBlock(args[3].toLowerCase()));
												break;
											default:
												sender.sendMessage(ChatColor.GRAY+"Setting invalid.");
										 }
									else if ("enchantment_add_weights".equalsIgnoreCase(args[2]))
										if (GroupsHelper.defaultGroup.addWeights.containsKey(NamespacedKey.fromString(args[3]).toString()))
											sender.sendMessage(ChatColor.DARK_GREEN+"interaction.enchantment_add_weights."+NamespacedKey.fromString(args[3]).toString()+ChatColor.GRAY+": "+GroupsHelper.defaultGroup.addWeights.get(NamespacedKey.fromString(args[3]).toString()));
										else if (args[3].equalsIgnoreCase("default"))
											sender.sendMessage(ChatColor.DARK_GREEN+"interaction.enchantment_add_weights.default"+ChatColor.GRAY+": "+GroupsHelper.defaultGroup.defaultWeight);
										else if (Enchantment.getByKey(NamespacedKey.fromString(args[3].toLowerCase())) == null)
											sender.sendMessage(ChatColor.GRAY+"Enchantment invalid.");
										else
											sender.sendMessage(ChatColor.GRAY+"Enchantment undefined, default value will be used.");
									else
										sender.sendMessage(ChatColor.GRAY+"Too many arguments.");
								else
									sender.sendMessage(ChatColor.GRAY+"Too many arguments.");
								break;
							case "limits":
								if (args.length < 3)
									sender.sendMessage(ChatColor.GRAY+"Please be more specific.");
								else if (args.length == 3)
									switch (args[2].toLowerCase()) {
										case "safe_mode":
											sender.sendMessage(ChatColor.DARK_GREEN + "limits.safe_mode"+ChatColor.GRAY+": "+ConfigHelper.getLimit("safe_mode"));
											break;
										case "enforce_vanilla_conflicts":
											sender.sendMessage(ChatColor.DARK_GREEN + "limits.enforce_vanilla_conflicts"+ChatColor.GRAY+": "+ConfigHelper.getLimit("enforce_vanilla_conflicts"));
											break;
										case "vanilla_level_limits":
											sender.sendMessage(ChatColor.DARK_GREEN + "limits.vanilla_level_limits"+ChatColor.GRAY+": "+ConfigHelper.getLimit("vanilla_level_limits"));
											break;
										case "prevent_dupe":
											sender.sendMessage(ChatColor.DARK_GREEN + "limits.prevent_dupe"+ChatColor.GRAY+": "+ConfigHelper.getLimit("prevent_dupe"));
											break;
										case "one_level_at_a_time":
											sender.sendMessage(ChatColor.DARK_GREEN + "limits.one_level_at_a_time"+ChatColor.GRAY+": "+ConfigHelper.getLimit("one_level_at_a_time"));
											break;
										case "max_enchantment_levels":
											sender.sendMessage(ChatColor.DARK_GREEN+"limits.max_enchantment_levels"+ChatColor.GRAY+": ");
											for (Map.Entry<String, Integer> s: GroupsHelper.defaultGroup.maxLevels.entrySet())
												sender.sendMessage(ChatColor.GRAY+" - "+ChatColor.DARK_GREEN+s.getKey()+ChatColor.GRAY+": "+ChatColor.WHITE+s.getValue());
											break;
										case "custom_conflicts":
											sender.sendMessage(ChatColor.DARK_GREEN+"limits.custom_conflicts"+ChatColor.GRAY+": ");
											for (String s: ConfigHelper.getConflictSinglesNames()) {
													sender.sendMessage(ChatColor.WHITE + " - " + ChatColor.GREEN + s + ChatColor.GRAY + ": ");
												for (String t: ConfigHelper.getConflictSinglesValues(s))
													sender.sendMessage(ChatColor.GRAY + "   - " + ChatColor.DARK_GREEN + t);
											}
											break;
										case "min_player_levels":
											sender.sendMessage(ChatColor.DARK_GREEN+"limits.min_player_levels"+ChatColor.GRAY+": ");
											for (Map.Entry<String, HashMap<String, Integer>> s: GroupsHelper.defaultGroup.minLevels.entrySet()) {
												sender.sendMessage(ChatColor.WHITE + " - " + ChatColor.GREEN + s.getKey() + ChatColor.GRAY + ": ");
												for (Map.Entry<String, Integer> t : s.getValue().entrySet())
													sender.sendMessage(ChatColor.GRAY + "   - " + ChatColor.DARK_GREEN + t.getKey() + ChatColor.GRAY + ": " + ChatColor.WHITE + t.getValue());
											}
											break;
										default:
											sender.sendMessage(ChatColor.GRAY+"Setting invalid.");
											break;
									}
								else if (args.length == 4)
									switch (args[2].toLowerCase()){
										case "max_enchantment_levels":
											if (GroupsHelper.defaultGroup.maxLevels.containsKey(NamespacedKey.fromString(args[3]).toString()))
												sender.sendMessage(ChatColor.DARK_GREEN+"limits.max_enchantment_levels."+args[3].toLowerCase()+ChatColor.GRAY+": "+GroupsHelper.defaultGroup.maxLevels.get(NamespacedKey.fromString(args[3]).toString()));
											else if (Enchantment.getByKey(NamespacedKey.fromString(args[3])) == null)
												sender.sendMessage(ChatColor.GRAY+"Enchantment invalid.");
											else
												sender.sendMessage(ChatColor.GRAY+"Enchantment undefined.");
											break;
										case "custom_conflicts":
											if (ConfigHelper.getConflictSinglesNames().contains(args[3].toLowerCase())) {
												sender.sendMessage(ChatColor.GREEN + args[3].toLowerCase() + ChatColor.GRAY + ": ");
												for (String t : ConfigHelper.getConflictSinglesValues(args[3].toLowerCase()))
													sender.sendMessage(ChatColor.GRAY + " - " + ChatColor.DARK_GREEN + t);
											} else if (Enchantment.getByKey(NamespacedKey.fromString(args[3].toLowerCase())) == null)
												sender.sendMessage(ChatColor.GRAY+"Enchantment invalid.");
											else
												sender.sendMessage(ChatColor.GRAY+"Enchantment undefined.");
											break;
										case "min_player_levels":
											if (GroupsHelper.defaultGroup.minLevels.containsKey(NamespacedKey.fromString(args[3]).toString())) {
												if (args.length < 5 || (!(args.length < 5) && args[4].equalsIgnoreCase("all"))) {
													sender.sendMessage(ChatColor.GREEN + args[3].toLowerCase() + ChatColor.GRAY + ": ");
													for (Map.Entry<String, Integer> t : GroupsHelper.defaultGroup.minLevels.get(NamespacedKey.fromString(args[3]).toString()).entrySet())
														sender.sendMessage(ChatColor.GRAY + " - " + ChatColor.DARK_GREEN + t.getKey() + ChatColor.GRAY + ": " + ChatColor.WHITE + t.getValue());
												} else {
													if (GroupsHelper.defaultGroup.minLevels.get(NamespacedKey.fromString(args[3]).toString()).containsKey(args[4].toLowerCase()))
														sender.sendMessage(ChatColor.DARK_GREEN + args[3].toLowerCase() + ChatColor.GRAY + ", " +ChatColor.DARK_GREEN + args[4].toLowerCase() + ChatColor.GRAY + ": " + ChatColor.WHITE + GroupsHelper.defaultGroup.minLevels.get(NamespacedKey.fromString(args[3]).toString()).get(args[4].toLowerCase()));
													else
														sender.sendMessage(ChatColor.GRAY+args[4].toLowerCase()+" is not a recognized subsection.");
												}
											} else if (Enchantment.getByKey(NamespacedKey.fromString(args[3].toLowerCase())) == null)
												sender.sendMessage(ChatColor.GRAY+"Enchantment invalid.");
											else
												sender.sendMessage(ChatColor.GRAY+"Enchantment undefined.");
											break;
									}
								else
									sender.sendMessage(ChatColor.GRAY+"Too many arguments.");
								break;
							case "event_cancelling":
								if (args.length < 3)
									sender.sendMessage(ChatColor.GRAY+"Please be more specific.");
								else if (args.length == 3)
									switch (args[2].toLowerCase()) {
										case "cancel_interaction":
												sender.sendMessage(ChatColor.DARK_GREEN + "event_cancelling.cancel_interaction.common" + ChatColor.GRAY + ": "+ConfigHelper.getCancelInteraction("common"));
												sender.sendMessage(ChatColor.DARK_GREEN + "event_cancelling.cancel_interaction.add" + ChatColor.GRAY + ": "+ConfigHelper.getCancelInteraction("add"));
												sender.sendMessage(ChatColor.DARK_GREEN + "event_cancelling.cancel_interactionl.remove" + ChatColor.GRAY + ": "+ConfigHelper.getCancelInteraction("remove"));
												sender.sendMessage(ChatColor.DARK_GREEN + "event_cancelling.cancel_interactionl.transfer" + ChatColor.GRAY + ": "+ConfigHelper.getCancelInteraction("transfer"));
											break;
										case "force_cancel_interaction":
												sender.sendMessage(ChatColor.DARK_GREEN + "event_cancelling.force_cancel_interaction.common" + ChatColor.GRAY + ": "+ConfigHelper.getForceCancelInteraction("common"));
												sender.sendMessage(ChatColor.DARK_GREEN + "event_cancelling.force_cancel_interaction.add" + ChatColor.GRAY + ": "+ConfigHelper.getForceCancelInteraction("add"));
												sender.sendMessage(ChatColor.DARK_GREEN + "event_cancelling.force_cancel_interaction.remove" + ChatColor.GRAY + ": "+ConfigHelper.getForceCancelInteraction("remove"));
											break;
										case "super_ultra_force_cancel_interaction_absolutely_for_sure":
												sender.sendMessage(ChatColor.DARK_GREEN + "event_cancelling.super_ultra_force_cancel_interaction_absolutely_for_sure.common" + ChatColor.GRAY + ": "+ConfigHelper.getSuperForceCancelInteraction("common"));
												sender.sendMessage(ChatColor.DARK_GREEN + "event_cancelling.super_ultra_force_cancel_interaction_absolutely_for_sure.add" + ChatColor.GRAY + ": "+ConfigHelper.getSuperForceCancelInteraction("add"));
												sender.sendMessage(ChatColor.DARK_GREEN + "event_cancelling.super_ultra_force_cancel_interaction_absolutely_for_sure.remove" + ChatColor.GRAY + ": "+ConfigHelper.getSuperForceCancelInteraction("remove"));
												sender.sendMessage(ChatColor.DARK_GREEN + "event_cancelling.super_ultra_force_cancel_interaction_absolutely_for_sure.transfer" + ChatColor.GRAY + ": "+ConfigHelper.getSuperForceCancelInteraction("transfer"));
											break;
										default:
											sender.sendMessage(ChatColor.GRAY+"Setting invalid.");
											break;
									}
								else if (args.length == 4)
									switch (args[2].toLowerCase()) {
										case "cancel_interaction":
												switch (args[3].toLowerCase()) {
													case "common":
														sender.sendMessage(ChatColor.DARK_GREEN + "event_cancelling.cancel_interaction.common" + ChatColor.GRAY + ": "+ConfigHelper.getCancelInteraction("common"));
														break;
													case "add":
														sender.sendMessage(ChatColor.DARK_GREEN + "event_cancelling.cancel_interaction.add" + ChatColor.GRAY + ": "+ConfigHelper.getCancelInteraction("add"));
														break;
													case "transfer":
														sender.sendMessage(ChatColor.DARK_GREEN + "event_cancelling.cancel_interaction.transfer" + ChatColor.GRAY + ": "+ConfigHelper.getCancelInteraction("transfer"));
														break;
													case "remove":
														sender.sendMessage(ChatColor.DARK_GREEN + "event_cancelling.cancel_interaction.remove" + ChatColor.GRAY + ": "+ConfigHelper.getCancelInteraction("remove"));
														break;
													default:
														sender.sendMessage(ChatColor.GRAY+"Setting invalid.");
														break;
												}
											break;
										case "force_cancel_interaction":
												switch (args[3].toLowerCase()) {
													case "common":
														sender.sendMessage(ChatColor.DARK_GREEN + "event_cancelling.force_cancel_interaction.common" + ChatColor.GRAY + ": "+ConfigHelper.getForceCancelInteraction("common"));
														break;
													case "add":
														sender.sendMessage(ChatColor.DARK_GREEN + "event_cancelling.force_cancel_interaction.add" + ChatColor.GRAY + ": "+ConfigHelper.getForceCancelInteraction("add"));
														break;
													case "transfer":
														sender.sendMessage(ChatColor.DARK_GREEN + "event_cancelling.force_cancel_interaction.transfer" + ChatColor.GRAY + ": "+ConfigHelper.getForceCancelInteraction("transfer"));
														break;
													case "remove":
														sender.sendMessage(ChatColor.DARK_GREEN + "event_cancelling.force_cancel_interaction.remove" + ChatColor.GRAY + ": "+ConfigHelper.getForceCancelInteraction("remove"));
														break;
													default:
														sender.sendMessage(ChatColor.GRAY+"Setting invalid.");
														break;
												}
											break;
										case "super_ultra_force_cancel_interaction_absolutely_for_sure":
												switch (args[3].toLowerCase()) {
													case "common":
														sender.sendMessage(ChatColor.DARK_GREEN + "event_cancelling.super_ultra_force_cancel_interaction_absolutely_for_sure.common" + ChatColor.GRAY + ": "+ConfigHelper.getSuperForceCancelInteraction("common"));
														break;
													case "add":
														sender.sendMessage(ChatColor.DARK_GREEN + "event_cancelling.super_ultra_force_cancel_interaction_absolutely_for_sure.add" + ChatColor.GRAY + ": "+ConfigHelper.getSuperForceCancelInteraction("add"));
														break;
													case "transfer":
														sender.sendMessage(ChatColor.DARK_GREEN + "event_cancelling.super_ultra_force_cancel_interaction_absolutely_for_sure.transfer" + ChatColor.GRAY + ": "+ConfigHelper.getSuperForceCancelInteraction("transfer"));
														break;
													case "remove":
														sender.sendMessage(ChatColor.DARK_GREEN + "event_cancelling.super_ultra_force_cancel_interaction_absolutely_for_sure.remove" + ChatColor.GRAY + ": "+ConfigHelper.getSuperForceCancelInteraction("remove"));
														break;
													default:
														sender.sendMessage(ChatColor.GRAY+"Setting invalid.");
														break;
												}
											break;
										default:
											sender.sendMessage(ChatColor.GRAY+"Setting invalid.");
											break;
									}
								break;
							default:
								sender.sendMessage(ChatColor.GRAY+"Setting invalid.");
								break;
						}
					break;
					//endregion
				case "set":
					//region set
					if (args.length < 3)
						sender.sendMessage(ChatColor.GRAY + "Not enough arguments.");
					else
						switch (args[1].toLowerCase()) {
							case "feature_active":
								if (args.length < 4) {
									sender.sendMessage(ChatColor.GRAY + "Not enough arguments.");
								} else if (args.length == 4)
									sender.sendMessage(ChatColor.GRAY + ConfigHelper.setFeature(args[2],Boolean.parseBoolean(args[3])));
								else
									sender.sendMessage(ChatColor.GRAY+"Too many arguments.");
								break;
							case "costs":
								if (args.length < 4)
									sender.sendMessage(ChatColor.GRAY + "Not enough arguments.");
								else if (args.length == 4)
									switch (args[2]) {
										case "add":
										case "transfer":
										case "remove":
											sender.sendMessage(ChatColor.GRAY + "Not enough arguments.");
											break;
										case "use_money_instead_of_xp":
											sender.sendMessage(ChatColor.GRAY+ConfigHelper.setUseCash(Boolean.parseBoolean(args[3])));
											break;
										default:
											sender.sendMessage(ChatColor.GRAY+"Setting invalid.");
											break;
									}
								else if (args.length == 5)
									switch (args[2]) {
										case "add":
										case "transfer":
										case "remove":
											sender.sendMessage(ChatColor.GRAY +ConfigHelper.setPriceValue(args[2],args[3],Double.parseDouble(args[4])));
											break;
										case "use_money_instead_of_xp":
											sender.sendMessage(ChatColor.GRAY +ConfigHelper.setScalePriceValue(args[2],args[3],Boolean.parseBoolean(args[4])));
											break;
										default:
											sender.sendMessage(ChatColor.GRAY+"Setting invalid.");
											break;
									}
								else
									sender.sendMessage(ChatColor.GRAY+"Too many arguments.");
								break;
							case "interaction":
								if (args.length < 4)
									sender.sendMessage(ChatColor.GRAY + "Not enough arguments.");
								else if (args.length == 4)
									switch (args[2].toLowerCase()) {
										case "use_permissions":
											sender.sendMessage(ChatColor.GRAY+ConfigHelper.setUsePermissions(Boolean.parseBoolean(args[3])));
											break;
										case "tell_player":
											sender.sendMessage(ChatColor.GRAY+ConfigHelper.setVerbose(Boolean.parseBoolean(args[3])));
											break;
										case "click_common":
											sender.sendMessage(ChatColor.GRAY+ConfigHelper.setClickCommon(Boolean.parseBoolean(args[3])));
											break;
										case "main_hand_only":
											sender.sendMessage(ChatColor.GRAY+ConfigHelper.setMainHandOnly(Boolean.parseBoolean(args[3])));
											break;
										case "creative_free_enchantments":
											sender.sendMessage(ChatColor.GRAY+ConfigHelper.setCreativeFree(Boolean.parseBoolean(args[3])));
											break;
										case "smart_enchantment_rolls":
											sender.sendMessage(ChatColor.GRAY+ConfigHelper.setSmartRolls(Boolean.parseBoolean(args[3])));
											break;
										case "dumb_roll_tries":
											sender.sendMessage(ChatColor.GRAY+ConfigHelper.setDumbTries(Integer.parseInt(args[3])));
											break;
										case "blocks":
										case "enchantment_add_weights":
											sender.sendMessage(ChatColor.GRAY + "Not enough arguments.");
											break;
										default:
											sender.sendMessage(ChatColor.GRAY+"Setting invalid.");
											break;
									}
								else if (args.length == 5)
									switch (args[2].toLowerCase()) {
										case "use_permissions":
										case "tell_player":
										case "click_common":
										case "main_hand_only":
										case "creative_free_enchantments":
										case "smart_enchantment_rolls":
										case "dumb_roll_tries":
											sender.sendMessage(ChatColor.GRAY+"Too many arguments.");
											break;
										case "blocks":
											sender.sendMessage(ChatColor.GRAY+ConfigHelper.setBlock(args[3],args[4]));
											break;
										case "enchantment_add_weights":
											sender.sendMessage(ChatColor.GRAY+ConfigHelper.setWeight(args[3],Integer.parseInt(args[4])));
											break;
										default:
											sender.sendMessage(ChatColor.GRAY+"Setting invalid.");
											break;
									}
								else
									sender.sendMessage(ChatColor.GRAY+"Too many arguments.");
								break;
							case "limits":
								if (args.length < 4)
									sender.sendMessage(ChatColor.GRAY + "Not enough arguments.");
								else if (args.length == 4)
									switch (args[2].toLowerCase()) {
										case "safe_mode":
											sender.sendMessage(ChatColor.GRAY+ConfigHelper.setLimitValue("safe_mode",Boolean.parseBoolean(args[3])));
											break;
										case "enforce_vanilla_conflicts":
											sender.sendMessage(ChatColor.GRAY+ConfigHelper.setLimitValue("enforce_vanilla_conflicts",Boolean.parseBoolean(args[3])));
											break;
										case "vanilla_level_limits":
											sender.sendMessage(ChatColor.GRAY+ConfigHelper.setLimitValue("vanilla_level_limits",Boolean.parseBoolean(args[3])));
											break;
										case "prevent_dupe":
											sender.sendMessage(ChatColor.GRAY+ConfigHelper.setLimitValue("prevent_dupe",Boolean.parseBoolean(args[3])));
											break;
										case "one_level_at_a_time":
											sender.sendMessage(ChatColor.GRAY+ConfigHelper.setLimitValue("one_level_at_a_time",Boolean.parseBoolean(args[3])));
											break;
										case "max_enchantment_levels":
										case "custom_conflicts":
										case "min_player_levels":
											sender.sendMessage(ChatColor.GRAY + "Not enough arguments.");
											break;
										default:
											sender.sendMessage(ChatColor.GRAY+"Setting invalid.");
											break;
									}
								else if (args.length == 5)
									switch (args[2].toLowerCase()){
										case "safe_mode":
										case "enforce_vanilla_conflicts":
										case "vanilla_level_limits":
										case "prevent_dupe":
										case "one_level_at_a_time":
											sender.sendMessage(ChatColor.GRAY+"Too many arguments.");
											break;
										case "max_enchantment_levels":
											sender.sendMessage(ChatColor.GRAY+ConfigHelper.setMaxLevel(args[3],Integer.parseInt(args[4])));
											break;
										case "custom_conflicts":
											sender.sendMessage(ChatColor.GRAY+ConfigHelper.addConflict(args[3],args[4]));
											break;
										case "min_player_levels":
											sender.sendMessage(ChatColor.GRAY + "Not enough arguments.");
											break;
									}
								else if (args.length == 6)
									switch (args[2].toLowerCase()){
										case "safe_mode":
										case "enforce_vanilla_conflicts":
										case "vanilla_level_limits":
										case "prevent_dupe":
										case "one_level_at_a_time":
										case "max_enchantment_levels":
										case "custom_conflicts":
											sender.sendMessage(ChatColor.GRAY+"Too many arguments.");
											break;
										case "min_player_levels":
											sender.sendMessage(ChatColor.GRAY+ConfigHelper.setMinLevel(args[3],args[4],Integer.parseInt(args[5])));
											break;
									}
								else
									sender.sendMessage(ChatColor.GRAY+"Too many arguments.");
								break;
							case "event_cancelling":
								if (args.length < 5)
									sender.sendMessage(ChatColor.GRAY + "Not enough arguments.");
								else if (args.length == 5)
									switch (args[2].toLowerCase()) {
										case "cancel_interaction":
											sender.sendMessage(ChatColor.GRAY + ConfigHelper.setCancel(args[3].toLowerCase(),Boolean.parseBoolean(args[4])));
											break;
										case "force_cancel_interaction":
											sender.sendMessage(ChatColor.GRAY + ConfigHelper.setForceCancel(args[3].toLowerCase(),Boolean.parseBoolean(args[4])));
											break;
										case "super_ultra_force_cancel_interaction_absolutely_for_sure":
											sender.sendMessage(ChatColor.GRAY + ConfigHelper.setSuperForceCancel(args[3].toLowerCase(),Boolean.parseBoolean(args[4])));
											break;
										default:
											sender.sendMessage(ChatColor.GRAY+"Setting invalid.");
											break;
									}
								break;
							default:
								sender.sendMessage(ChatColor.GRAY+"Setting invalid.");
								break;
						}
					break;
					//endregion
				case "remove":
					//region remove
					if (args.length < 4)
						sender.sendMessage(ChatColor.GRAY+"Please be more specific.");
					else if (args.length == 4)
						switch (args[1].toLowerCase()) {
							case "interaction":
								if ("enchantment_add_weights".equalsIgnoreCase(args[2])) {
									sender.sendMessage(ChatColor.GRAY +ConfigHelper.removeWeight(args[3]));
									break;
								}
								sender.sendMessage(ChatColor.DARK_RED + "Can't remove that.");
								break;
							case "limits":
									switch (args[2].toLowerCase()) {
									case "max_enchantment_levels":
										sender.sendMessage(ChatColor.GRAY +ConfigHelper.removeMaxLevelValue(args[3]));
										break;
									case "custom_conflicts":
										sender.sendMessage(ChatColor.GRAY + ConfigHelper.removeConflictWith(args[3]));
										break;
									case "min_player_levels":
										sender.sendMessage(ChatColor.GRAY +ConfigHelper.removeMinLevel(args[3], "all"));
										break;
									default:
										sender.sendMessage(ChatColor.DARK_RED + "Can't remove that.");
										break;
									}
								break;
							default:
								sender.sendMessage(ChatColor.DARK_RED + "Can't remove that.");
								break;

							}
						else if (args.length == 5)
						if ("limits".equalsIgnoreCase(args[1])) {
							switch (args[2].toLowerCase()) {
								case "custom_conflicts":
									sender.sendMessage(ChatColor.GRAY + ConfigHelper.removeConflict(args[3], args[4]));
									break;
								case "min_player_levels":
									sender.sendMessage(ChatColor.GRAY + ConfigHelper.removeMinLevel(args[3], args[4]));
									break;
								default:
									sender.sendMessage(ChatColor.DARK_RED + "Can't remove that.");
									break;
							}
						} else {
							sender.sendMessage(ChatColor.DARK_RED + "Can't remove that.");
						}
						else
							sender.sendMessage(ChatColor.GRAY + "Too many arguments.");
					break;
					//endregion
				default:
					return false;
			}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1){
			return filterArgs(Arrays.asList("help","reload","save","info","get","set","remove"),args[0]);
		} else if (args.length > 1){
			switch (args[0].toLowerCase()) {
				case "help":
					//region help
					if (args.length < 3)
						return filterArgs(Arrays.asList("help","reload","save","info","get","set","remove"),args[1]);
					else if (args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("help"))
						return List.of("help");
					break;
					//endregion
				case "set":
					//region set
					if (args.length > 3)
						switch (args[1].toLowerCase()) {
						case "feature_active":
							if (args.length == 4)
								return filterArgs(Arrays.asList("true","false"),args[3]);
							break;
						case "costs":
							if (args.length == 4 && args[2].equalsIgnoreCase("use_money_instead_of_xp"))
								return filterArgs(Arrays.asList("true","false"),args[3]);
							else if (args.length == 5)
								switch (args[3].toLowerCase()){
									case "cost":
									case "cost_multiplier":
									case "cost_adder":
										return List.of("<double>");
									case "scaling_per_enchantment_level":
										return filterArgs(Arrays.asList("true","false"),args[4]);
									}
							break;
						case "interaction":
							if (args.length == 4)
								switch (args[2].toLowerCase()) {
									case "use_permissions":
									case "tell_player":
									case "click_common":
									case "main_hand_only":
									case "creative_free_enchantments":
									case "smart_enchantment_rolls":
										return filterArgs(Arrays.asList("true","false"),args[3]);
									case "dumb_roll_tries":
										return List.of("<int>");
								}
							else if (args.length == 5)
								switch (args[2].toLowerCase()){
									case "blocks":
										return filterArgs(ConfigHelper.blockNames,args[4]);
									case "enchantment_add_weights":
										return filterArgs(List.of("<int>"),args[4]);
								}
							break;
						case "limits":
							if (args.length == 4)
								switch (args[2].toLowerCase()) {
									case "safe_mode":
									case "enforce_vanilla_conflicts":
									case "vanilla_level_limits":
									case "prevent_dupe":
									case "one_level_at_a_time":
										return filterArgs(Arrays.asList("true","false"),args[3]);
									case "custom_conflicts":
									case "max_enchantment_levels":
									case "min_player_levels":
										return filterArgs(EnchHelper.enchNames,args[3]);
								}
							else
								switch (args[2].toLowerCase()){
									case "max_enchantment_levels":
										if (args.length == 5)
											return List.of("<int>");
										break;
									case "custom_conflicts":
										if (args.length == 5)
											return filterArgs(EnchHelper.enchNames,args[4]);
										break;
									case "min_player_levels":
										if (args.length == 6)
											return List.of("<int>");
										break;
								}
							break;
						case "event_cancelling":
							if (args.length == 5)
								switch (args[3].toLowerCase()){
									case "add":
									case "transfer":
									case "remove":
									case "common":
											return filterArgs(Arrays.asList("true","false"),args[4]);
								}
							break;
					}
					//endregion
				case "info":
				case "get":
					//region info get
					if (args.length < 3)
						return filterArgs(Arrays.asList("feature_active","interaction","limits","costs","event_cancelling"),args[1]);

					switch (args[1].toLowerCase()) {
						case "feature_active":
							if (args.length < 4)
								return filterArgs(Arrays.asList("add","transfer","remove"),args[2]);
							break;
						case "costs":
							if (args.length < 4)
								return filterArgs(Arrays.asList("add","transfer","remove","use_money_instead_of_xp"),args[2]);
							else if (args.length < 5)
								switch (args[2].toLowerCase()){
									case "add":
									case "remove":
									case "transfer":
										return filterArgs(Arrays.asList("cost","cost_multiplier","cost_adder","scaling_per_enchantment_level"),args[3]);
									}
							break;
						case "interaction":
							if (args.length < 4)
								return filterArgs(Arrays.asList("use_permissions","tell_player","click_common","main_hand_only","creative_free_enchantments","blocks","smart_enchantment_rolls","dumb_roll_tries","enchantment_add_weights"),args[2]);
							else if (args.length < 5)
								switch (args[2].toLowerCase()){
									case "blocks":
										return filterArgs(Arrays.asList("add","transfer","remove","common"),args[3]);
									case "enchantment_add_weights":
										ArrayList<String> tmp = new ArrayList<String>(GroupsHelper.defaultGroup.addWeights.keySet());
										tmp.add("default");
										return filterArgs(tmp, args[3]);
									}
							break;
						case "limits":
							if (args.length < 4)
								return filterArgs(Arrays.asList("safe_mode","enforce_vanilla_conflicts","vanilla_level_limits","prevent_dupe","one_level_at_a_time","max_enchantment_levels","custom_conflicts","min_player_levels"),args[2]);
							else
								switch (args[2].toLowerCase()){
									case "max_enchantment_levels":
										if (args.length < 5)
											return filterArgs(new ArrayList<String>(GroupsHelper.defaultGroup.maxLevels.keySet()),args[3]);
										break;
									case "custom_conflicts":
										if (args.length < 5)
											return filterArgs(new ArrayList<String>(ConfigHelper.getConflictSinglesNames()),args[3]);
										break;
									case "min_player_levels":
										if (args.length < 5)
											return filterArgs(new ArrayList<String>(ConfigHelper.getMinLevelNames()),args[3]);
										else if (args.length < 6)
											return filterArgs(Arrays.asList("add","transfer","remove","all"),args[4]);
										break;
								}
							break;
						case "event_cancelling":
							if (args.length < 4)
								return filterArgs(Arrays.asList("cancel_interaction","force_cancel_interaction","super_ultra_force_cancel_interaction_absolutely_for_sure"),args[2]);
							else if (args.length < 5)
								switch (args[2].toLowerCase()){
									case "cancel_interaction":
									case "force_cancel_interaction":
									case "super_ultra_force_cancel_interaction_absolutely_for_sure":
											return filterArgs(Arrays.asList("add","transfer","remove","common"),args[3]);
								}
							break;
					}
					break;
					//endregion
				case "remove":
					//region remove
					if (args.length < 3)
						return filterArgs(Arrays.asList("interaction","limits"),args[1]);
					else
						switch (args[1].toLowerCase()) {
							case "interaction":
								if (args.length < 4)
									return filterArgs(List.of("enchantment_add_weights"),args[2]);
								else if (args.length < 5 && args[2].equalsIgnoreCase("enchantment_add_weights"))
									return filterArgs(new ArrayList<String>(GroupsHelper.defaultGroup.addWeights.keySet()), args[3]);
								break;
							case "limits":
								if (args.length < 4)
									return filterArgs(Arrays.asList("max_enchantment_levels","custom_conflicts","min_player_levels"),args[2]);
								else
									switch (args[2].toLowerCase()) {
									case "max_enchantment_levels":
										if (args.length < 5)
											return filterArgs(new ArrayList<String>(GroupsHelper.defaultGroup.maxLevels.keySet()),args[3]);
										break;
									case "custom_conflicts":
										if (args.length < 5)
											return filterArgs(new ArrayList<String>(ConfigHelper.getConflictEnchNames()),args[3]);
										else if (args.length < 6){
											return filterArgs(ConfigHelper.getConflictsWith(args[3]),args[4]);}
										break;
									case "min_player_levels":
										if (args.length < 5)
											return filterArgs(new ArrayList<String>(ConfigHelper.getMinLevelNames()),args[3]);
										else if (args.length < 6)
											return filterArgs(Arrays.asList("add","transfer","remove","all"),args[4]);
										break;
									}
								break;
						}
					//endregion
			}
		}


		return List.of("");
	}

	private List<String> filterArgs(List<String> possibles, String text) {
		if (text.isBlank())
			return possibles;
		
		ArrayList<String> l = new ArrayList<String>();
		for (String s:possibles) 
			if (s.toLowerCase().contains(text.toLowerCase())) 
				l.add(s);
			
		
		return l;
	}
}
