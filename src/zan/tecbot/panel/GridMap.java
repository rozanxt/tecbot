package zan.tecbot.panel;

import java.util.ArrayList;

import zan.tecbot.object.Block;

public class GridMap {
	
	private int w, h;
	private String map = "                    " +
						 "                    " +
						 " 4002       10002   " +
						 "  4002      43 43   " +
						 "   4002             " +
						 "    400             " +
						 "          003       " +
						 "                0   " +
						 "       1            " +
						 "      10      0     " +
						 "     100            " +
						 "000000000000000000 0" +
						 "                 0 0" +
						 "                 000" +
						 "                    ";
	
	public GridMap() {
		w = 20;
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
					blocks.add(block);
				} else if (code == '1') {
					Block block = new Block(1);
					block.setPos(20f+i*40f, 580f-j*40f);
					block.setSize(40f);
					blocks.add(block);
				} else if (code == '2') {
					Block block = new Block(2);
					block.setPos(20f+i*40f, 580f-j*40f);
					block.setSize(40f);
					blocks.add(block);
				} else if (code == '3') {
					Block block = new Block(3);
					block.setPos(20f+i*40f, 580f-j*40f);
					block.setSize(40f);
					blocks.add(block);
				} else if (code == '4') {
					Block block = new Block(4);
					block.setPos(20f+i*40f, 580f-j*40f);
					block.setSize(40f);
					blocks.add(block);
				}
			}
		}
	}
	
	
}
