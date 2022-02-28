package io.github.SkywolfDragon.fileHelpers;

import java.util.*;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;

import io.github.SkywolfDragon.EnchManagerPlugin;

public abstract class ConfigHelper {

	
	private static final HashMap<String, Boolean> features =  new HashMap<String, Boolean>();
	private static Boolean usePermissions = false;
	private static Boolean verbose = true;
	private static Boolean clickCommon = false;
	private static final HashMap<String, String> blocks =  new HashMap<String, String>();
	private static Boolean mainHandOnly = false;
	private static Boolean creativeFree = false;
	private static final HashMap<String, Boolean> cancelInteractions =  new HashMap<String, Boolean>();
	private static final HashMap<String, Boolean> forceCancelInteractions =  new HashMap<String, Boolean>();
	private static final HashMap<String, Boolean> superForceCancelInteractions =  new HashMap<String, Boolean>();
	private static final HashMap<String, Boolean> limits =  new HashMap<String, Boolean>();
	private static HashMap<String, ArrayList<String>> conflicts =  new HashMap<String, ArrayList<String>>();
	private static final HashMap<String, ArrayList<String>> conflictSingles =  new HashMap<String, ArrayList<String>>();
	private static Boolean smartRolls = true;
	private static Integer dumbTries = 1;
	
	private static FileConfiguration config;
	
	public static final ArrayList<String> blockNames = new ArrayList<String>();
	
	public static void startUpConfiguration() {
		
		EnchManagerPlugin.PLUGIN_INSTANCE.saveDefaultConfig();
		
		EnchManagerPlugin.PLUGIN_INSTANCE.reloadConfig();
		
		config = EnchManagerPlugin.PLUGIN_INSTANCE.getConfig();
		
		//load feature_active
		features.put("add", config.getBoolean("feature_active.add",true));
			//whether the add enchantment feature is usable, if false this feature will be completely disabled
		features.put("transfer", config.getBoolean("feature_active.transfer",true));
			//whether the transfer enchantment feature is usable, if false this feature will be completely disabled
		features.put("remove", config.getBoolean("feature_active.remove",true));
			//whether the add enchantment feature is usable, if false this feature will be completely disabled

		
		//load use_permissions
		usePermissions = config.getBoolean("interaction.use_permissions", false);
		    //when true the following permissions will be checked:
		        //enchantmentmanagement.commands.cost for using /enchcost 
		        //enchantmentmanagement.use.add for using the add feature
		        //enchantmentmanagement.use.transfer for using the transfer feature
		        //enchantmentmanagement.use.remove for using the remove feature
		        //enchantmentmanagement.use (or enchantmentmanagement.use.*) can be used to give all the use permissions
		    //all other permissions are always checked
		    //when false, these permissions aren't checked
		
		//load tell_player
		verbose = config.getBoolean("interaction.tell_player", true);
			//whether messages should be broadcasted to players
		
		//load click_common
		clickCommon = config.getBoolean("interaction.click_common", true);
		    //when true you use the plugin by activating the common block(the one below), when false you use the block on top of the common
		    //if set to false, it's recommended you set the cancel_interactions
		

		//load and make sure all the blocks are actual blocks (common, add, transfer, remove)
		if (Material.matchMaterial(config.getString("interaction.blocks.common")) == null) {
			EnchManagerPlugin.PLUGIN_INSTANCE.getLogger().warning("Couldn't recognize "+config.getString("interaction.blocks.common")+" as a valid bukkit material for the common block, defaulting to DIAMOND_BLOCK!");
			blocks.put("common", Material.DIAMOND_BLOCK.getKey().toString());
		} else {
			blocks.put("common", Material.matchMaterial(config.getString("interaction.blocks.common")).getKey().toString());
		}
        	//^the block that you punch, the one that goes below the other blocks
		
		if (Material.matchMaterial(config.getString("interaction.blocks.add")) == null) {
			EnchManagerPlugin.PLUGIN_INSTANCE.getLogger().warning("Couldn't recognize "+config.getString("interaction.blocks.add")+" as a valid bukkit material for the add block, defaulting to ENCHANTING_TABLE!");
			blocks.put("add", Material.ENCHANTING_TABLE.getKey().toString());
		} else {
			blocks.put("add", Material.matchMaterial(config.getString("interaction.blocks.add")).getKey().toString());
		}
        	//^the block that sits on top of the common block for adding enchantments to the item in the player's main or off hand
		
		if (Material.matchMaterial(config.getString("interaction.blocks.transfer")) == null) {
			EnchManagerPlugin.PLUGIN_INSTANCE.getLogger().warning("Couldn't recognize "+config.getString("interaction.blocks.transfer")+" as a valid bukkit material for the transfer block, defaulting to ANVIL!");
			blocks.put("transfer", Material.ANVIL.getKey().toString());
		} else {
			blocks.put("transfer", Material.matchMaterial(config.getString("interaction.blocks.transfer")).getKey().toString());
		}
        	//^the block that sits on top of the common block for transfering enchantments from the item in the player's main hand to the item in the player's off hand
		
		if (Material.matchMaterial(config.getString("interaction.blocks.remove")) == null) {
			EnchManagerPlugin.PLUGIN_INSTANCE.getLogger().warning("Couldn't recognize "+config.getString("interaction.blocks.remove")+" as a valid bukkit material for the remove block, defaulting to GRINDSTONE!");
			blocks.put("remove", Material.GRINDSTONE.getKey().toString());
		} else {
			blocks.put("remove", Material.matchMaterial(config.getString("interaction.blocks.remove")).getKey().toString());
		}
        	//^the block that sits on top of the common block for removing enchantments from the item in the player's main or off hand:
		
		//load limits
		limits.put("safe_mode", config.getBoolean("limits.safe_mode",true));
        	//only adds or transfers enchantments where compatible in vanilla minecraft (boring), does not check the enchantment's level or enchantment conflicts
		limits.put("enforce_vanilla_conflicts", config.getBoolean("limits.enforce_vanilla_conflicts",true));
        	//when true, conflicting enchantments won't be added into the same item (example: silk touch conflicts with fortune), doesn't affect custom_conflicts
		limits.put("vanilla_level_limits", config.getBoolean("limits.vanilla_level_limits",true));
        	//if the levels of enchantments should be limited to their vanilla minecraft maximum levels (boring), doesn't affect max_enchantment_levels
		limits.put("prevent_dupe", config.getBoolean("limits.prevent_dupe",true));
        	//prevents the use of more than one item in a stack when adding, removing, or transfering enchantments
		limits.put("one_level_at_a_time", config.getBoolean("limits.one_level_at_a_time",true));
        	//when true, enchantments will be removed/transfered one level at a time instead of all levels at once
		

		//load custom_conflicts, yes it has to be this big
		conflictSingles.clear();
		if (config.getConfigurationSection("limits.custom_conflicts") != null) {
			//iterate through the elements
			for (String s:config.getConfigurationSection("limits.custom_conflicts").getKeys(false)) {
				//we used namespaced keys here to have them be consistent later
				NamespacedKey ench = NamespacedKey.fromString(s);
				//we tell the console when things aren't enchantments. including typos. smute isn't an enchantment, sorry
				if (Enchantment.getByKey(ench) == null) {
					EnchManagerPlugin.PLUGIN_INSTANCE.getLogger().warning("Couldn't recognize "+s+" as a valid enchantment!");
				} else {
					//if conflictSingles doesn't have a key, yes it does
					if (!conflictSingles.containsKey(ench.toString())) {
						conflictSingles.put(ench.toString(), new ArrayList<String>());
					}
					//check string or list of strings, act accordingly
					if (config.isString("limits.custom_conflicts."+s)) {
						//just a quick exsistance check
						NamespacedKey ench2 = NamespacedKey.fromString(config.getString("limits.custom_conflicts."+s));
						if (Enchantment.getByKey(ench2) == null) {
							EnchManagerPlugin.PLUGIN_INSTANCE.getLogger().warning("Couldn't recognize "+config.getString("limits.custom_conflicts."+s)+" as a valid enchantment!");
						} else {
							//finally we get to add the enchantment
							conflictSingles.get(ench.toString()).add(ench2.toString());
						}
					} else if (config.isList("limits.custom_conflicts."+s)) {
						//i heard you like iterators
						for (String t:config.getStringList("limits.custom_conflicts."+s)) {
							//same as when it's a string, but more
							NamespacedKey ench2 = NamespacedKey.fromString(t);
							if (Enchantment.getByKey(ench2) == null) {
								EnchManagerPlugin.PLUGIN_INSTANCE.getLogger().warning("Couldn't recognize "+t+" as a valid enchantment!");
							} else {
								conflictSingles.get(ench.toString()).add(ench2.toString());
							}
						}
					}
				}
			}
		}
		//because the previous section is obscene enough as is
		processConflicts();

		//load creative_free_enchantments
		creativeFree = config.getBoolean("interaction.creative_free_enchantments", false);
		
		//we just outsource the loading of costs, it's cheaper overseas!
		GroupsHelper.setDefaultGroup(config.getRoot());
		
		//load smart rolls
		smartRolls = config.getBoolean("interaction.smart_enchantment_rolls",true);
		
		//you keep trying buddy, you'll get it eventually
		dumbTries = config.getInt("interaction.dumb_roll_tries", 16);

		//load main_hand_only
		mainHandOnly = config.getBoolean("interaction.main_hand_only",false);
		

		//load the dreaded cancel interaction sections
		//yes, yes, i know it's not elegant, but it's simple and reliable, so
		cancelInteractions.put("common", config.getBoolean("event_cancelling.cancel_interaction.common",true));
		cancelInteractions.put("add", config.getBoolean("event_cancelling.cancel_interaction.add",false));
		cancelInteractions.put("transfer", config.getBoolean("event_cancelling.cancel_interaction.transfer",false));
		cancelInteractions.put("remove", config.getBoolean("event_cancelling.cancel_interaction.remove",false));
		
		//sometimes the simplest way is the best one
		forceCancelInteractions.put("common", config.getBoolean("event_cancelling.force_cancel_interaction.common",false));
		forceCancelInteractions.put("add", config.getBoolean("event_cancelling.force_cancel_interaction.add",false));
		forceCancelInteractions.put("transfer", config.getBoolean("event_cancelling.force_cancel_interaction.transfer",false));
		forceCancelInteractions.put("remove", config.getBoolean("event_cancelling.force_cancel_interaction.remove",false));

		//are you sure of this?
		superForceCancelInteractions.put("common", config.getBoolean("event_cancelling.super_ultra_force_cancel_interaction_absolutely_for_sure.common",false));
		superForceCancelInteractions.put("add", config.getBoolean("event_cancelling.super_ultra_force_cancel_interaction_absolutely_for_sure.add",false));
		superForceCancelInteractions.put("transfer", config.getBoolean("event_cancelling.super_ultra_force_cancel_interaction_absolutely_for_sure.transfer",false));
		superForceCancelInteractions.put("remove", config.getBoolean("event_cancelling.super_ultra_force_cancel_interaction_absolutely_for_sure.remove",false));
		
		
		EnchManagerPlugin.PLUGIN_INSTANCE.getLogger().info("Configuration Loaded!");
		EnchManagerPlugin.PLUGIN_INSTANCE.getLogger().info("To see a list of available settings/subsettings use command: /enchmanagementconfig list [setting]");
		EnchManagerPlugin.PLUGIN_INSTANCE.getLogger().info("To see the values of each setting, use command: /enchmanagementconfig get <setting> [subsetting]");
	}

	public static void buildBlockNames(){
		for (Material m: Material.values())
			if (m.isBlock())
				blockNames.add(m.name());
	}

	private static void processConflicts() {
		//we put the conflicts we already have into the conflicts map
		conflicts = new HashMap<String, ArrayList<String>>(conflictSingles);
		//now we make em reciprocal
		for (String e: conflictSingles.keySet()) {
			for (String f: conflictSingles.get(e)) {
				if (!conflicts.containsKey(f))
					conflicts.put(f, new ArrayList<String>());
				conflicts.get(f).add(e);
			}
		}
		
		//remove the forever alones
		for (String e: conflictSingles.keySet()) {
			if (conflicts.get(e).isEmpty()) {
				conflicts.remove(e);
			}
		}

	}


	
	public static String setFeature(String key, Boolean value) {
		if (features.containsKey(key)) {
			features.put(key, value);
			config.set("feature_active."+key, value);
			String state = value? "activated":"deactivated";
			return key+" feature successfully "+state+".";
		}
		return "Category "+key+" not recognized, use \"/enchmanager list feature_active\" to see a list of categories.";
	}
	
	public static String setUsePermissions(Boolean value) {
		usePermissions = value;
		config.set("interaction.use_permissions", value);
		if (usePermissions) {
			return "Now using permissions.";
		}
		return "Now ignoring permissions (except for groups), anyone can use the plugin's active features (excluding the /enchmanager command).";
	}
	
	public static String setClickCommon(Boolean value) {
		clickCommon = value;
		config.set("interaction.click_common", value);
		if (clickCommon) {
			return "Players must use the common block now.";
		}
		return "Players must use the block above the common block now.";
	}
	
	public static String setMainHandOnly(Boolean value) {
		mainHandOnly = value;
		config.set("interaction.main_hand_only", value);
		if (mainHandOnly) {
			return "Players must use have the item to add/remove enchantment on their main hand.";
		}
		return "Players can have the item to add/remove enchantment on either hand.";
	}
	
	public static String setBlock(String key, String value) {
		if (blocks.containsKey(key)) {
			Material mat = Material.matchMaterial(value);
			if (mat == null) {
				return "Couldn't recognize \""+value+"\" as a valid bukkit material.";
			}
			blocks.put(key, mat.getKey().toString());
			config.set("interaction.blocks."+key, mat.name());
			return key+" block successfully set to "+mat.name()+".";
		}
		return "Category "+key+" not recognized, use \"/enchmanager list blocks\" to see a list of categories.";
	}
	
	public static String setCancel(String group, Boolean value) {
		if (cancelInteractions.containsKey(group)) {
			cancelInteractions.put(group, value);
			config.set("event_cancelling.cancel_interaction."+group, value);
			String state = value? "can't":"can";
			return "The "+group+" block ("+blocks.get(group)+")"+state+" be interacted with when the use of this plugin is successful.";
		}
		return "Category "+group+" not recognized, use \"/enchmanager list cancel_interaction\" to see a list of categories.";
	}
	
	public static String setForceCancel(String group, Boolean value) {
		if (forceCancelInteractions.containsKey(group)) {
			forceCancelInteractions.put(group, value);
			config.set("event_cancelling.force_cancel_interaction."+group, value);
			String state = value? "can't be interacted with regardless of use success":"can be interacted with (if the corresponding cancel_interaction allows it)";
			return "The "+group+" block ("+blocks.get(group)+")"+state+".";
		}
		return "Category "+group+" not recognized, use \"/enchmanager list force_cancel_interaction\" to see a list of categories.";
	}
	
	public static String setSuperForceCancel(String key, Boolean value) {
		if (superForceCancelInteractions.containsKey(key)) {
			superForceCancelInteractions.put(key, value);
			config.set("event_cancelling.force_cancel_interaction."+key, value);
			if (value) {
				return "The "+blocks.get(key)+" block won't have all its interactions forcibly canceled.";
			}
			return "The "+blocks.get(key)+" block can't be interacted with, are you absolutely sure of this?";
		}
		return "Category "+key+" not recognized, use \"/enchmanager list super_ultra_force_cancel_interaction_absolutely_for_sure\" to see a list of categories.";
	}

	public static String setUseCash(Boolean value) {
		GroupsHelper.defaultGroup.useCash = value;
		config.set("costs.use_money_instead_of_xp", value);
		if (value) {
			if (EnchManagerPlugin.vaultFound) {
				return "Money will be used as payment.";
			}
			return "Money is set to be used as payment, but no Vault economy plugin found, falling back to Xp.";
		}
		return "Xp will be used as payment.";
	}

	public static String setPriceValue(String group, String key, Double value) {
		if (GroupsHelper.defaultGroup.containsKey(group)) {
			if(GroupsHelper.defaultGroup.get(group).containsDouble(key)) {
				GroupsHelper.defaultGroup.get(group).put(key, value);
				config.set("costs."+group+"."+key, value);
				return key+" of "+group+" set to: "+value.toString()+".";
			}
			return "Subcategory "+key+" not recognized, use \"/enchmanager list costs "+group+"\" to see a list of subcategories.";
		}
		return "Category "+group+" not recognized, use \"/enchmanager list costs\" to see a list of categories.";
	}

	public static String setScalePriceValue(String group, String key, Boolean value) {
		if (GroupsHelper.defaultGroup.containsKey(group)) {
			if(GroupsHelper.defaultGroup.get(group).containsBool(key)) {
				GroupsHelper.defaultGroup.get(group).put(key, value);
				config.set("costs."+group+"."+key, value);
				return key+" of "+group+" set to: "+value.toString()+".";
			}
			return "Subcategory "+key+" not recognized, use \"/enchmanager list costs "+group+"\" to see a list of subcategories.";
		}
		return "Category "+group+" not recognized, use \"/enchmanager list costs\" to see a list of categories.";
	}

	public static String setLimitValue(String key, Boolean value) {
		if (limits.containsKey(key)) {
			limits.put(key, value);
			config.set("limits."+key, value);
			return key+" set to: "+value.toString()+".";
		}
		return "Category "+key+" not recognized, use \"/enchmanager list limits\" to see a list of categories.";
	}
	
	public static String setMaxLevel(String key, int value) {
		NamespacedKey ench = NamespacedKey.fromString(key);
		if (Enchantment.getByKey(ench) == null) {
			return "Couldn't recognize "+key+" as a valid enchantment.";
		}
		GroupsHelper.defaultGroup.maxLevels.put(ench.toString(), value);
		config.set("limits.max_enchantment_levels", GroupsHelper.defaultGroup.maxLevels);
		return "Enchantment "+key+" will from now on be limited to level "+Integer.toString(value)+".";
	}

	public static String removeMaxLevelValue(String key) {
		NamespacedKey ench = NamespacedKey.fromString(key);
		if (Enchantment.getByKey(ench) == null) {
			return "Couldn't recognize "+key+" as a valid enchantment.";
		}
		if (!GroupsHelper.defaultGroup.maxLevels.containsKey(ench.toString())) {
			return "Found no currently registered limit for "+key;
		}
		GroupsHelper.defaultGroup.maxLevels.remove(ench.toString());
		config.set("limits.max_enchantment_levels", GroupsHelper.defaultGroup.maxLevels);
		return "Enchantment limit for "+key+" has been removed.";
	}

	public static String setWeight(String key, int value) {
		if (key.equalsIgnoreCase("default")){
			GroupsHelper.defaultGroup.defaultWeight = value;
			config.set("interaction.enchantment_add_weights.default", value);
			return "Default weight is now "+Integer.toString(value)+".";
		}
		NamespacedKey ench = NamespacedKey.fromString(key);
		if (Enchantment.getByKey(ench) == null) {
			return "Couldn't recognize "+key+" as a valid enchantment.";
		}
		GroupsHelper.defaultGroup.addWeights.put(ench.toString(), value);
		LinkedHashMap<String,Integer> tmp = new LinkedHashMap<String,Integer>(GroupsHelper.defaultGroup.addWeights);
		tmp.put("default",GroupsHelper.defaultGroup.defaultWeight);
		config.set("interaction.enchantment_add_weights", tmp);
		return "Enchantment "+key+"'s weight now is "+Integer.toString(value)+".";
	}

	public static String removeWeight(String key) {
		NamespacedKey ench = NamespacedKey.fromString(key);
		if (Enchantment.getByKey(ench) == null) {
			return "Couldn't recognize "+key+" as a valid enchantment.";
		}
		if (!GroupsHelper.defaultGroup.addWeights.containsKey(ench.toString())) {
			return "Found no currently registered limit for "+key;
		}
		GroupsHelper.defaultGroup.addWeights.remove(ench.toString());
		LinkedHashMap<String,Integer> tmp = new LinkedHashMap<String,Integer>(GroupsHelper.defaultGroup.addWeights);
		tmp.put("default",GroupsHelper.defaultGroup.defaultWeight);
		config.set("interaction.enchantment_add_weights", tmp);
		return "Enchantment add weight for "+key+" has been removed.";
	}

	public static String setMinLevel(String enchName, String key, int value) {
		NamespacedKey ench = NamespacedKey.fromString(enchName);
		if (Enchantment.getByKey(ench) == null) {
			return "Couldn't recognize "+key+" as a valid enchantment.";
		}

		if (!GroupsHelper.defaultGroup.minLevels.containsKey(ench.toString()))
			GroupsHelper.defaultGroup.minLevels.put(ench.toString(), new HashMap<String,Integer>());

		switch (key.toLowerCase()) {
			case "add":
			case "transfer":
			case "remove":
				GroupsHelper.defaultGroup.minLevels.get(ench.toString()).put(key.toLowerCase(), value);
				config.set("limits.min_player_levels", GroupsHelper.defaultGroup.minLevels);
				if (value > 0) {
					return "Players won't be able to "+key.toLowerCase()+" "+ench.toString()+" before level "+Math.abs(value)+".";
				} else if (value < 0){
					return "Players won't be able to "+key.toLowerCase()+" "+ench.toString()+" after level "+ Math.abs(value)+".";
				} else {
					return "Players won't be able to "+key.toLowerCase()+" "+ench.toString()+" at any level.";
				}
			case "all":
				GroupsHelper.defaultGroup.minLevels.get(ench.toString()).put("add",value);
				GroupsHelper.defaultGroup.minLevels.get(ench.toString()).put("transfer",value);
				GroupsHelper.defaultGroup.minLevels.get(ench.toString()).put("remove",value);
				config.set("limits.min_player_levels", GroupsHelper.defaultGroup.minLevels);
				if (value > 0) {
					return "Players won't be able to manipulate "+ench.toString()+" before level "+Math.abs(value)+".";
				} else if (value < 0){
					return "Players won't be able to manipulate "+ench.toString()+" after level "+Math.abs(value)+".";
				} else {
					return "Players won't be able to manipulate "+ench.toString()+" at any level.";
				}
			default:
				return "Couldn't recognize "+key+" as either all, add, transfer or remove.";
		}
	}

	public static String removeMinLevel(String enchName, String key) {
		NamespacedKey ench = NamespacedKey.fromString(enchName);

		if (!GroupsHelper.defaultGroup.minLevels.containsKey(ench.toString()))
			return "Couldn't find "+ench.toString()+" in the minimum levels list.";

		switch (key.toLowerCase()) {
			case "add":
			case "transfer":
			case "remove":
				GroupsHelper.defaultGroup.minLevels.get(ench.toString()).remove(key.toLowerCase());
				if (GroupsHelper.defaultGroup.minLevels.get(ench.toString()).isEmpty()) {
					GroupsHelper.defaultGroup.minLevels.remove(ench.toString());
				}
				break;
			case "all":
				GroupsHelper.defaultGroup.minLevels.remove(ench.toString());
				break;
			default:
				return "Couldn't recognize "+key+" as either all, add, transfer or remove.";
		}


		config.set("limits.min_player_levels", GroupsHelper.defaultGroup.minLevels);

		if (key.equalsIgnoreCase("all")){
			return "All player level limits for "+ench.toString()+" have been removed.";
		} else {
			return "The "+key.toLowerCase()+" player level limit for "+ench.toString()+" has been removed.";
		}
	}
	
	public static String addConflict(String key, String value) {
		NamespacedKey ench1 = NamespacedKey.fromString(key);
		NamespacedKey ench2 = NamespacedKey.fromString(value);
		if (Enchantment.getByKey(ench1) == null) {
			return "Couldn't recognize "+key+" as a valid enchantment.";
		}
		if (Enchantment.getByKey(ench2) == null) {
			return "Couldn't recognize "+value+" as a valid enchantment.";
		}
		
		if (conflictSingles.containsKey(ench1.toString())) {
			conflictSingles.get(ench1.toString()).add(ench2.toString());
		} else if (conflictSingles.containsKey(ench2.toString())) {
			conflictSingles.get(ench2.toString()).add(ench1.toString());
		} else {
			conflictSingles.put(ench1.toString(), new ArrayList<String>());
			conflictSingles.get(ench1.toString()).add(ench2.toString());
		}
		
		
		config.set("limits.custom_conflicts", conflictSingles);
		processConflicts();
		return "Enchantment "+key+" won't be added to items with enchantment "+value+" and viceversa.";
	}
	
	public static String removeConflict(String key, String value) {
		NamespacedKey ench1 = NamespacedKey.fromString(key);
		NamespacedKey ench2 = NamespacedKey.fromString(value);
		if (Enchantment.getByKey(ench1) == null) {
			return "Couldn't recognize "+key+" as a valid enchantment.";
		}
		if (Enchantment.getByKey(ench2) == null) {
			return "Couldn't recognize "+value+" as a valid enchantment.";
		}
		
		if (conflictSingles.containsKey(ench1.toString())) {
			conflictSingles.get(ench1.toString()).remove(ench2.toString());
			if (conflictSingles.get(ench1.toString()).isEmpty()) {
				conflictSingles.remove(ench1.toString());
			}
		} else if (conflictSingles.containsKey(ench2.toString())) {
			conflictSingles.get(ench2.toString()).remove(ench1.toString());
			if (conflictSingles.get(ench2.toString()).isEmpty()) {
				conflictSingles.remove(ench2.toString());
			}
		} else {
			return ench1.toString()+" and "+ench2.toString()+" already don't conflict with each other.";
		}
		config.set("limits.custom_conflicts", conflictSingles);
		processConflicts();
		return "Enchantment "+key+" no longer conflicts with enchantment "+value+" and viceversa.";
	}

	public static String removeConflictWith(String key) {
		NamespacedKey ench1 = NamespacedKey.fromString(key);
		if (Enchantment.getByKey(ench1) == null) {
			return "Couldn't recognize "+key+" as a valid enchantment.";
		}

		conflictSingles.remove(ench1.toString());

		for (String ckey: new ArrayList<String>(conflictSingles.keySet())){
			conflictSingles.get(ckey).remove(ench1.toString());
			if (conflictSingles.get(ckey).isEmpty()) {
				conflictSingles.remove(ckey);
			}
		}

		config.set("limits.custom_conflicts", conflictSingles);
		processConflicts();
		return "Enchantment "+key+" has been removed from the conflict rules.";
	}
	
	public static String setSmartRolls(Boolean value) {
		smartRolls = value;
		config.set("interaction.smart_enchantment_rolls", value);
		if (smartRolls) {
			return "Only valid enchantments for the item being enchanted will be in the pool of options to randomly pick from.";
		}
		return "Using dumb enchantment rolls, adding enchantments might be less reliable.";
	}

	public static String setDumbTries(Integer value) {
		dumbTries = value;
		config.set("interaction.dumb_roll_tries", value);
		return "If interaction.smart_enchantment_rolls is false, an enchantment will be rolled "+value+" times before giving up.";
	}
	
	public static String setVerbose(Boolean value) {
		verbose = value;
		config.set("interaction.tell_player", value);
		if (verbose) {
			return "Players will get feedback from this plugin.";
		}
		return "Players won't get feedback from this plugin.";
	}
	
	public static String setCreativeFree(Boolean value) {
		creativeFree = value;
		config.set("interaction.creative_free_enchantments", value);
		if (creativeFree) {
			return "Players in creative won't pay for this plugin's services.";
		}
		return "Everyone has to pay, welcome to capitalism.";
	}
	

	public static Boolean getFeature(String key) {
		return features.get(key);
	}
	public static Boolean getUsePermissions() {
		return usePermissions;
	}
	public static Boolean getClickCommon() {
		return clickCommon;
	}
	public static Boolean getMainHandOnly() {
		return mainHandOnly;
	}
	public static String getBlock(String type) {
		return blocks.get(type);
	}
	public static boolean hasBlock(String block) {
		return blocks.containsValue(block);
	}
	public static Boolean getCancelInteraction(String key) {
		return cancelInteractions.get(key);
	}
	public static Boolean getForceCancelInteraction(String key) {
		return forceCancelInteractions.get(key);
	}
	public static Boolean getSuperForceCancelInteraction(String key) {
		return superForceCancelInteractions.get(key);
	}
	public static Boolean getLimit(String key) {
		return limits.get(key);
	}
	public static Boolean getVerbose() {
		return verbose;
	}
	public static Boolean getCreativeFree() {
		return creativeFree;
	}
	public static Set<String> getConflictSinglesNames() {
		return conflictSingles.keySet();
	}
	public static Set<String> getConflictEnchNames() {
		HashSet<String> tmp = new HashSet<String>(conflictSingles.keySet());

		for (ArrayList<String> l:conflictSingles.values()){
			tmp.addAll(l);
		}

		return tmp;
	}
	public static List<String> getConflictSinglesValues(String key) {
		if (conflictSingles.containsKey(key))
			return conflictSingles.get(key);
		return List.of("");
	}
	public static List<String> getConflictsWith(String key) {
		if (conflicts.containsKey(key))
			return conflicts.get(key);
		return List.of("");
	}
	public static Set<String> getMinLevelNames() {
		return GroupsHelper.defaultGroup.minLevels.keySet();
	}

	public static boolean conflicts(String ench1, String ench2) {
		if (conflicts.containsKey(ench1)) {
			return conflicts.get(ench1).contains(ench2);
		} else if (conflicts.containsKey(ench2)) {
			return conflicts.get(ench2).contains(ench1);
		}
		return false;
	}

	public static boolean rollSmart() {
		return smartRolls;
	}

	public static int rollTries() {
		return dumbTries;
	}
	
	public static void saveConfig() {
		config.options().header("All descriptions of values were removed due to the commands being used \n please move/rename this file to generate a new, commented config. \n This is regrettable and due to bukkit's YAML parser \n\n You can also use the /enchmanager help command");
		EnchManagerPlugin.PLUGIN_INSTANCE.saveConfig();
	}

}