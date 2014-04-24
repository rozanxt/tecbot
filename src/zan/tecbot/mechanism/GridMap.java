package zan.tecbot.mechanism;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;

import zan.tecbot.object.entity.*;
import zan.tecbot.object.block.*;

public class GridMap {
	
	public static final float tileWidth = 40f, tileHeight = 40f;
	public static final float tileSize = Math.max(tileWidth, tileHeight);
	
	private static String mapData;
	private static int mapWidth, mapHeight;
	
	private static Vector2f playerSpawn;
	
	public GridMap(String md, int mw, int mh) {
		mapData = md;
		mapWidth = mw;
		mapHeight = mh;
		playerSpawn = new Vector2f(0f, 0f);
	}
	
	public Vector2f getPlayerSpawn() {return playerSpawn;}
	
	public static int getTileX(float sx) {return (int)Math.floor(sx/tileWidth);}
	public static int getTileY(float sy) {return (int)Math.floor(sy/tileHeight);}
	
	public static boolean isSolidBlock(int sx, int sy) {
		char code = mapData.charAt(sx+(mapHeight-sy-1)*mapWidth);
		if (code == '0' || code == '1' || code == '2' || code == '3' || code == '4' || code == '5' || code == 'O') return true;
		return false;
	}
	
	public void createMap(ArrayList<Block> blocks, ArrayList<BaseEntity> entities) {
		if (mapData.length() != mapWidth*mapHeight) return;
		for (int j=0;j<mapHeight;j++) {
			for (int i=0;i<mapWidth;i++) {
				char code = mapData.charAt(i+j*mapWidth);
				float tx = (tileWidth*0.5f)+i*tileWidth;
				float ty = (tileHeight*(mapHeight-0.5f))-j*tileHeight;
				if (code == 's') {
					playerSpawn.set(tx, ty);
				} else if (code == 'b') {
					GummBot badbot = new GummBot();
					badbot.setPos(tx, ty);
					badbot.spawn();
					entities.add(badbot);
				} else if (code == '0') {
					SolidBlock block = new SolidBlock(i, mapHeight-j-1, 0);
					block.setPos(tx, ty);
					block.setSize(tileSize);
					block.spawn();
					blocks.add(block);
				} else if (code == '1') {
					SolidBlock block = new SolidBlock(i, mapHeight-j-1, 1);
					block.setPos(tx, ty);
					block.setSize(tileSize);
					block.spawn();
					blocks.add(block);
				} else if (code == '2') {
					Block block = new SolidBlock(i, mapHeight-j-1, 2);
					block.setPos(tx, ty);
					block.setSize(tileSize);
					block.spawn();
					blocks.add(block);
				} else if (code == '3') {
					Block block = new SolidBlock(i, mapHeight-j-1, 3);
					block.setPos(tx, ty);
					block.setSize(tileSize);
					block.spawn();
					blocks.add(block);
				} else if (code == '4') {
					Block block = new SolidBlock(i, mapHeight-j-1, 4);
					block.setPos(tx, ty);
					block.setSize(tileSize);
					block.spawn();
					blocks.add(block);
				} else if (code == '5') {
					BumperBlock block = new BumperBlock(i, mapHeight-j-1);
					block.setPos(tx, ty);
					block.setSize(tileSize);
					block.spawn();
					blocks.add(block);
				} else if (code == 'O') {
					DestroyAbleBlock block = new DestroyAbleBlock(i, mapHeight-j-1);
					block.setPos(tx, ty);
					block.setSize(tileSize);
					block.spawn();
					blocks.add(block);
				}
			}
		}
	}
	
}
