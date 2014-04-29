package zan.tecbot.mechanism;

import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.util.vector.Vector2f;

import zan.tecbot.object.entity.*;
import zan.tecbot.object.block.*;
import zan.tecbot.object.bullet.*;
import zan.tecbot.object.collectible.*;
import zan.tecbot.resource.MapData;

public class GridMap {
	
	public static final float tileWidth = 40f, tileHeight = 40f;
	public static final float tileSize = Math.max(tileWidth, tileHeight);
	
	public static int getTileX(float sx) {return (int)Math.floor(sx/tileWidth);}
	public static int getTileY(float sy) {return (int)Math.floor(sy/tileHeight);}
	public static float getGameX(int sx) {return (sx+0.5f)*tileWidth;}
	public static float getGameY(int sy) {return (sy+0.5f)*tileHeight;}
	
	private HashMap<String, MapData> mapDatas;
	private MapData mapData;
	private String nextMap;
	
	private Block[][] tiles;
	private ArrayList<Block> wires;
	
	private boolean inExit;
	private boolean requestMapChange;
	
	private Vector2f playerSpawn;
	
	public GridMap(HashMap<String, MapData> md) {
		mapDatas = md;
		mapData = null;
		nextMap = "";
		tiles = null;
		wires = new ArrayList<Block>();
		inExit = false;
		requestMapChange = false;
		playerSpawn = new Vector2f(0f, 0f);
	}
	
	public void cleanup() {
		if (tiles != null && mapData != null) {
			for (int j=0;j<mapData.getMapHeight();j++) for (int i=0;i<mapData.getMapWidth();i++) tiles[i][j] = null;
			tiles = null;
		}
		wires.clear();
	}
	
	public void initPlayerSpawn() {
		for (MapData map : mapDatas.values()) {
			int pos = map.getMapData().indexOf("s");
			if (pos != -1) {
				nextMap = map.getMapName();
				playerSpawn.set(((pos % map.getMapWidth())+0.5f)*tileWidth, ((map.getMapHeight()-(pos / map.getMapWidth())-1)+0.5f)*tileHeight);
				break;
			}
		}
	}
	public void initMapChange(char id) {
		String mapDest = "";
		float spawnX = playerSpawn.x;
		float spawnY = playerSpawn.y;
		for (MapData map : mapDatas.values()) {
			if (!map.getMapName().equals(mapData.getMapName())) {
				int pos = map.getWireData().indexOf(id);
				if (pos != -1) {
					mapDest = map.getMapName();
					spawnX = ((pos % map.getMapWidth())+0.5f)*tileWidth;
					spawnY = ((map.getMapHeight()-(pos / map.getMapWidth())-1)+0.5f)*tileHeight;
				}
			}
		}
		if (!mapDest.isEmpty() && mapDatas.get(mapDest) != null) {
			nextMap = mapDest;
			playerSpawn.set(spawnX, spawnY);
			requestMapChange = true;
		}
	}
	public boolean requestMapChange() {return requestMapChange;}
	public void changeMap() {
		if (mapDatas.get(nextMap) != null) {
			cleanup();
			mapData = mapDatas.get(nextMap);
			tiles = new Block[mapData.getMapWidth()][mapData.getMapHeight()];
		}
		requestMapChange = false;
	}
	
	public Vector2f getTeleDest(int tx, int ty, char id) {
		Vector2f dest = null;
		int pos = mapData.getWireData().indexOf(id);
		if (pos == ((mapData.getMapHeight()-ty-1)*mapData.getMapWidth())+tx) {
			pos = mapData.getWireData().indexOf(id, pos+1);
		}
		if (pos != -1) {
			dest = new Vector2f();
			dest.x = ((pos % mapData.getMapWidth())+0.5f)*tileWidth;
			dest.y = ((mapData.getMapHeight()-(pos / mapData.getMapWidth())-1)+0.5f)*tileHeight;
		}
		return dest;
	}
	
	public void reachExit() {inExit = true;}
	public boolean inExit() {
		if (inExit) {
			inExit = false;
			return true;
		}
		return false;
	}
	
	public Block getBlock(int sx, int sy) {
		if (sx < 0 || sy < 0 || sx >= mapData.getMapWidth() || sy >= mapData.getMapHeight()) return null;
		return tiles[sx][sy];
	}
	public boolean isSolidBlock(int sx, int sy) {
		if (sx < 0 || sy < 0 || sx >= mapData.getMapWidth() || sy >= mapData.getMapHeight()) return false;
		Block block = tiles[sx][sy];
		if (block != null && block.isActive() && block.isSolid()) return true;
		return false;
	}
	public boolean isSolidBlockType(int sx, int sy, int st) {
		if (sx < 0 || sy < 0 || sx >= mapData.getMapWidth() || sy >= mapData.getMapHeight()) return false;
		Block block = tiles[sx][sy];
		if (block != null && block.isActive() && block.isSolid()) {
			if (block instanceof SolidBlock) {
				SolidBlock b = (SolidBlock)block;
				if (b.getType() == st) return true;
			} else if (st == 0) return true;
		}
		return false;
	}
	public void removeBlock(int sx, int sy) {tiles[sx][sy] = null;}
	
	public char getWireID(int sx, int sy) {
		if (mapData.getWireData().length() != mapData.getMapWidth()*mapData.getMapHeight()) return ' ';
		return mapData.getWireData().charAt(sx+(mapData.getMapHeight()-sy-1)*mapData.getMapWidth());
	}
	public void giveWireID(Block block, int sx, int sy) {
		char wireID = getWireID(sx, sy);
		if (wireID != ' ') {
			block.setWireID(wireID);
			wires.add(block);
		}
	}
	
	public void checkWire(char id) {
		ArrayList<Block> connected = new ArrayList<Block>();
		boolean allSwitched = true;
		for (int i=0;i<wires.size();i++) {
			Block block = wires.get(i);
			if (block != null && block.getWireID() == id) {
				if (block instanceof SwitchBlock) {
					if (!block.isPowered()) allSwitched = false;
				} else connected.add(block);
			}
		}
		for (int i=0;i<connected.size();i++) {
			if (allSwitched) connected.get(i).setPowered(true);
			else connected.get(i).setPowered(false);
		}
	}
	public void checkWires() {
		ArrayList<Character> wireIDs = new ArrayList<Character>();
		for (int i=0;i<wires.size();i++) {
			Block block = wires.get(i);
			char wireID = getWireID(block.getTileX(), block.getTileY());
			boolean newID = true;
			for (int j=0;j<wireIDs.size();j++) if (wireIDs.get(j) == wireID) newID = false;
			if (newID) wireIDs.add(wireID);
		}
		for (int i=0;i<wireIDs.size();i++) checkWire(wireIDs.get(i));
	}
	
	public void createMap(Player gamePlayer, ArrayList<Block> blocks, ArrayList<BaseEntity> entities, ArrayList<Bullet> bullets, ArrayList<Collectible> collectibles) {
		if (mapData == null || !mapData.isValid()) return;
		int mapWidth = mapData.getMapWidth();
		int mapHeight = mapData.getMapHeight();
		gamePlayer.setPlayerSpawn(playerSpawn.x, playerSpawn.y);
		for (int j=0;j<mapHeight;j++) {
			for (int i=0;i<mapWidth;i++) {
				char code = mapData.getMapData().charAt(i+j*mapWidth);
				float tx = (tileWidth*0.5f)+i*tileWidth;
				float ty = (tileHeight*(mapHeight-0.5f))-j*tileHeight;
				if (code == 'x') {
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
					giveWireID(b, i, mapHeight-j-1);
					tiles[i][mapHeight-j-1] = b;
				} else if (code == 'O') {
					DestroyAbleBlock b = new DestroyAbleBlock(i, mapHeight-j-1);
					b.setPos(tx, ty);
					b.setSize(tileSize);
					b.spawn();
					blocks.add(b);
					tiles[i][mapHeight-j-1] = b;
				} else if (code == '*') {
					SpikeBlock b = new SpikeBlock(i, mapHeight-j-1);
					b.setPos(tx, ty);
					b.setSize(tileSize);
					b.spawn();
					blocks.add(b);
					giveWireID(b, i, mapHeight-j-1);
					tiles[i][mapHeight-j-1] = b;
				} else if (code == 'm') {
					MovingPlatform b = new MovingPlatform(i, mapHeight-j-1);
					b.setPos(tx, ty);
					b.setSize(tileSize*3f);
					b.setType(0);
					b.spawn();
					blocks.add(b);
					giveWireID(b, i, mapHeight-j-1);
					tiles[i][mapHeight-j-1] = b;
				} else if (code == 'k') {
					MovingPlatform b = new MovingPlatform(i, mapHeight-j-1);
					b.setPos(tx, ty);
					b.setSize(tileSize*3f);
					b.setType(1);
					b.spawn();
					blocks.add(b);
					giveWireID(b, i, mapHeight-j-1);
					tiles[i][mapHeight-j-1] = b;
				} else if (code == 'd') {
					DoorBlock b = new DoorBlock(i, mapHeight-j-1);
					b.setPos(tx, ty);
					b.setSize(tileSize);
					b.spawn();
					blocks.add(b);
					giveWireID(b, i, mapHeight-j-1);
					tiles[i][mapHeight-j-1] = b;
				} else if (code == 'e') {
					EnergySwitch b = new EnergySwitch(i, mapHeight-j-1, this);
					b.setPos(tx, ty);
					b.setSize(tileSize);
					b.spawn();
					blocks.add(b);
					giveWireID(b, i, mapHeight-j-1);
					tiles[i][mapHeight-j-1] = b;
				} else if (code == 'p') {
					SwitchPlate b = new SwitchPlate(i, mapHeight-j-1, this);
					b.setPos(tx, ty);
					b.setSize(tileSize);
					b.spawn();
					blocks.add(b);
					giveWireID(b, i, mapHeight-j-1);
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
				} else if (code == 't') {
					TelePort x = new TelePort(i, mapHeight-j-1, this);
					x.setPos(tx, ty);
					x.setSize(tileSize);
					x.spawn();
					blocks.add(x);
					giveWireID(x, i, mapHeight-j-1);
					tiles[i][mapHeight-j-1] = x;
				} else if (code == 'w') {
					MapPort x = new MapPort(i, mapHeight-j-1, this);
					x.setPos(tx, ty);
					x.setSize(tileSize);
					x.spawn();
					blocks.add(x);
					giveWireID(x, i, mapHeight-j-1);
					tiles[i][mapHeight-j-1] = x;
				} else if (code == 'q') {
					ExitArea x = new ExitArea(i, mapHeight-j-1, this);
					x.setPos(tx, ty);
					x.setSize(tileSize);
					x.spawn();
					blocks.add(x);
					tiles[i][mapHeight-j-1] = x;
				}
			}
		}
		checkWires();
	}
	
}
