package io.github.SkywolfDragon.enchWorkers;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.SkywolfDragon.fileHelpers.ConfigHelper;
import io.github.SkywolfDragon.fileHelpers.GroupsHelper;

public abstract class EnchAdder {
	public static EnchResult addEnchantment(Player p, ItemStack i) {
		if (ConfigHelper.rollSmart()) {
			//roll smartly
			Enchantment e = GroupsHelper.rollEnch(p, i);
			
			//if there is an enchantment, enchant
			if (e != null) {
				EnchHelper.enchant(i,e);
				return new EnchResult(true, "The item now has "+e.getKey().getKey().replace("_", " ")+" "+i.getEnchantmentLevel(e));
			}
		} else {
			//roll like a dumbass
			Enchantment e = GroupsHelper.rollEnch(p);
			int tries = ConfigHelper.rollTries();
			
			//no, no, keep rolling, you'll get it eventually
			while (!(EnchHelper.isValidToAdd(p, i, e, "add")) && tries > 0) {
				e = GroupsHelper.rollEnch(p);
			}
			//finally.
			if (EnchHelper.isValidToAdd(p, i, e, "add")) {
				EnchHelper.enchant(i,e);
				return new EnchResult(true, "The item now has "+e.getKey().getKey().replace("_", " ")+" "+i.getEnchantmentLevel(e));
			}
		}
		//for when either there's no more possible enchantments, or the dumb roll gave up
		return new EnchResult(false, "The item fails to gain an enchantment.");
	}
	
}
