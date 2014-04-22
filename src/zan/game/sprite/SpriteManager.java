package zan.game.sprite;

import java.util.HashMap;

import zan.game.resource.ResourceData;

/** Sprite manager class */
public class SpriteManager {
	private static final String LOGNAME = "SpriteManager :: ";
	
	/** Sprite storage */
	private static HashMap<String, ISprite> spriteStore;
	
	/** Initialize sprite manager */
	public static void init() {
		spriteStore = new HashMap<String, ISprite>();
	}
	
	/** Sprite manager cleanup */
	public static void destroy() {
		spriteStore.clear();
	}
	
	/** Load sprites from a resource data */
	public static void loadSpriteData(ResourceData spriteData) {
		if (spriteData.isEmpty()) {
			System.err.println(LOGNAME + "Error loading sprite data:\n no data found");
			return;
		}
		
		for (int i=0;i<spriteData.getNumNodes();i++) {
			ResourceData node = spriteData.getNode(i);
			if (!node.isEmpty()) {
				loadSingleSprite(node.getName(),
								 node.getValue("tex"),
								 node.getIntegerValue("w"),
								 node.getIntegerValue("h"),
								 node.getIntegerValue("x0"),
								 node.getIntegerValue("y0"),
								 node.getIntegerValue("x1"),
								 node.getIntegerValue("y1"));
			}
		}
	}
	
	/** Load single sprite */
	private static boolean loadSingleSprite(String name, String tid, int sw, int sh, int x0, int y0, int x1, int y1) {
		spriteStore.put(name, new Sprite(TextureManager.getTextureID(tid), sw, sh, x0, y0, x1, y1));
		return true;
	}
	
	/** @return Sprite */
	public static ISprite getSprite(String name) {
		if (!isSpriteLoaded(name)) {
			System.out.println(LOGNAME + "No sprite stored under " + name);  
			return null;
		}
		return spriteStore.get(name);
	}
	
	/** @return true if sprite is loaded */
	public static boolean isSpriteLoaded(String name) {
		if (spriteStore.get(name) == null) return false;
		return true;
	}
	
}
