package zan.tecbot.panel;

import java.util.ArrayList;

import zan.tecbot.object.Block;

public class GridMap {
						//0000000000111111111122222222223333333333
	private int w, h;	//0123456789012345678901234567890123456789
	private String map = "0                                       " + //A
						 "0                                       " + //B
						 "0                                       " + //C
						 "00002       10002                       " + //D
						 "  4002      43 43                       " + //E
						 "   4002                                 " + //F
						 "    400                                 " + //G
						 "00        402                         00" + //H
						 "03              0                     40" + //I
						 "0      1                               0" + //J
						 "0     10      0                        0" + //K
						 "0    100                  12           0" + //L
						 "000000000000000000 000000000000000000000" + //M
						 "                 0 0                    " + //N
						 "                 000                    ";  //O
	
	public GridMap() {
		w = 40;
		h = 15;
	}
	
	public void createMap(ArrayList<Block> blocks) {
		if (map.length() != w*h) return;
		for (int j=0;j<h;j++) {
			for (int i=0;i<w;i++) {
				char code = map.charAt(i+j*w);
				if (code == '0') {
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
				}
			}
		}
	}
	
}
