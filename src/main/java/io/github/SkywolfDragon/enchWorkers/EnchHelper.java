package io.github.SkywolfDragon.enchWorkers;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;

import io.github.SkywolfDragon.fileHelpers.ConfigHelper;
import io.github.SkywolfDragon.fileHelpers.GroupsHelper;

public abstract class EnchHelper {

	public static final ArrayList<String> enchNames = new ArrayList<String>();
	public static final ArrayList<String> enchNamesWDefault = new ArrayList<String>();

	public static void buildEnchNames(){
		for (Enchantment e:Enchantment.values()) {
			enchNames.add(e.getKey().toString());
			enchNamesWDefault.add(e.getKey().toString());
		}
		enchNamesWDefault.add("default");
	}

	public static boolean isValidToAdd(Player p, ItemStack items, Enchantment ench, String op, int lvl) {
		
		//check to see that the player isn't duplicating enchantments
		if (ConfigHelper.getLimit("prevent_dupe") && items.getAmount() > 1) {
			//System.out.println("prevent_dupe");
			return false;
		}
		
		//check if the player is boring
		if (ConfigHelper.getLimit("safe_mode") && !ench.canEnchantItem(items)) {
			//System.out.println("safe_mode");
			return false;
		}
		
		//check if the item has the enchantment already
		if (items.containsEnchantment(ench)) {
			//if there's a level limit, enforce it
			if (ConfigHelper.getLimit("vanilla_level_limits") && items.getEnchantmentLevel(ench)+lvl > ench.getMaxLevel()) {
				//System.out.println("vanilla_level_limits w/enchant");
				return false;
			}
			if (items.getEnchantmentLevel(ench)+lvl > GroupsHelper.getMaxLevel(p, ench)) {
				//System.out.println("custom_level_limits w/enchant");
				return false;
			}
			
		} else {
			//enforce conflicts
			for (Enchantment e:items.getEnchantments().keySet()) {
				//vanilla
				if (ConfigHelper.getLimit("enforce_vanilla_conflicts") && ench.conflictsWith(e)) {
					//System.out.println("vanilla_conflict");
					return false;
				}
				//custom
				if (ConfigHelper.conflicts(ench.getKey().toString(), e.getKey().toString())) {
					//System.out.println("custom_conflict");
					return false;
				}
			}
			
			//if there's a level limit, enforce it
			if (ConfigHelper.getLimit("vanilla_level_limits") && lvl > ench.getMaxLevel()) {
				//System.out.println("vanilla_level_limits no ench");
				return false;
			}
			if (lvl > GroupsHelper.getMaxLevel(p, ench)) {
				//System.out.println("custom_level_limits no ench");
				return false;
			}
		}
		
		//do the min level check
		//System.out.println("player can't");
		return GroupsHelper.canPlayer(p, ench, op);
	}
	
	//a shorthand of the previous function
	public static boolean isValidToAdd(Player p, ItemStack i, Enchantment e, String op) {
		return isValidToAdd(p,i,e,op,1);
	}
	
	
	

	
	//we ask no questions, we just do the dirty work
	public static void enchant(ItemStack i, Enchantment e, int lvls) {
		if (i.containsEnchantment(e)) {
			i.addUnsafeEnchantment(e, i.getEnchantmentLevel(e)+lvls);
		} else {
			i.addUnsafeEnchantment(e, lvls);
		}	
	}
	public static void enchant(ItemStack i, Enchantment e) {
		enchant(i,e,1);
	}

	public static void disenchant(ItemStack i, Enchantment ench, int lvl) {
		if (i.getEnchantmentLevel(ench) == lvl) {
			i.removeEnchantment(ench);
		} else if (i.getEnchantmentLevel(ench) > lvl) {
			i.addUnsafeEnchantment(ench, i.removeEnchantment(ench)-lvl);
		}
	}
	
	public static Entry<Enchantment, Integer> disenchantCandidate(Player p, ItemStack i, String op) {
		//if there are no enchantments, we can't remove an enchantment
		if (!i.getEnchantments().isEmpty()) {
			ArrayList<Entry<Enchantment,Integer>> enchs = new ArrayList<Entry<Enchantment,Integer>>(i.getEnchantments().entrySet());
			//if there are, get the first possible one, whichever one
			for (Entry<Enchantment,Integer> e: enchs) {
				//do a quick level check
				if (GroupsHelper.canPlayer(p, e.getKey(), op)) {
					return e;
				}
			}
		}
		return null;
	}
	
	public static int countEnchs(Permissible p, ItemStack is, String category) {
		//to count the enchantments
		int totalEnchs = 0;
		if (GroupsHelper.getScalingPerLevel(p, category)) {
			//if the per level setting is on, we count the levels
			for (Integer e:is.getEnchantments().values()) {
				totalEnchs += e;
			}
		} else {
			//if it's off, just the enchantments
			totalEnchs = is.getEnchantments().size();
		}
		return totalEnchs;
	}



	
}
