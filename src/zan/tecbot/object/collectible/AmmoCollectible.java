package zan.tecbot.object.collectible;

import zan.game.object.Shape;
import zan.tecbot.mechanism.Player;

public class AmmoCollectible extends Collectible {
	
	public AmmoCollectible(Player sc) {
		super(sc);
		setSize(30f);
		shape = new Shape();
		shape.addPoint(0f, 0.5f);
		shape.addPoint(0.5f, 1f);
		shape.addPoint(1f, 0.5f);
		shape.addPoint(0.5f, 0f);
		shape.fix();
		setValue(300f);
		setRecurTime(30*60);
	}
	
	public void collected() {
		if (collector.getAmmo() >= collector.getMaxAmmo()) return;
		collector.addAmmo((int)getValue());
		super.collected();
	}
	
}
