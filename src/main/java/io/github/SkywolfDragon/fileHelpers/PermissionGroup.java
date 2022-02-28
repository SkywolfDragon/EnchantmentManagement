package io.github.SkywolfDragon.fileHelpers;

import io.github.SkywolfDragon.EnchManagerPlugin;
import io.github.SkywolfDragon.enchWorkers.EnchHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;



public class PermissionGroup {

	public final HashMap<String, Integer> maxLevels =  new HashMap<String, Integer>();
	public final LinkedHashMap<String, Integer> addWeights =  new LinkedHashMap<String, Integer>();
	public final HashMap<String, HashMap<String, Integer>> minLevels =  new HashMap<String, HashMap<String, Integer>>();
	public Integer defaultWeight = null;
	public boolean hasWeights = false;
	public boolean hasDefaultWeight = false;
	
	public String name = null;
	
	public Boolean useCash = null;
	
	public CostBundle add = new CostBundle();
	public CostBundle tra = new CostBundle();
	public CostBundle rem = new CostBundle();
	
	public CostBundle get(String group) {
		//an implementation of an old system i'm too lazy to do propper
		switch (group) {
		case "add":
			return this.add;
		case "transfer":
			return this.tra;
		case "remove":
			return this.rem;
		}
		return null;
	}

	public boolean containsKey(String group) {
		return group.equals("add") || group.equals("transfer") || group.equals("remove");
	}
	
	
	public Map<String,Object> toMap(){
		Map<String,Object> tmpBase = new HashMap<String, Object>();
		Map<String,Object> tmp = new HashMap<String, Object>();
		
		tmp.put("use_money_instead_of_xp", this.useCash);
		tmp.put("add", this.add.toMap());
		tmp.put("transfer", this.tra.toMap());
		tmp.put("remove", this.rem.toMap());
		tmp.put("max_enchantment_levels", this.maxLevels);
		this.addWeights.put("default", defaultWeight);
		tmp.put("enchantment_add_weights", this.addWeights);
		this.addWeights.remove("default");
		tmp.put("min_player_levels", minLevels);
		tmpBase.put(this.name, tmp);
		
		return tmpBase;
	}

	
	public PermissionGroup(ConfigurationSection config) {
		
		this.useCash = config.getBoolean("costs.use_money_instead_of_xp");
		
		this.add = new CostBundle(
				config.getDouble("costs.add.cost"),
				config.getDouble("costs.add.cost_multiplier"),
				config.getDouble("costs.add.cost_adder"), 
				config.getBoolean("costs.add.scaling_per_enchantment_level")
		);
		this.tra = new CostBundle(
				config.getDouble("costs.transfer.cost"),
				config.getDouble("costs.transfer.cost_multiplier"),
				config.getDouble("costs.transfer.cost_adder"),
				config.getBoolean("costs.transfer.scaling_per_enchantment_level")
		);
		this.rem = new CostBundle(
				config.getDouble("costs.remove.cost"),
				config.getDouble("costs.remove.cost_multiplier"),
				config.getDouble("costs.remove.cost_adder"),
				config.getBoolean("costs.remove.scaling_per_enchantment_level")
		);
		
		//load enchantment_add_weights
		//oh, boy, an iterator!
		for (String e:config.getConfigurationSection("interaction.enchantment_add_weights").getKeys(false)) {
			if (e.equalsIgnoreCase("default")) {
				this.defaultWeight = config.getInt("interaction.enchantment_add_weights."+e);
				this.hasDefaultWeight = this.defaultWeight != null;
			} else if (Enchantment.getByKey(NamespacedKey.fromString(e)) == null) {
				EnchManagerPlugin.LOGGER.warning("config.yml, interaction, enchantment_add_weights: Couldn't recognize "+e+" as an existing enchantment or default, ignoring.");
			} else {
				this.addWeights.put(e, config.getInt("interaction.enchantment_add_weights."+e));
			}
		}
		
		
		if (!this.hasDefaultWeight) {
			EnchManagerPlugin.LOGGER.severe("Couldn't find default in field interaction, enchantment_add_weights in config.yml, defaulting the default to 1.");
			this.defaultWeight = 1;
			this.hasDefaultWeight = true;
		}
		
		this.hasWeights = true;
		
		
		//load max_enchantment_levels
		//guess what? iterator.
		for (String e:config.getConfigurationSection("limits.max_enchantment_levels").getKeys(false)) {
			if (Enchantment.getByKey(NamespacedKey.fromString(e)) == null) {
				EnchManagerPlugin.LOGGER.warning("config.yml, limits, max_enchantment_levels: Couldn't recognize "+e+" as an existing enchantment, ignoring.");
			} else {
				this.maxLevels.put(NamespacedKey.fromString(e).toString(), config.getInt("limits.max_enchantment_levels."+e));
			}
		}
		
	    //Here you can set the max level for any enchantment, one enchantment per line, this list will be respected even if safe_mode and limit_levels is disabled
	    //setting a max level of 0 will prevent that enchantment from being added or transfered (though not removed)
		//load min_player_levels
		if (config.getConfigurationSection("limits.min_player_levels") != null) {
			//are you tired of iterations yet? no? good.
			for(String t:config.getConfigurationSection("limits.min_player_levels").getKeys(false)) {
				NamespacedKey ench = NamespacedKey.fromString(t);
				//check if enchanment is, as always
				if (Enchantment.getByKey(ench) == null) {
					EnchManagerPlugin.PLUGIN_INSTANCE.getLogger().warning("Couldn't recognize "+t+" as a valid enchantment in limits, min_player_levels!");
				} else {
					if (!this.minLevels.containsKey(ench.toString())) {
						this.minLevels.put(ench.toString(), new HashMap<String,Integer>());
					}
					//no need for defaults, since we're checking if it exists beforehand
					if (config.isInt("limits.min_player_levels."+t)) {
						this.minLevels.get(ench.toString()).put("add", config.getInt("limits.min_player_levels."+t));
						this.minLevels.get(ench.toString()).put("transfer", config.getInt("limits.min_player_levels."+t));
						this.minLevels.get(ench.toString()).put("remove", config.getInt("limits.min_player_levels."+t));
					} else {
						if (config.isInt("limits.min_player_levels."+t+".add")) {
							this.minLevels.get(ench.toString()).put("add", config.getInt("limits.min_player_levels."+t+".add"));
						}
						if (config.isInt("limits.min_player_levels."+t+".transfer")) {
							this.minLevels.get(ench.toString()).put("transfer", config.getInt("limits.min_player_levels."+t+".transfer"));
						}
						if (config.isInt("limits.min_player_levels."+t+".remove")) {
							this.minLevels.get(ench.toString()).put("remove", config.getInt("limits.min_player_levels."+t+".remove"));
						}
					}
				}
			}
		}
		

	}

	public PermissionGroup(String n, Map<?,?> costs) {
		this.name = n;
		//start casting and hope for the best
		//it does what the constructor above does, but dangerously
		//this section is not a good idea, but i don't have any others
		//well, others that aren't implementation-dependant
		//not gonna comment this bullshit, look at the previous one.
		this.useCash = (Boolean)costs.get("use_money_instead_of_xp");
		
		if (costs.get("add") instanceof Map) {
			this.add = new CostBundle(
					(Double)((Map<?,?>) costs.get("add")).get("cost"),
					(Double)((Map<?,?>) costs.get("add")).get("cost_multiplier"),
					(Double)((Map<?,?>) costs.get("add")).get("cost_adder"), 
					(Boolean)((Map<?,?>) costs.get("add")).get("scaling_per_enchantment_level"));
		}
		if (costs.get("transfer") instanceof Map) {
			this.tra = new CostBundle(
					(Double)((Map<?,?>) costs.get("transfer")).get("cost"),
					(Double)((Map<?,?>) costs.get("transfer")).get("cost_multiplier"),
					(Double)((Map<?,?>) costs.get("transfer")).get("cost_adder"), 
					(Boolean)((Map<?,?>) costs.get("transfer")).get("scaling_per_enchantment_level"));
		}
		if (costs.get("remove") instanceof Map) {
			this.rem = new CostBundle(
					(Double)((Map<?,?>) costs.get("remove")).get("cost"),
					(Double)((Map<?,?>) costs.get("remove")).get("cost_multiplier"),
					(Double)((Map<?,?>) costs.get("remove")).get("cost_adder"), 
					(Boolean)((Map<?,?>) costs.get("remove")).get("scaling_per_enchantment_level"));
		}
		if (costs.get("enchantment_add_weights") instanceof Map) {
			for (Entry<?,?> e:((Map<?,?>)costs.get("enchantment_add_weights")).entrySet()) {
				if (e.getKey().equals("default")) {
					this.defaultWeight = (Integer)e.getValue();
					this.hasDefaultWeight = !(this.defaultWeight == null);
				} else if (Enchantment.getByKey(NamespacedKey.fromString((String)e.getKey())) == null) {
					EnchManagerPlugin.LOGGER.warning("costgroups.yml, "+this.name+" group, enchantment_add_weights: Couldn't recognize "+ e.getKey() +" as an existing enchantment or default.");
				} else {
					this.addWeights.put(NamespacedKey.fromString((String)e.getKey()).toString(), (Integer)e.getValue());
				}
			}
		}

		this.hasWeights = this.addWeights.size() > 0;
		
		
		if (!(costs.get("max_enchantment_levels") == null)) {
			for (Entry<?,?> e:((Map<?,?>)costs.get("max_enchantment_levels")).entrySet()) {
				if (Enchantment.getByKey(NamespacedKey.fromString((String)e.getKey())) == null) {
					EnchManagerPlugin.LOGGER.warning("costgroups.yml, max_enchantment_levels: Couldn't recognize "+ e.getKey() +" as an existing enchantment.");
				} else {
					this.maxLevels.put(NamespacedKey.fromString((String)e.getKey()).toString(), (Integer)e.getValue());
				}
			}
		}

		if (costs.get("min_player_levels") != null) {
			for(Entry<?,?> t:((Map<?,?>)costs.get("min_player_levels")).entrySet()) {
				NamespacedKey ench = NamespacedKey.fromString((String)t.getKey());
				if (Enchantment.getByKey(ench) == null) {
					EnchManagerPlugin.PLUGIN_INSTANCE.getLogger().warning("costgroups.yml, min_player_levels: Couldn't recognize "+ t.getKey() +" as a valid enchantment!");
				} else {
					if (!this.minLevels.containsKey(ench.toString())) {
						this.minLevels.put(ench.toString(), new HashMap<String,Integer>());
					}
					
					if (t.getValue() instanceof Integer) {
						this.minLevels.get(ench.toString()).put("add", (Integer)t.getValue());
						this.minLevels.get(ench.toString()).put("transfer",(Integer)t.getValue());
						this.minLevels.get(ench.toString()).put("remove", (Integer)t.getValue());
					} else if (t.getValue() instanceof Map) {
						for (Entry<?,?> u:((Map<?,?>)t.getValue()).entrySet()) {
							if (!(u.getValue() instanceof Integer)) {
								continue;
							}
							if (u.getKey().equals("add")) {
								this.minLevels.get(ench.toString()).put("add", (Integer)u.getValue());
							}
							if (u.getKey().equals("transfer")) {
								this.minLevels.get(ench.toString()).put("transfer", (Integer)u.getValue());
							}
							if (u.getKey().equals("remove")) {
								this.minLevels.get(ench.toString()).put("remove", (Integer)u.getValue());
							}
						}
					}
				}
			}
		}
		
	}

	public PermissionGroup(String n) {this.name = n;}

	public static LinkedHashMap<String,Integer> addWeightsToPool(LinkedHashMap<String,Integer> pool, Map<String,Integer> weights){
		LinkedHashMap<String,Integer> enchs = new LinkedHashMap<String,Integer>(pool);
		for (Entry<String,Integer> e:weights.entrySet()) {
			if (!enchs.containsKey(e.getKey())) {
				enchs.put(e.getKey(), e.getValue());
			}
		}
		return enchs;
	}
	
	public static LinkedHashMap<String,Integer> completeWeights(LinkedHashMap<String, Integer> pool, LinkedHashMap<String,Integer> weights, Integer defaultWeight) {
		//bad and naughty (all) enchantments have to go into the enchantment pool to atone for their sins (by being randomly selected)
		LinkedHashMap<String,Integer> enchPool = addWeightsToPool(pool, weights);
		for (Enchantment e: Enchantment.values()) {
			if (!enchPool.containsKey(e.getKey().toString())) {
				enchPool.put(e.getKey().toString(), defaultWeight);
			}
		}
		return enchPool;
	}

	public static LinkedHashMap<String, Integer> completeWeights(LinkedHashMap<String, Integer> pool, LinkedHashMap<String, Integer> weights, Integer defaultWeight, Player p, ItemStack i) {
		LinkedHashMap<String,Integer> enchPool = addWeightsToPool(pool, weights);
		for (Enchantment e: Enchantment.values()) {
			if (!enchPool.containsKey(e.getKey().toString())) {
				if (EnchHelper.isValidToAdd(p, i, e, "add"))
					enchPool.put(e.getKey().toString(), defaultWeight);
				else
					enchPool.put(e.getKey().toString(), 0);
			}
		}
		return enchPool;
	}
}
