package io.github.SkywolfDragon.fileHelpers;

import java.util.HashMap;
import java.util.Map;

public class CostBundle {
	public Boolean perLevel;
	
	public Double cost;
	public Double mult;
	public Double add;

	public CostBundle() {
		this(null, null, null);
	}

	public CostBundle(Double c, Double m, Double a) {
		this(c, m, a, null);
	}
	
	public CostBundle(Double c, Double m, Double a, Boolean p) {
		this.cost = c;
		this.mult = m;
		this.add = a;
		this.perLevel = p;
	}

	public boolean containsDouble(String key) {
		return key.equals("cost") || key.equals("cost_multiplier") || key.equals("cost_adder");
	}

	public boolean containsBool(String key) {
		return key.equals("scaling_per_enchantment_level");
	}

	public void put(String key, Double value) {
		switch (key) {
		case "cost":
			this.cost = value;
			break;
		case "cost_multiplier":
			this.mult = value;
			break;
		case "cost_adder":
			this.add = value;
			break;
		}
	}

	public void put(String key, Boolean value) {
		if ("scaling_per_enchantment_level".equals(key)) {
			this.perLevel = value;
		}
	}
	
	public Map<?,?> toMap(){
		HashMap<String,Object> tmp = new HashMap<String, Object>();
			tmp.put("cost", this.cost);
			tmp.put("cost_multiplier", this.mult);
			tmp.put("cost_adder", this.add);
			tmp.put("scaling_per_enchantment_level", this.perLevel);
		return tmp;
	}
	
}
