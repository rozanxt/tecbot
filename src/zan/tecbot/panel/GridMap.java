package zan.tecbot.panel;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;

import zan.tecbot.object.entity.*;
import zan.tecbot.object.block.*;

public class GridMap {
						//0000000000111111111122222222223333333333
						//0123456789012345678901234567890123456789
	/*private String map = "0                      02               " + //A
						 "0                      402              " + //B
						 "0               b       402             " + //C
						 "00002       10002        402            " + //D
						 "  4002      43 43     02  40000002      " + //E
						 "   4002               402  4000000      " + //F
						 "    400     b          402      402     " + //G
						 "00        402           402  b   00   00" + //H
						 "03              102      40000   03   40" + //I
						 "0      1                               0" + //J
						 "0 s   10                               0" + //K
						 "0    1002            12                0" + //L
						 "000000000000050000 000000000000500005000" + //M
						 "                 0 0                    " + //N
						 "                 000                    ";  //O*/
	
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
				if (code == 's') {
					playerSpawn.setX(20f+i*40f);
					playerSpawn.setY(580f-j*40f);
				} else if (code == 'b') {
					BadBot badbot = new BadBot();
					badbot.setPos(20f+i*40f, 580f-j*40f);
					badbot.setSize(100f);
					badbot.spawn();
					entities.add(badbot);
				} else if (code == '0') {
					SolidBlock block = new SolidBlock(0);
					block.setPos(20f+i*40f, 580f-j*40f);
					block.setSize(40f);
					block.spawn();
					blocks.add(block);
				} else if (code == '1') {
					SolidBlock block = new SolidBlock(1);
					block.setPos(20f+i*40f, 580f-j*40f);
					block.setSize(40f);
					block.spawn();
					blocks.add(block);
				} else if (code == '2') {
					Block block = new SolidBlock(2);
					block.setPos(20f+i*40f, 580f-j*40f);
					block.setSize(40f);
					block.spawn();
					blocks.add(block);
				} else if (code == '3') {
					Block block = new SolidBlock(3);
					block.setPos(20f+i*40f, 580f-j*40f);
					block.setSize(40f);
					block.spawn();
					blocks.add(block);
				} else if (code == '4') {
					Block block = new SolidBlock(4);
					block.setPos(20f+i*40f, 580f-j*40f);
					block.setSize(40f);
					block.spawn();
					blocks.add(block);
				} else if (code == '5') {
					BumperBlock block = new BumperBlock();
					block.setPos(20f+i*40f, 580f-j*40f);
					block.setSize(40f);
					block.spawn();
					blocks.add(block);
				}
			}
		}
	}
	
}
