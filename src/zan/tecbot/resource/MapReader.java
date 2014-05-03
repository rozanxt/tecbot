package zan.tecbot.resource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import zan.game.util.GameUtility;

public class MapReader {
	private static final String LOGNAME = "MapReader :: ";
	
	private static final String RES_DIR = "res/map/";
	
	private HashMap<String, MapData> mapDatas;
	
	public MapReader(String fnm) {
		mapDatas = new HashMap<String, MapData>();
		HashMap<Character, TypeInfo> typeDatas = new HashMap<Character, TypeInfo>();
		String tempMapName = "";
		String tempMapData = "";
		String tempWireData = "";
		String tempTypeData = "";
		int tempMapWidth = 0;
		int tempMapHeight = 0;
		boolean[] tempWeapons = new boolean[10];
		for (int i=0;i<tempWeapons.length;i++) tempWeapons[i] = false;
		tempWeapons[0] = true;
		boolean mapping = false;
		try {
			BufferedReader br = new BufferedReader(new FileReader(RES_DIR + fnm));
			String line;
			while((line = br.readLine()) != null) {
				if (line.length() == 0)	continue;
				if (line.startsWith("//") || line.startsWith("##") || line.startsWith("++") || line.startsWith("$$")) continue;
				
				if (!mapping) {
					if (line.contains("MAPBEGIN")) {
						tempMapName = "";
						tempMapData = "";
						tempWireData = "";
						tempMapWidth = 0;
						tempMapHeight = 0;
						mapping = true;
					}
				} else {
					if (line.contains("MAPEND")) {
						mapDatas.put(tempMapName, new MapData(tempMapName, tempMapData, tempWireData, tempTypeData, typeDatas, tempMapWidth, tempMapHeight, tempWeapons));
						mapping = false;
					} else if (line.startsWith("#")) {
						tempMapData += line.substring(1, line.length()-1);
					} else if (line.startsWith("+")) {
						tempWireData += line.substring(1, line.length()-1);
					} else if (line.startsWith("$")) {
						tempTypeData += line.substring(1, line.length()-1);
					} else if (line.startsWith(">")) {
						Character id = line.charAt(1);
						if (id != null) {
							TypeInfo info = new TypeInfo();
							String[] tkns = GameUtility.split(line);
							for (int i=1;i<tkns.length;i++) info.add(tkns[i]);
							typeDatas.put(id, info);
						}
					} else {
						String[] tkns = GameUtility.split(line);
						for (int i=0;i<tkns.length;i++) {
							if (tkns[i].contains("=")) {
								int pos = tkns[i].indexOf("=")+1;
								if (tkns[i].startsWith("map=")) {
									tempMapName = tkns[i].substring(pos);
								} else if (tkns[i].startsWith("w=")) {
									String w = tkns[i].substring(pos);
									if (GameUtility.isIntegerString(w)) tempMapWidth = Integer.parseInt(w);
								} else if (tkns[i].startsWith("h=")) {
									String h = tkns[i].substring(pos);
									if (GameUtility.isIntegerString(h)) tempMapHeight = Integer.parseInt(h);
								} else if (tkns[i].startsWith("weapons=")) {
									String wpns = tkns[i].substring(pos);
									for (int j=0;j<tempWeapons.length;j++) {
										char wpn = Character.forDigit(j, 10);
										if (wpns.indexOf(wpn) != -1) tempWeapons[j] = true;
									}
								}
							}
						}
					}
				}
			}
			br.close();
		} catch (IOException e) {
			System.err.println(LOGNAME + "Error reading file " + RES_DIR + fnm + ":\n " + e);
		}
	}
	
	public HashMap<String, MapData> getMapDatas() {return mapDatas;}
	
}
