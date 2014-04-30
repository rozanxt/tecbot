package zan.tecbot.resource;

import java.util.HashMap;

import zan.game.util.GameUtility;

public class TypeInfo {
	
	private HashMap<String, Integer> typeInt;
	
	public TypeInfo() {
		typeInt = new HashMap<String, Integer>();
	}
	
	public void add(String sa) {
		if (sa.contains("=")) {
			int pos = sa.indexOf("=");
			String key = sa.substring(0, pos);
			String info = sa.substring(pos+1);
			if (GameUtility.isIntegerString(info)) {
				int value = Integer.parseInt(info);
				typeInt.put(key, value);
			}
		}
	}
	
	public Integer get(String sk) {return typeInt.get(sk);}
	
}
