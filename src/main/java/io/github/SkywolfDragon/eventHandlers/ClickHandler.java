package io.github.SkywolfDragon.eventHandlers;

import io.github.SkywolfDragon.enchWorkers.*;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import io.github.SkywolfDragon.fileHelpers.ConfigHelper;

import java.util.Map;
import java.util.Optional;

public class ClickHandler implements Listener {
	
	@EventHandler
	public void onBlockClick(PlayerInteractEvent e) {
		
		//we only care if the player clicked a block without sneaking
		if (e.hasBlock() && !e.getPlayer().isSneaking() && e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getHand() == EquipmentSlot.HAND) {
			//a clicked block is our block, check further
			if (ConfigHelper.hasBlock(e.getClickedBlock().getType().getKey().toString())) {
				String common = ConfigHelper.getClickCommon()? e.getClickedBlock().getType().getKey().toString():e.getClickedBlock().getRelative(0, -1, 0).getType().getKey().toString();
				String functional = ConfigHelper.getClickCommon()? e.getClickedBlock().getRelative(0, 1, 0).getType().getKey().toString():e.getClickedBlock().getType().getKey().toString();
				
				if (common.equals(ConfigHelper.getBlock("common"))) {
					//we know one of the blocks is the common block, now we're getting somewhere
					
					if (functional.equals(ConfigHelper.getBlock("add")) && ConfigHelper.getFeature("add")) {
						//check permissions are in order
						if (!ConfigHelper.getUsePermissions() || e.getPlayer().hasPermission("enchantmentmanagement.use.add")) {
							//make sure they've got something in their hands
							if (isPlayerHoldingOneItem(e.getPlayer())) {
								//we add the enchantments
								double price = PayHelper.findPrice(e.getPlayer(), getPlayerHoldItem(e.getPlayer()),"add");
								if (PayHelper.canPay(e.getPlayer(), price)) {
									EnchResult result = EnchAdder.addEnchantment(e.getPlayer(), getPlayerHoldItem(e.getPlayer()));
									//if the enchantment didn't go through, you don't pay
									if (result.wasSuccessful()) {
										PayHelper.payUp(e.getPlayer(), price);
										sendMessage(e.getPlayer(), result.getMessage());
										cancelEvent(e,"add");
									} else {
										sendMessage(e.getPlayer(), result.getMessage());
									}
								} else {
									sendMessage(e.getPlayer(),"You aren't strong enough to perform this magic.");
								}
							} else {
								sendMessage(e.getPlayer(),getEmptyHandMessage());
							}
						} else if (ConfigHelper.getUsePermissions()) {
							sendMessage(e.getPlayer(), "You can't do that.");
						}
						
						forceCancelEvent(e,"add");
						
						
					} else if (functional.equals(ConfigHelper.getBlock("transfer")) && ConfigHelper.getFeature("transfer")) {
						//check permissions are in order
						if (!ConfigHelper.getUsePermissions() || e.getPlayer().hasPermission("enchantmentmanagement.use.transfer")) {
							//make sure they've got something in their hands
							if (isPlayerHoldingTwoItems(e.getPlayer())) {
								//we trans them genders
								double price = PayHelper.findPrice(e.getPlayer(), e.getPlayer().getInventory().getItemInMainHand(), e.getPlayer().getInventory().getItemInOffHand());
								if (PayHelper.canPay(e.getPlayer(), price)) {
									EnchResult result = EnchTransferer.transferEnchantment(e.getPlayer(), e.getPlayer().getInventory().getItemInMainHand(), e.getPlayer().getInventory().getItemInOffHand());
									//if the transfer didn't go through, you don't pay
									if (result.wasSuccessful()) {
										PayHelper.payUp(e.getPlayer(), price);
										sendMessage(e.getPlayer(), result.getMessage());
										cancelEvent(e,"transfer");
									} else {
										sendMessage(e.getPlayer(), result.getMessage());
									}
								} else {
									sendMessage(e.getPlayer(),"You aren't strong enough to perform this magic.");
								}
							} else if (isPlayerHoldingOneItem(e.getPlayer())) {
								ItemStack item = getPlayerHoldItem(e.getPlayer());
								Optional<Map.Entry<Enchantment, Integer>> shfl = item.getEnchantments().entrySet().stream().findFirst();
								if (shfl.isPresent()) {
									item.removeEnchantment(shfl.get().getKey());
									item.addUnsafeEnchantment(shfl.get().getKey(), shfl.get().getValue());
								}
							} else {
								sendMessage(e.getPlayer(),getEmptyHandMessage());
							}
						} else if (ConfigHelper.getUsePermissions()) {
							sendMessage(e.getPlayer(), "You can't do that.");
						}
						
						forceCancelEvent(e,"transfer");
						
						
					} else if (functional.equals(ConfigHelper.getBlock("remove")) && ConfigHelper.getFeature("remove")) {
						//check permissions are in order
						if (!ConfigHelper.getUsePermissions() || e.getPlayer().hasPermission("enchantmentmanagement.use.remove")) {
							//make sure they've got something in their hands
							if (isPlayerHoldingOneItem(e.getPlayer())) {
								//we remove the enchantments
								double price = PayHelper.findPrice(e.getPlayer(), getPlayerHoldItem(e.getPlayer()), "remove");
								if (PayHelper.canPay(e.getPlayer(), price)) {
									EnchResult result = EnchRemover.removeEnchantment(e.getPlayer(), getPlayerHoldItem(e.getPlayer()));
									//if the enchantment didn't go through, you don't pay
									if (result.wasSuccessful()) {
										PayHelper.payUp(e.getPlayer(), price);
										sendMessage(e.getPlayer(), result.getMessage());
										cancelEvent(e,"remove");
									} else {
										sendMessage(e.getPlayer(), result.getMessage());
									}
								} else {
									sendMessage(e.getPlayer(),"You aren't strong enough to perform this magic.");
								}
							} else {
								sendMessage(e.getPlayer(), getEmptyHandMessage());
							}
						} else if (ConfigHelper.getUsePermissions()) {
							sendMessage(e.getPlayer(), "You can't do that.");
						}
						
						forceCancelEvent(e,"remove");
						
					}
				}

				superForceCancelEvent(e);
			}
		}
	}
	
	private void sendMessage(Player p, String msg){
		if (ConfigHelper.getVerbose()) {
			p.sendMessage(msg);
		}
	}

	private static ItemStack getPlayerHoldItem(Player p) {
		if (p.getInventory().getItemInMainHand().getType() != Material.AIR) {
			return p.getInventory().getItemInMainHand();
		} else if (p.getInventory().getItemInOffHand().getType() != Material.AIR && !ConfigHelper.getMainHandOnly()) {
			return p.getInventory().getItemInOffHand();
		}
		return null;
	}
	
	private static boolean isPlayerHoldingOneItem(Player p) {
		return (p.getInventory().getItemInMainHand().getType() != Material.AIR || (p.getInventory().getItemInOffHand().getType() != Material.AIR && !ConfigHelper.getMainHandOnly()));
	}
	
	private static boolean isPlayerHoldingTwoItems(Player p) {
		return (p.getInventory().getItemInMainHand().getType() != Material.AIR && p.getInventory().getItemInOffHand().getType() != Material.AIR);
	}
	
	private String getEmptyHandMessage() {
		return ConfigHelper.getMainHandOnly()? "You need an item on your main hand to do that.":"You need an item in either hand to do that.";
	}
	
	private void cancelEvent(PlayerInteractEvent e, String op) {
		if (ConfigHelper.getCancelInteraction("common") && e.getClickedBlock().getType().getKey().toString().equals(ConfigHelper.getBlock("common"))) {
			e.setCancelled(true);
			return;
		}
		if (ConfigHelper.getCancelInteraction(op) && e.getClickedBlock().getType().getKey().toString().equals(ConfigHelper.getBlock(op))) {
			e.setCancelled(true);
		}
	}
	private void forceCancelEvent(PlayerInteractEvent e, String op) {
		if (ConfigHelper.getForceCancelInteraction("common") && e.getClickedBlock().getType().getKey().toString().equals(ConfigHelper.getBlock("common"))) {
			e.setCancelled(true);
			return;
		}
		if (ConfigHelper.getForceCancelInteraction(op) && e.getClickedBlock().getType().getKey().toString().equals(ConfigHelper.getBlock(op))) {
			e.setCancelled(true);
		}
	}
	private void superForceCancelEvent(PlayerInteractEvent e) {
		if (ConfigHelper.getSuperForceCancelInteraction("common") && e.getClickedBlock().getType().getKey().toString().equals(ConfigHelper.getBlock("common"))) {
			e.setCancelled(true);
			return;
		}
		if (ConfigHelper.getSuperForceCancelInteraction("add") && e.getClickedBlock().getType().getKey().toString().equals(ConfigHelper.getBlock("add"))) {
			e.setCancelled(true);
			return;
		}
		if (ConfigHelper.getSuperForceCancelInteraction("transfer") && e.getClickedBlock().getType().getKey().toString().equals(ConfigHelper.getBlock("transfer"))) {
			e.setCancelled(true);
			return;
		}
		if (ConfigHelper.getSuperForceCancelInteraction("remove") && e.getClickedBlock().getType().getKey().toString().equals(ConfigHelper.getBlock("remove"))) {
			e.setCancelled(true);
		}
	}
}
