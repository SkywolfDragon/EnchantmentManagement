package io.github.SkywolfDragon.enchWorkers;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;

import io.github.SkywolfDragon.EnchManagerPlugin;
import io.github.SkywolfDragon.fileHelpers.ConfigHelper;
import io.github.SkywolfDragon.fileHelpers.GroupsHelper;

public abstract class PayHelper {
	//find out if they're poor
	public static boolean canPay(Player p, double price) {
		if (ConfigHelper.getCreativeFree() && p.getGameMode() == GameMode.CREATIVE) {
			return true;
		}
		if (GroupsHelper.getUseCash(p) && EnchManagerPlugin.vaultFound){
			try {
				return EnchManagerPlugin.econ.getBalance(p) > price;
			} catch (Exception e) {
				return false;
			}
		} else {
			return calcTotalXP(p) > price;
		}
	}
	
	public static void payUp(Player p, double price) {
		if (ConfigHelper.getCreativeFree() && p.getGameMode() == GameMode.CREATIVE) {
			return;
		}
		if (GroupsHelper.getUseCash(p) && EnchManagerPlugin.vaultFound){
			try {
				if (price >= 0) {
					EnchManagerPlugin.econ.withdrawPlayer(p, Math.abs(price));
				} else {
					EnchManagerPlugin.econ.depositPlayer(p, Math.abs(price));
				}
			} catch (Exception e) {
				EnchManagerPlugin.LOGGER.warning("Error while processing enchantment payment for "+p.getDisplayName());
				EnchManagerPlugin.LOGGER.warning(e.getLocalizedMessage());
			}
		} else {
			p.giveExp(-(int)Math.floor(price));
		}
	}
	 
	//why is minecraft xp so convoluted
	public static int calcTotalXP(Player p) {
		int lvl = p.getLevel()+1;
		if (lvl <= 16) {
			return (int)Math.floor(Math.pow(lvl,2)+6*lvl) - p.getExpToLevel();
		} else if (lvl <= 32) {
			return (int)Math.floor(2.5*Math.pow(lvl,2)-40.5*lvl+360) - p.getExpToLevel();
		} else {
			return (int)Math.floor(4.5*Math.pow(lvl,2)-162.5*lvl+2220) - p.getExpToLevel();
		}
	}
	
	//anything can be enchanted, for a price.
	public static double findPrice(Permissible p, ItemStack is, String category) {
		int totalEnchs = EnchHelper.countEnchs(p, is, category);
		
		return (GroupsHelper.getBaseCost(p, category)
				+(GroupsHelper.getCostAdd(p, category)*totalEnchs))
				*Math.pow(GroupsHelper.getCostMult(p, category),totalEnchs);
	}
	//a convenience for the transfer process
	public static double findPrice(Permissible p, ItemStack is1, ItemStack is2) {
		int totalEnchs = EnchHelper.countEnchs(p, is1, "transfer")+EnchHelper.countEnchs(p, is2, "transfer");
		
		return (GroupsHelper.getBaseCost(p, "transfer")
				+(GroupsHelper.getCostAdd(p, "transfer")*totalEnchs))
				*Math.pow(GroupsHelper.getCostMult(p, "transfer"),totalEnchs);
	}
	
}
