package zan.tecbot.resource;

import java.util.HashMap;

public class MapData {
	
	private String mapName;
	private String mapData, wireData, typeData;
	private HashMap<Character, TypeInfo> typeInfo;
	private int mapWidth, mapHeight;
	
	public MapData(String mn, String md, String wd, String td, HashMap<Character, TypeInfo> ti, int mw, int mh) {
		setMapName(mn);
		setMapData(md);
		setWireData(wd);
		setTypeData(td);
		setTypeInfo(ti);
		setMapWidth(mw);
		setMapHeight(mh);
	}
	
	public boolean isValid() {
		if (mapName.isEmpty() || mapData.isEmpty() || mapWidth == 0 || mapHeight == 0) return false;
		if (mapData.length() != mapWidth*mapHeight) return false;
		if (!wireData.isEmpty() && wireData.length() != mapWidth*mapHeight) return false;
		if (!typeData.isEmpty() && typeData.length() != mapWidth*mapHeight) return false;
		return true;
	}
	
	public void setMapName(String mn) {mapName = mn;}
	public String getMapName() {return mapName;}
	public void setMapData(String md) {mapData = md;}
	public String getMapData() {return mapData;}
	public void setWireData(String wd) {wireData = wd;}
	public String getWireData() {return wireData;}
	public void setTypeData(String td) {typeData = td;}
	public String getTypeData() {return typeData;}
	public void setTypeInfo(HashMap<Character, TypeInfo> ti) {typeInfo = ti;}
	public HashMap<Character, TypeInfo> getTypeInfo() {return typeInfo;}
	public void setMapWidth(int mw) {mapWidth = mw;}
	public int getMapWidth() {return mapWidth;}
	public void setMapHeight(int mh) {mapHeight = mh;}
	public int getMapHeight() {return mapHeight;}
	
}
