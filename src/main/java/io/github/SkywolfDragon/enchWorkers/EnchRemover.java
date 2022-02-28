package io.github.SkywolfDragon.enchWorkers;

import java.util.Map.Entry;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.SkywolfDragon.fileHelpers.ConfigHelper;

public abstract class EnchRemover {
	public static EnchResult removeEnchantment(Player p,ItemStack i) {
		Entry<Enchantment,Integer> ench = EnchHelper.disenchantCandidate(p, i, "remove");
		
		if (ench != null) {
			EnchHelper.disenchant(i, ench.getKey(), ConfigHelper.getLimit("one_level_at_a_time")? 1:ench.getValue());
			String s =  i.containsEnchantment(ench.getKey())? "a level of ":" ";
			return new EnchResult(true, "The item has lost "+s+ench.getKey().getKey().getKey().replace("_", " "));
		}
		return new EnchResult(false, "The item refuses to loose an enchantment.");
	}
	

	
}
