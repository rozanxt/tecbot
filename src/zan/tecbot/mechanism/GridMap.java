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
					break;
				}
			}
		}
		if (!mapDest.isEmpty()) {
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
	
	public Vector2f getTeleDest(int gx, int gy, char id) {
		Vector2f dest = null;
		int pos = mapData.getWireData().indexOf(id);
		if (pos == ((mapData.getMapHeight()-gy-1)*mapData.getMapWidth())+gx) {
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
	
	public char getTypeID(int sx, int sy) {
		if (mapData.getTypeData().isEmpty() || mapData.getTypeData().length() != mapData.getMapWidth()*mapData.getMapHeight()) return ' ';
		return mapData.getTypeData().charAt(sx+(mapData.getMapHeight()-sy-1)*mapData.getMapWidth());
	}
	public boolean hasAttribute(char id, String sa) {
		if (mapData.getTypeInfo().get(id) != null && mapData.getTypeInfo().get(id).get(sa) != null) return true;
		return false;
	}
	public int getAttribute(char id, String sa) {
		if (!hasAttribute(id, sa)) return 0;
		return mapData.getTypeInfo().get(id).get(sa);
	}
	
	public char getWireID(int sx, int sy) {
		if (mapData.getWireData().isEmpty() || mapData.getWireData().length() != mapData.getMapWidth()*mapData.getMapHeight()) return ' ';
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
				float gx = (tileWidth*0.5f)+i*tileWidth;
				float gy = (tileHeight*(mapHeight-0.5f))-j*tileHeight;
				int tx = i;
				int ty = mapHeight-j-1;
				
				String blockCodes = "0123456789OBMDLPEYTWQ";
				String entityCodes = "ert";
				String collectCodes = "ah";
				
				if (blockCodes.indexOf(code) != -1) {
					if (code == '0') {
						SolidBlock b = new SolidBlock(tx, ty, 0);
						blocks.add(b);
					} else if (code == '1') {
						SolidBlock b = new SolidBlock(tx, ty, 1);
						blocks.add(b);
					} else if (code == '2') {
						SolidBlock b = new SolidBlock(tx, ty, 2);
						blocks.add(b);
					} else if (code == '3') {
						SolidBlock b = new SolidBlock(tx, ty, 3);
						blocks.add(b);
					} else if (code == '4') {
						SolidBlock b = new SolidBlock(tx, ty, 4);
						blocks.add(b);
					} else if (code == 'O') {
						DestroyAbleBlock b = new DestroyAbleBlock(tx, ty);
						blocks.add(b);
					} else if (code == 'B') {
						BumperBlock b = new BumperBlock(tx, ty);
						blocks.add(b);
					} else if (code == 'M') {
						MovingPlatform b = new MovingPlatform(tx, ty);
						char tid = getTypeID(tx, ty);
						if (tid != ' ') if (hasAttribute(tid, "theta")) b.setTheta(getAttribute(tid, "theta"));
						blocks.add(b);
					} else if (code == 'D') {
						DoorBlock b = new DoorBlock(tx, ty);
						blocks.add(b);
					} else if (code == 'L') {
						SwitchLever b = new SwitchLever(tx, ty, this);
						blocks.add(b);
					} else if (code == 'P') {
						SwitchPlate b = new SwitchPlate(tx, ty, this);
						blocks.add(b);
					} else if (code == 'E') {
						EnergySwitch b = new EnergySwitch(tx, ty, this);
						blocks.add(b);
					} else if (code == 'Y') {
						SpikeBlock b = new SpikeBlock(tx, ty);
						blocks.add(b);
					} else if (code == 'T') {
						TelePort b = new TelePort(tx, ty, this);
						blocks.add(b);
					} else if (code == 'W') {
						MapPort b = new MapPort(tx, ty, this);
						blocks.add(b);
					} else if (code == 'Q') {
						ExitArea b = new ExitArea(tx, ty, this);
						blocks.add(b);
					} else continue;
					
					Block b = blocks.get(blocks.size()-1);
					b.setPos(gx, gy);
					b.setSize(tileSize);
					b.spawn();
					
					char tid = getTypeID(tx, ty);
					if (tid != ' ') {
						if (hasAttribute(tid, "type")) b.setType(getAttribute(tid, "type"));
						if (hasAttribute(tid, "inv")) {
							int inv = getAttribute(tid, "inv");
							if (inv == 0) b.setInverse(false);
							else b.setInverse(true);
						}
						if (b instanceof SwitchBlock) {
							SwitchBlock s = (SwitchBlock)b;
							if (hasAttribute(tid, "switch")) {
								int swc = getAttribute(tid, "switch");
								if (swc == 0) s.setSwitchAble(false);
								else s.setSwitchAble(true);
							}
							if (hasAttribute(tid, "timer")) {
								s.setSwitchTimer(getAttribute(tid, "timer"));
							}
						}
					}
					tiles[tx][ty] = b;
					giveWireID(b, tx, ty);
					
				} else if (entityCodes.indexOf(code) != -1) {
					if (code == 'e') {
						GummBot e = new GummBot(this);
						entities.add(e);
					} else if (code == 'r') {
						HuntBot e = new HuntBot(bullets, this);
						e.setTarget(gamePlayer.getTecbot());
						entities.add(e);
					} else if (code == 't') {
						ShotBot e = new ShotBot(bullets, this);
						e.setTarget(gamePlayer.getTecbot());
						entities.add(e);
					} else continue;
					
					BaseEntity e = entities.get(entities.size()-1);
					e.setPos(gx, gy);
					e.spawn();
					
				} else if (collectCodes.indexOf(code) != -1) {
					if (code == 'a') {
						AmmoCollectible c = new AmmoCollectible(gamePlayer);
						collectibles.add(c);
					} else if (code == 'h') {
						HealthCollectible c = new HealthCollectible(gamePlayer);
						collectibles.add(c);
					}
					
					Collectible c = collectibles.get(collectibles.size()-1);
					c.setPos(gx, gy);
					c.spawn();
				}
				
				/*
				if (code == 'x') {
					GummBot e = new GummBot(this);
					e.setPos(gx, gy);
					e.spawn();
					entities.add(e);
				} else if (code == 'c') {
					HuntBot e = new HuntBot(bullets, this);
					e.setPos(gx, gy);
					e.setTarget(gamePlayer.getTecbot());
					e.spawn();
					entities.add(e);
				} else if (code == 'v') {
					ShotBot e = new ShotBot(bullets, this);
					e.setPos(gx, gy);
					e.setTarget(gamePlayer.getTecbot());
					e.spawn();
					entities.add(e);
				} else if (code == '0') {
					SolidBlock b = new SolidBlock(i, mapHeight-j-1, 0);
					b.setPos(gx, gy);
					b.setSize(tileSize);
					b.spawn();
					blocks.add(b);
					tiles[i][mapHeight-j-1] = b;
				} else if (code == '1') {
					SolidBlock b = new SolidBlock(i, mapHeight-j-1, 1);
					b.setPos(gx, gy);
					b.setSize(tileSize);
					b.spawn();
					blocks.add(b);
					tiles[i][mapHeight-j-1] = b;
				} else if (code == '2') {
					Block b = new SolidBlock(i, mapHeight-j-1, 2);
					b.setPos(gx, gy);
					b.setSize(tileSize);
					b.spawn();
					blocks.add(b);
					tiles[i][mapHeight-j-1] = b;
				} else if (code == '3') {
					Block b = new SolidBlock(i, mapHeight-j-1, 3);
					b.setPos(gx, gy);
					b.setSize(tileSize);
					b.spawn();
					blocks.add(b);
					tiles[i][mapHeight-j-1] = b;
				} else if (code == '4') {
					Block b = new SolidBlock(i, mapHeight-j-1, 4);
					b.setPos(gx, gy);
					b.setSize(tileSize);
					b.spawn();
					blocks.add(b);
					tiles[i][mapHeight-j-1] = b;
				} else if (code == '5') {
					BumperBlock b = new BumperBlock(i, mapHeight-j-1);
					b.setPos(gx, gy);
					b.setSize(tileSize);
					b.spawn();
					blocks.add(b);
					giveWireID(b, i, mapHeight-j-1);
					tiles[i][mapHeight-j-1] = b;
				} else if (code == 'O') {
					DestroyAbleBlock b = new DestroyAbleBlock(i, mapHeight-j-1);
					b.setPos(gx, gy);
					b.setSize(tileSize);
					b.spawn();
					blocks.add(b);
					tiles[i][mapHeight-j-1] = b;
				} else if (code == '*') {
					SpikeBlock b = new SpikeBlock(i, mapHeight-j-1);
					b.setPos(gx, gy);
					b.setSize(tileSize);
					b.spawn();
					blocks.add(b);
					giveWireID(b, i, mapHeight-j-1);
					tiles[i][mapHeight-j-1] = b;
				} else if (code == 'm') {
					MovingPlatform b = new MovingPlatform(i, mapHeight-j-1);
					b.setPos(gx, gy);
					b.setSize(tileSize*3f);
					b.setType(0);
					if (getTypeID(i, mapHeight-j-1) != ' ') {
						if (mapData.getTypeInfo().get(getTypeID(i, mapHeight-j-1)) != null) {
							if (mapData.getTypeInfo().get(getTypeID(i, mapHeight-j-1)).get("type") != null) {
								b.setType(mapData.getTypeInfo().get(getTypeID(i, mapHeight-j-1)).get("type"));
							}
						}
					}
					b.spawn();
					blocks.add(b);
					giveWireID(b, i, mapHeight-j-1);
					tiles[i][mapHeight-j-1] = b;
				} else if (code == 'd') {
					DoorBlock b = new DoorBlock(i, mapHeight-j-1);
					b.setPos(gx, gy);
					b.setSize(tileSize);
					b.spawn();
					blocks.add(b);
					giveWireID(b, i, mapHeight-j-1);
					tiles[i][mapHeight-j-1] = b;
				} else if (code == 'e') {
					EnergySwitch b = new EnergySwitch(i, mapHeight-j-1, this);
					b.setPos(gx, gy);
					b.setSize(tileSize);
					b.spawn();
					blocks.add(b);
					giveWireID(b, i, mapHeight-j-1);
					tiles[i][mapHeight-j-1] = b;
				} else if (code == 'p') {
					SwitchPlate b = new SwitchPlate(i, mapHeight-j-1, this);
					b.setPos(gx, gy);
					b.setSize(tileSize);
					b.spawn();
					blocks.add(b);
					giveWireID(b, i, mapHeight-j-1);
					tiles[i][mapHeight-j-1] = b;
				} else if (code == 'a') {
					AmmoCollectible c = new AmmoCollectible(gamePlayer);
					c.setPos(gx, gy);
					c.spawn();
					collectibles.add(c);
				} else if (code == 'h') {
					HealthCollectible c = new HealthCollectible(gamePlayer);
					c.setPos(gx, gy);
					c.spawn();
					collectibles.add(c);
				} else if (code == 't') {
					TelePort x = new TelePort(i, mapHeight-j-1, this);
					x.setPos(gx, gy);
					x.setSize(tileSize);
					x.spawn();
					blocks.add(x);
					giveWireID(x, i, mapHeight-j-1);
					tiles[i][mapHeight-j-1] = x;
				} else if (code == 'w') {
					MapPort x = new MapPort(i, mapHeight-j-1, this);
					x.setPos(gx, gy);
					x.setSize(tileSize);
					x.spawn();
					blocks.add(x);
					giveWireID(x, i, mapHeight-j-1);
					tiles[i][mapHeight-j-1] = x;
				} else if (code == 'q') {
					ExitArea x = new ExitArea(i, mapHeight-j-1, this);
					x.setPos(gx, gy);
					x.setSize(tileSize);
					x.spawn();
					blocks.add(x);
					tiles[i][mapHeight-j-1] = x;
				}*/
			}
		}
		checkWires();
	}
	
}
