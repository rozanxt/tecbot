package zan.tecbot.object.block;

import zan.game.object.Shape;

public class SolidBlock extends Block {
	
	protected int type;
	
	public SolidBlock(int st) {
		super();
		type = st;
		shape = new Shape();
		if (type == 1) {
			shape.addPoint(0f, 0f);
			shape.addPoint(1f, 1f);
			shape.addPoint(1f, 0f);
		} else if (type == 2) {
			shape.addPoint(0f, 0f);
			shape.addPoint(0f, 1f);
			shape.addPoint(1f, 0f);
		} else if (type == 3) {
			shape.addPoint(0f, 0f);
			shape.addPoint(0f, 1f);
			shape.addPoint(1f, 1f);
		} else if (type == 4) {
			shape.addPoint(0f, 1f);
			shape.addPoint(1f, 1f);
			shape.addPoint(1f, 0f);
		} else {
			shape.addPoint(0f, 0f);
			shape.addPoint(0f, 1f);
			shape.addPoint(1f, 1f);
			shape.addPoint(1f, 0f);
		}
		shape.fix();
	}
	
}
