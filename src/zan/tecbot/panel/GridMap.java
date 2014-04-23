package zan.tecbot.panel;

import java.util.ArrayList;

import zan.tecbot.object.BadBot;
import zan.tecbot.object.BaseEntity;
import zan.tecbot.object.Block;

public class GridMap {
						//0000000000111111111122222222223333333333
	private int w, h;	//0123456789012345678901234567890123456789
	private String map = "0                      02               " + //A
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
						 "                 000                    ";  //O
	
	public GridMap() {
		w = 40;
		h = 15;
	}
	
	public void createMap(ArrayList<Block> blocks, ArrayList<BaseEntity> entities) {
		if (map.length() != w*h) return;
		for (int j=0;j<h;j++) {
			for (int i=0;i<w;i++) {
				char code = map.charAt(i+j*w);
				if (code == 'b') {
					BadBot badbot = new BadBot();
					badbot.setPos(20f+i*40f, 580f-j*40f);
					badbot.setSize(100f);
					badbot.spawn();
					entities.add(badbot);
				} else if (code == '0') {
					Block block = new Block(0);
					block.setPos(20f+i*40f, 580f-j*40f);
					block.setSize(40f);
					block.spawn();
					blocks.add(block);
				} else if (code == '1') {
					Block block = new Block(1);
					block.setPos(20f+i*40f, 580f-j*40f);
					block.setSize(40f);
					block.spawn();
					blocks.add(block);
				} else if (code == '2') {
					Block block = new Block(2);
					block.setPos(20f+i*40f, 580f-j*40f);
					block.setSize(40f);
					block.spawn();
					blocks.add(block);
				} else if (code == '3') {
					Block block = new Block(3);
					block.setPos(20f+i*40f, 580f-j*40f);
					block.setSize(40f);
					block.spawn();
					blocks.add(block);
				} else if (code == '4') {
					Block block = new Block(4);
					block.setPos(20f+i*40f, 580f-j*40f);
					block.setSize(40f);
					block.spawn();
					blocks.add(block);
				} else if (code == '5') {
					Block block = new Block(5);
					block.setPos(20f+i*40f, 580f-j*40f);
					block.setSize(40f);
					block.spawn();
					blocks.add(block);
				}
			}
		}
	}
	
}
