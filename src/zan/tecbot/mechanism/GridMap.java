package zan.tecbot.mechanism;

import java.util.ArrayList;

import zan.tecbot.object.entity.*;
import zan.tecbot.object.block.*;
import zan.tecbot.object.bullet.*;
import zan.tecbot.object.collectible.*;

public class GridMap {
	
	public static final float tileWidth = 40f, tileHeight = 40f;
	public static final float tileSize = Math.max(tileWidth, tileHeight);
	
	public static int getTileX(float sx) {return (int)Math.floor(sx/tileWidth);}
	public static int getTileY(float sy) {return (int)Math.floor(sy/tileHeight);}
	
	private String mapData;
	private int mapWidth, mapHeight;
	
	private Block[][] tiles;
	
	public GridMap(String md, int mw, int mh) {
		mapData = md;
		mapWidth = mw;
		mapHeight = mh;
		tiles = new Block[mapWidth][mapHeight];
	}
	
	public Block getBlock(int sx, int sy) {
		if (sx < 0 || sy < 0 || sx >= mapWidth || sy >= mapHeight) return null;
		return tiles[sx][sy];
	}
	public boolean isSolidBlock(int sx, int sy) {
		if (sx < 0 || sy < 0 || sx >= mapWidth || sy >= mapHeight) return false;
		Block block = tiles[sx][sy];
		if (block != null) {
			if (block.isActive() && block.isSolid()) return true;
		}
		return false;
	}
	public boolean isSolidBlockType(int sx, int sy, int st) {
		if (sx < 0 || sy < 0 || sx >= mapWidth || sy >= mapHeight) return false;
		Block block = tiles[sx][sy];
		if (block != null) {
			if (block.isActive()) {
				if (block instanceof SolidBlock) {
					SolidBlock b = (SolidBlock)block;
					if (b.getType() == st) return true;
				} else if (st == 0) return true;
			}
		}
		return false;
	}
	
	public void removeBlock(int sx, int sy) {tiles[sx][sy] = null;}
	
	public void createMap(Player gamePlayer, ArrayList<Block> blocks, ArrayList<BaseEntity> entities, ArrayList<Bullet> bullets, ArrayList<Collectible> collectibles) {
		if (mapData.length() != mapWidth*mapHeight) return;
		for (int j=0;j<mapHeight;j++) {
			for (int i=0;i<mapWidth;i++) {
				char code = mapData.charAt(i+j*mapWidth);
				float tx = (tileWidth*0.5f)+i*tileWidth;
				float ty = (tileHeight*(mapHeight-0.5f))-j*tileHeight;
				if (code == 's') {
					gamePlayer.setPlayerSpawn(tx, ty);
				} else if (code == 'x') {
					GummBot e = new GummBot(this);
					e.setPos(tx, ty);
					e.spawn();
					entities.add(e);
				} else if (code == 'c') {
					HuntBot e = new HuntBot(bullets, this);
					e.setPos(tx, ty);
					e.setTarget(gamePlayer.getTecbot());
					e.spawn();
					entities.add(e);
				} else if (code == 'v') {
					ShotBot e = new ShotBot(bullets, this);
					e.setPos(tx, ty);
					e.setTarget(gamePlayer.getTecbot());
					e.spawn();
					entities.add(e);
				} else if (code == '0') {
					SolidBlock b = new SolidBlock(i, mapHeight-j-1, 0);
					b.setPos(tx, ty);
					b.setSize(tileSize);
					b.spawn();
					blocks.add(b);
					tiles[i][mapHeight-j-1] = b;
				} else if (code == '1') {
					SolidBlock b = new SolidBlock(i, mapHeight-j-1, 1);
					b.setPos(tx, ty);
					b.setSize(tileSize);
					b.spawn();
					blocks.add(b);
					tiles[i][mapHeight-j-1] = b;
				} else if (code == '2') {
					Block b = new SolidBlock(i, mapHeight-j-1, 2);
					b.setPos(tx, ty);
					b.setSize(tileSize);
					b.spawn();
					blocks.add(b);
					tiles[i][mapHeight-j-1] = b;
				} else if (code == '3') {
					Block b = new SolidBlock(i, mapHeight-j-1, 3);
					b.setPos(tx, ty);
					b.setSize(tileSize);
					b.spawn();
					blocks.add(b);
					tiles[i][mapHeight-j-1] = b;
				} else if (code == '4') {
					Block b = new SolidBlock(i, mapHeight-j-1, 4);
					b.setPos(tx, ty);
					b.setSize(tileSize);
					b.spawn();
					blocks.add(b);
					tiles[i][mapHeight-j-1] = b;
				} else if (code == '5') {
					BumperBlock b = new BumperBlock(i, mapHeight-j-1);
					b.setPos(tx, ty);
					b.setSize(tileSize);
					b.spawn();
					blocks.add(b);
					tiles[i][mapHeight-j-1] = b;
				} else if (code == 'O') {
					DestroyAbleBlock b = new DestroyAbleBlock(i, mapHeight-j-1);
					b.setPos(tx, ty);
					b.setSize(tileSize);
					b.spawn();
					blocks.add(b);
					tiles[i][mapHeight-j-1] = b;
				} else if (code == 'a') {
					AmmoCollectible c = new AmmoCollectible(gamePlayer);
					c.setPos(tx, ty);
					c.spawn();
					collectibles.add(c);
				} else if (code == 'h') {
					HealthCollectible c = new HealthCollectible(gamePlayer);
					c.setPos(tx, ty);
					c.spawn();
					collectibles.add(c);
				}
			}
		}
	}
	
}
