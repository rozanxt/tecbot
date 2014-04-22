package zan.game.resource;

import java.util.ArrayList;
import java.util.HashMap;

import zan.game.util.GameUtility;

/** Resource data class */
public class ResourceData {
	
	private String name;
	
	private ArrayList<String> value;
	private HashMap<String, String> param;
	
	private ArrayList<ResourceData> node;
	
	public ResourceData(String name) {
		this.name = name;
		if (!isEmpty()) {
			value = new ArrayList<String>();
			param = new HashMap<String, String>();
			node = new ArrayList<ResourceData>();
		}
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isEmpty() {
		return (name == null);
	}
	
	public void clear() {
		if (!isEmpty()) {
			param.clear();
			value.clear();
			for (int i=0;i<node.size();i++) node.get(i).clear();
			node.clear();
		}
	}
	
	public void addValue(String value) {
		if (isEmpty()) return;
		if (value.contains("=")) {
			int pos = value.indexOf("=");
			String key = value.substring(0, pos);
			if (!param.containsKey(key)) {
				param.put(key, value.substring(pos+1));
			}
		}
		this.value.add(value);
	}
	
	public String getValue(int index) {
		if (isEmpty()) return null;
		if (index < 0 || index >= value.size()) return null;
		return value.get(index);
	}
	
	public String getValue(String key) {
		if (isEmpty()) return null;
		return param.get(key);
	}
	
	public int getIntegerValue(int index) {
		if (isEmpty()) return 0;
		if (index < 0 || index >= value.size()) return 0;
		if (!GameUtility.isIntegerString(value.get(index))) return 0;
		return Integer.parseInt(value.get(index));
	}
	
	public int getIntegerValue(String key) {
		if (isEmpty()) return 0;
		if (param.get(key) == null) return 0;
		if (!GameUtility.isIntegerString(param.get(key))) return 0;
		return Integer.parseInt(param.get(key));
	}
	
	public boolean getBooleanValue(int index) {
		if (isEmpty()) return false;
		if (index < 0 || index >= value.size()) return false;
		if (value.get(index).contentEquals("true")) return true;
		return false;
	}
	
	public boolean getBooleanValue(String key) {
		if (isEmpty()) return false;
		if (param.get(key) == null) return false;
		if (param.get(key).contentEquals("true")) return true;
		return false;
	}
	
	public boolean hasValue() {
		if (isEmpty()) return false;
		return !value.isEmpty();
	}
	
	public int getNumValues() {
		if (isEmpty()) return 0;
		return value.size();
	}
	
	public void addNode(ResourceData node) {
		if (isEmpty()) return;
		this.node.add(node);
	}
	
	public ResourceData getNode(int index) {
		if (isEmpty()) return new ResourceData(null);
		if (index < 0 || index > node.size()) return new ResourceData(null);
		return node.get(index);
	}
	
	public ResourceData getNode(String key) {
		if (isEmpty()) return new ResourceData(null);
		for (int i=0;i<node.size();i++) {
			if (node.get(i).getName().contentEquals(key)) {
				return node.get(i);
			}
		}
		return new ResourceData(null);
	}
	
	public ResourceData getNodes(String key) {
		if (isEmpty()) return new ResourceData(null);
		ResourceData group = new ResourceData(key);
		for (int i=0;i<node.size();i++) {
			if (node.get(i).getName().contentEquals(key)) {
				group.addNode(node.get(i));
			}
		}
		return group;
	}
	
	public boolean hasNode() {
		if (isEmpty()) return false;
		return !node.isEmpty();
	}
	
	public int getNumNodes() {
		if (isEmpty()) return 0;
		return node.size();
	}
	
}
