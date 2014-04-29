package zan.tecbot.resource;

public class MapData {
	
	private String mapName;
	private String mapData, wireData;
	private int mapWidth, mapHeight;
	
	public MapData(String mn, String md, String wd, int mw, int mh) {
		setMapName(mn);
		setMapData(md);
		setWireData(wd);
		setMapWidth(mw);
		setMapHeight(mh);
	}
	
	public boolean isValid() {
		if (mapName.isEmpty() || mapData.isEmpty() || wireData.isEmpty() || mapWidth == 0 || mapHeight == 0) return false;
		if (mapData.length() != mapWidth*mapHeight || wireData.length() != mapWidth*mapHeight) return false;
		return true;
	}
	
	public void setMapName(String mn) {mapName = mn;}
	public String getMapName() {return mapName;}
	public void setMapData(String md) {mapData = md;}
	public String getMapData() {return mapData;}
	public void setWireData(String wd) {wireData = wd;}
	public String getWireData() {return wireData;}
	public void setMapWidth(int mw) {mapWidth = mw;}
	public int getMapWidth() {return mapWidth;}
	public void setMapHeight(int mh) {mapHeight = mh;}
	public int getMapHeight() {return mapHeight;}
	
}
