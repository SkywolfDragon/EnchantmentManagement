package io.github.SkywolfDragon.enchWorkers;

import java.util.Map.Entry;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.SkywolfDragon.fileHelpers.ConfigHelper;

public abstract class EnchTransferer {
	public static EnchResult transferEnchantment(Player p,ItemStack from, ItemStack to) {
		//grab the first enchantment from the source item
		Entry<Enchantment, Integer> enchEntry = EnchHelper.disenchantCandidate(p,from,"transfer");
		//if none, then no
		if (enchEntry == null) {
			return new EnchResult(false, "No valid enchantment to transfer.");
		}
		
		Enchantment ench = enchEntry.getKey();
		int lvl = ConfigHelper.getLimit("one_level_at_a_time")? 1:enchEntry.getValue();
		if (EnchHelper.isValidToAdd(p, to, ench,"Transfer")) {
			EnchHelper.enchant(to, ench, lvl);
			EnchHelper.disenchant(from, ench, lvl);
			String l = lvl == 1? " level of ":" levels of ";
			return new EnchResult(true, "Transfered "+lvl+l+ench.getKey().getKey().replace("_", " "));
		}
		
		return new EnchResult(false, "The recieving item failed to gain the enchantment.");
	}
}
