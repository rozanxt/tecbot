package zan.tecbot.panel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MapReader {
	private static final String LOGNAME = "MapReader :: ";
	
	private static final String RES_DIR = "res/map/";
	
	private String mapData;
	private int mapWidth, mapHeight;
	
	public MapReader(String fnm) {
		mapData = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(RES_DIR + fnm));
			String line;
			mapWidth = 0;
			mapHeight = 0;
			while((line = br.readLine()) != null) {
				if (line.length() == 0)	continue;
				if (line.contains("##")) continue;
				if (mapWidth == 0) mapWidth = line.length()-2;
				mapHeight++;
				mapData += line.substring(1, line.length()-1);
			}
			br.close();
		} catch (IOException e) {
			System.err.println(LOGNAME + "Error reading file " + RES_DIR + fnm + ":\n " + e);
		}
	}
	
	public String getMapData() {return mapData;}
	public int getMapWidth() {return mapWidth;}
	public int getMapHeight() {return mapHeight;}
	
}
