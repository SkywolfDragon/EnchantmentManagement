/*
*EnchantmentManagement Bukkit/Spigot plugin
*    Copyright (C) 2022  SkywolfDragon
*
*    This program is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package io.github.SkywolfDragon;

import java.util.logging.Logger;

import io.github.SkywolfDragon.enchWorkers.EnchHelper;
import io.github.SkywolfDragon.eventHandlers.GroupsCommandHandler;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.SkywolfDragon.eventHandlers.ClickHandler;
import io.github.SkywolfDragon.eventHandlers.CostCommandHandler;
import io.github.SkywolfDragon.eventHandlers.ManageCommandHandler;
import io.github.SkywolfDragon.fileHelpers.ConfigHelper;
import io.github.SkywolfDragon.fileHelpers.GroupsHelper;
import net.milkbowl.vault.economy.Economy;

public class EnchManagerPlugin extends JavaPlugin {
	
	public static EnchManagerPlugin PLUGIN_INSTANCE;
	public static Logger LOGGER;
	
	public static boolean vaultFound = false;
	public static Economy econ = null;
	
	@Override
	public void onEnable() {
		//get some of them sweet sweet variables
		PLUGIN_INSTANCE = getPlugin(this.getClass());
		LOGGER = this.getLogger();
		
		//start up the files
		ConfigHelper.startUpConfiguration();
		ConfigHelper.buildBlockNames();
		GroupsHelper.startUpGroups();
		EnchHelper.buildEnchNames();
		//register group permissions
		GroupsHelper.registerPermissions();
		
		//register enchantments, i think plugin.yml does this, but you can't be too sure
		this.getCommand("enchmanagementconfig").setExecutor(new ManageCommandHandler());
		this.getCommand("enchmanagementgroups").setExecutor(new GroupsCommandHandler());
		this.getCommand("enchmanagementcost").setExecutor(new CostCommandHandler());

		
		//register the clicc event
		getServer().getPluginManager().registerEvents(new ClickHandler(), this);
		LOGGER.info(this.checkForVault());

	}

	public String checkForVault() {
		//find and tell on vault
		vaultFound = getServer().getPluginManager().getPlugin("Vault") != null;
		econ = vaultFound? getServer().getServicesManager().getRegistration(Economy.class).getProvider() : null;
		vaultFound = vaultFound && (econ != null);
		if (vaultFound) {
			return "Found vault with a valid economy plugin!";
		} else {
			return"Didn't find vault with a valid economy plugin!";
		}
	}

	@Override
	public void onDisable() {
		
	}
	
}
