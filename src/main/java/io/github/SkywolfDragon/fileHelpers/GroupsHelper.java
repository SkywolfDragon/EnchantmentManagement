package io.github.SkywolfDragon.fileHelpers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.permissions.DefaultPermissions;

import io.github.SkywolfDragon.EnchManagerPlugin;
import io.github.SkywolfDragon.enchWorkers.EnchHelper;

public abstract class GroupsHelper {
	
	private static final Random enchRNG = new Random();
	
	private static FileConfiguration groupsFile;
	
	public static PermissionGroup defaultGroup;

	public static final LinkedHashMap<String, PermissionGroup> groups = new LinkedHashMap<String, PermissionGroup>();
	
	
	public static void startUpGroups() {
		File groupsFileRaw = new File(EnchManagerPlugin.PLUGIN_INSTANCE.getDataFolder(),"costgroups.yml");
		if (!groupsFileRaw.exists()) {
			EnchManagerPlugin.PLUGIN_INSTANCE.saveResource("costgroups.yml",false);
		}
		
		groupsFile = new YamlConfiguration();
		try {
			groupsFile.load(groupsFileRaw);
		} catch (IOException | InvalidConfigurationException e) {
			EnchManagerPlugin.PLUGIN_INSTANCE.getLogger().warning("Couldn't read groups.yml because: "+e.getLocalizedMessage());
			EnchManagerPlugin.PLUGIN_INSTANCE.getLogger().warning("Groups won't be loaded, fix the yml and use /enchgroups reload");
			return;
		}
		
		if (groupsFile.getMapList("groups") != null) {
			for (Object t: groupsFile.getList("groups")) {
				if (t instanceof Map) {
					for(Entry<?, ?> u:((Map<?,?>)t).entrySet()) {
						if (!(u.getKey() instanceof String)) continue;
						
						if (u.getValue() == null || !(u.getValue() instanceof Map))
							groups.put((String) u.getKey(),new PermissionGroup((String)u.getKey()));
						else
							groups.put((String) u.getKey(),new PermissionGroup((String)u.getKey(),(Map<?, ?>) u.getValue()));
					}
				} else if (t instanceof String) {
					groups.put((String) t,new PermissionGroup((String)t));
				}
			}
		}
		
		if (groups.size() > 0) {
			EnchManagerPlugin.LOGGER.info("Groups loaded, in order of priority:");
			for (String g:groups.keySet()) {
				EnchManagerPlugin.LOGGER.info(g);
			}
			registerPermissions();
		} else {
			EnchManagerPlugin.LOGGER.info("No cost groups found in costgroups.yml");
		}
	}
	
	
	
	public static void setDefaultGroup(ConfigurationSection config) {
		defaultGroup = new PermissionGroup(config);
	}



	public static boolean getUseCash(Permissible p) {
		for (PermissionGroup group:groups.values()) {
			if (p.hasPermission("enchantmentmanagement.costgroup."+group.name) && group.useCash != null) {
				return group.useCash;
			}
		}
		return defaultGroup.useCash;
	}



	public static double getBaseCost(Permissible p, String category) {
		for (PermissionGroup group:groups.values()) {
			if (p.hasPermission("enchantmentmanagement.costgroup."+group.name) && group.get(category).cost != null) {
				return group.get(category).cost;
			}
		}
		return defaultGroup.get(category).cost;
	}
	public static double getCostAdd(Permissible p, String category) {
		for (PermissionGroup group:groups.values()) {
			if (p.hasPermission("enchantmentmanagement.costgroup."+group.name) && group.get(category).add != null) {
				return group.get(category).add;
			}
		}
		return defaultGroup.get(category).add;
	}
	public static double getCostMult(Permissible p, String category) {
		for (PermissionGroup group:groups.values()) {
			if (p.hasPermission("enchantmentmanagement.costgroup."+group.name) && group.get(category).mult != null) {
				return group.get(category).mult;
			}
		}
		return defaultGroup.get(category).mult;
	}
	public static boolean getScalingPerLevel(Permissible p, String category) {
		for (PermissionGroup group:groups.values()) {
			if (p.hasPermission("enchantmentmanagement.costgroup."+group.name) && group.get(category).perLevel != null) {
				return group.get(category).perLevel;
			}
		}
		return defaultGroup.get(category).perLevel;
	}
	public static Integer getMaxLevel(Permissible p, Enchantment ench) {
		//if there isn't a max level for the enchantment, returns MAX_INT
		for (PermissionGroup group:groups.values()) {
			if (p.hasPermission("enchantmentmanagement.costgroup."+group.name) && group.maxLevels.containsKey(ench.getKey().toString())) {
				return group.maxLevels.get(ench.getKey().toString());
			}
		}
		if (defaultGroup.maxLevels.containsKey(ench.getKey().toString())){
			return defaultGroup.maxLevels.get(ench.getKey().toString());
		}
		return Integer.MAX_VALUE;
	}
	public static boolean canPlayer(Player p, Enchantment e, String op) {
		for (PermissionGroup group:groups.values()) {
			if (p.hasPermission("enchantmentmanagement.costgroup."+group.name) && group.minLevels.containsKey(e.getKey().toString())) {
				if (group.minLevels.get(e.getKey().toString()).containsKey(op)) {
					//A positive number means you can only manipulate it AT OR AFTER that level
					if (group.minLevels.get(e.getKey().toString()).get(op) > 0) {
						return p.getLevel() >= group.minLevels.get(e.getKey().toString()).get(op);
					}
					//A negative number means you can only manipulate it BEFORE OR AT that level
					if (group.minLevels.get(e.getKey().toString()).get(op) < 0) {
						return p.getLevel() <= -group.minLevels.get(e.getKey().toString()).get(op);
					}
					//A 0 means the enchantment won't be added/transfered/removed, depending on what subsections the entry has
					return false;
				}
			}
		}

		if (defaultGroup.minLevels.containsKey(e.getKey().toString())) {
			if (defaultGroup.minLevels.get(e.getKey().toString()).containsKey(op)) {
				if (defaultGroup.minLevels.get(e.getKey().toString()).get(op) > 0) {
					return p.getLevel() >= defaultGroup.minLevels.get(e.getKey().toString()).get(op);
				} else if (defaultGroup.minLevels.get(e.getKey().toString()).get(op) < 0) {
					return p.getLevel() <= -defaultGroup.minLevels.get(e.getKey().toString()).get(op);
				}
				return false;
			}
		}
		return true;
	}


	//do a smart roll
	public static Enchantment rollEnch(Player p,ItemStack i) {
		//build the enchantment pool
		List<Enchantment> checklist = Arrays.asList(Enchantment.values());
		LinkedHashMap<String, Integer> tmp = new LinkedHashMap<String, Integer>();
		
		for (PermissionGroup group:groups.values()) {
			if (p.hasPermission("enchantmentmanagement.costgroup."+group.name) && group.hasWeights) {
				if (group.hasDefaultWeight) {
					tmp = PermissionGroup.completeWeights(tmp, group.addWeights, group.defaultWeight, p, i);
					checklist.clear();
					break;
				}
				for (Entry<String, Integer> weight:group.addWeights.entrySet()) {
					if (!tmp.containsKey(weight.getKey()) &&  weight.getValue()!=null)
						if (EnchHelper.isValidToAdd(p, i, Enchantment.getByKey(NamespacedKey.fromString(weight.getKey())), "add"))
							tmp.put(weight.getKey(), weight.getValue());
						else
							tmp.put(weight.getKey(), 0);
				}
				if (checklist.size() == 0) break;
			}
		}
		
		if (checklist.size() > 0) tmp = PermissionGroup.completeWeights(tmp, defaultGroup.addWeights, defaultGroup.defaultWeight, p, i);
		
		int w = 0;
		for (Entry<String,Integer> e: tmp.entrySet()) {
			if (EnchHelper.isValidToAdd(p,i, Enchantment.getByKey(NamespacedKey.fromString(e.getKey())),"add")) {
				tmp.put(e.getKey(),e.getValue());
				w += e.getValue();
			}
		}
		
		if (w == 0) return null;
		
		//do the weighted roll
		int num = enchRNG.nextInt(w);
		for (String s:tmp.keySet()) {
			num -= tmp.get(s);
			if (num < 0) {
				return Enchantment.getByKey(NamespacedKey.fromString(s));
			}
		}
		return null;
	}
	
	//do a dumb roll
	public static Enchantment rollEnch(Player p) {
		
		//build the enchantment pool
		List<Enchantment> checklist = Arrays.asList(Enchantment.values());
		LinkedHashMap<String, Integer> tmp = new LinkedHashMap<String, Integer>();
		
		for (PermissionGroup group:groups.values()) {
			if (p.hasPermission("enchantmentmanagement.costgroup."+group.name) && group.hasWeights) {
				if (group.hasDefaultWeight) {
					tmp = PermissionGroup.completeWeights(tmp, group.addWeights, group.defaultWeight);
					checklist.clear();
					break;
				}
				for (Entry<String, Integer> weight:group.addWeights.entrySet()) {
					if (!tmp.containsKey(weight.getKey()) &&  weight.getValue()!=null)
						tmp.put(weight.getKey(), weight.getValue());
				}
				if (checklist.size() == 0) break;
			}
		}
		
		if (checklist.size() > 0) tmp = PermissionGroup.completeWeights(tmp, defaultGroup.addWeights, defaultGroup.defaultWeight);
		
		int w = 0;
		for (Entry<String,Integer> e: tmp.entrySet()) 
			w += e.getValue();
		
		if (w == 0) return null;
		
		//do the weighted roll
		int num = enchRNG.nextInt(w);
		for (String s:tmp.keySet()) {
			num -= tmp.get(s);
			if (num < 0)
				return Enchantment.getByKey(NamespacedKey.fromString(s));
		}
		return null;
	}
	
	public static String saveGroupsFile() {
		//make a list of all the maps
		ArrayList<Map<?,?>> tmp = new ArrayList<Map<?,?>>();
		for (PermissionGroup p:groups.values()) {
			tmp.add(p.toMap());
		}
		//overwrite it
		groupsFile.set("groups", tmp);
		//serialize their ass
		try {
			groupsFile.options().header("All the comments in this file have been removed due to the use of the /enchgroups command \nTo see a commented version of this file, remove/rename this file and let the plugin generate a new, commented one \nOr use the /enchgroups help command in-game");
			groupsFile.save(new File(EnchManagerPlugin.PLUGIN_INSTANCE.getDataFolder(),"costgroups.yml"));
		} catch (IOException e) {
			return e.getLocalizedMessage();
		}
		return null;
	}

	public static void registerPermissions() {
		for (String p:groups.keySet()) {
			DefaultPermissions.registerPermission("enchmanagement.costgroup."+p, "Autogenerated permission for group "+p+" declared in costgroups.yml", PermissionDefault.FALSE);
		}
	}


	public static void restartGroups(CommandSender sender) {
		File groupsFileRaw = new File(EnchManagerPlugin.PLUGIN_INSTANCE.getDataFolder(), "costgroups.yml");
		if (!groupsFileRaw.exists()) {
			EnchManagerPlugin.PLUGIN_INSTANCE.saveResource("costgroups.yml", false);
		}

		groupsFile = new YamlConfiguration();
		try {
			groupsFile.load(groupsFileRaw);
		} catch (IOException | InvalidConfigurationException e) {
			sender.sendMessage(ChatColor.DARK_RED+"Couldn't read groups.yml because: " + e.getLocalizedMessage());
			sender.sendMessage(ChatColor.YELLOW+"Groups"+ChatColor.DARK_RED+" won't be reloaded.");
			return;
		}

		if (groupsFile.getMapList("groups") != null) {
			for (Object t : groupsFile.getList("groups")) {
				if (t instanceof Map) {
					for (Entry<?, ?> u : ((Map<?, ?>) t).entrySet()) {
						if (!(u.getKey() instanceof String)) continue;

						if (u.getValue() == null || !(u.getValue() instanceof Map))
							groups.put((String) u.getKey(),new PermissionGroup((String) u.getKey()));
						else
							groups.put((String) u.getKey(),new PermissionGroup((String) u.getKey(), (Map<?, ?>) u.getValue()));
					}
				} else if (t instanceof String) {
					groups.put((String) t,new PermissionGroup((String) t));
				}
			}
		}

		if (groups.size() > 0) {
			sender.sendMessage(ChatColor.YELLOW +"Groups"+ChatColor.GRAY+" loaded, in order of priority:");
			for (String g : groups.keySet()) {
				sender.sendMessage(ChatColor.WHITE+" - "+ChatColor.YELLOW +g);
			}
			registerPermissions();
		} else {
			sender.sendMessage(ChatColor.DARK_RED +"No cost groups found in costgroups.yml");
		}
	}
}
