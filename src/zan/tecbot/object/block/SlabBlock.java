package zan.tecbot.object.block;

import zan.game.object.Shape;

public class SlabBlock extends Block {
	
	public SlabBlock(int sx, int sy) {
		super(sx, sy);
		shape = new Shape();
		shape.addPoint(0f, 0f);
		shape.addPoint(0f, 0.5f);
		shape.addPoint(1f, 0.5f);
		shape.addPoint(1f, 0f);
		shape.fix();
		setSolid(true);
		setBottomPass(true);
	}
	
}
