package zan.tecbot.panel;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;

import zan.tecbot.object.entity.*;
import zan.tecbot.object.block.*;

public class GridMap {
	
	public static final float tileWidth = 40f, tileHeight = 40f;
	public static final float tileSize = Math.max(tileWidth, tileHeight);
	
	private String mapData;
	private int mapWidth, mapHeight;
	
	private Vector2f playerSpawn;
	
	public GridMap(String md, int mw, int mh) {
		mapData = md;
		mapWidth = mw;
		mapHeight = mh;
		playerSpawn = new Vector2f(0f, 0f);
	}
	
	public Vector2f getPlayerSpawn() {return playerSpawn;}
	
	public void createMap(ArrayList<Block> blocks, ArrayList<BaseEntity> entities) {
		if (mapData.length() != mapWidth*mapHeight) return;
		for (int j=0;j<mapHeight;j++) {
			for (int i=0;i<mapWidth;i++) {
				char code = mapData.charAt(i+j*mapWidth);
				float tileX = (tileWidth*0.5f)+i*tileWidth;
				float tileY = (tileHeight*(mapHeight-0.5f))-j*tileHeight;
				if (code == 's') {
					playerSpawn.set(tileX, tileY);
				} else if (code == 'b') {
					DummBot badbot = new DummBot();
					badbot.setPos(tileX, tileY);
					badbot.spawn();
					entities.add(badbot);
				} else if (code == '0') {
					SolidBlock block = new SolidBlock(0);
					block.setPos(tileX, tileY);
					block.setSize(tileSize);
					block.spawn();
					blocks.add(block);
				} else if (code == '1') {
					SolidBlock block = new SolidBlock(1);
					block.setPos(tileX, tileY);
					block.setSize(tileSize);
					block.spawn();
					blocks.add(block);
				} else if (code == '2') {
					Block block = new SolidBlock(2);
					block.setPos(tileX, tileY);
					block.setSize(tileSize);
					block.spawn();
					blocks.add(block);
				} else if (code == '3') {
					Block block = new SolidBlock(3);
					block.setPos(tileX, tileY);
					block.setSize(tileSize);
					block.spawn();
					blocks.add(block);
				} else if (code == '4') {
					Block block = new SolidBlock(4);
					block.setPos(tileX, tileY);
					block.setSize(tileSize);
					block.spawn();
					blocks.add(block);
				} else if (code == '5') {
					BumperBlock block = new BumperBlock();
					block.setPos(tileX, tileY);
					block.setSize(tileSize);
					block.spawn();
					blocks.add(block);
				}
			}
		}
	}
	
}
