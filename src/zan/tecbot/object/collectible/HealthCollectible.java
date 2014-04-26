package zan.tecbot.object.collectible;

import zan.game.object.Shape;
import zan.tecbot.mechanism.Player;

public class HealthCollectible extends Collectible {
	
	public HealthCollectible(Player sc) {
		super(sc);
		setSize(30f);
		shape = new Shape();
		shape.addPoint(0f, 1f);
		shape.addPoint(0.25f, 1f);
		shape.addPoint(0.5f, 0.75f);
		shape.addPoint(0.75f, 1f);
		shape.addPoint(1f, 1f);
		shape.addPoint(1f, 0.5f);
		shape.addPoint(0.5f, 0f);
		shape.addPoint(0f, 0.5f);
		shape.fix();
	}
	
	public void collected() {
		if (collector.getHealth() >= collector.getMaxHealth()) return;
		collector.healDamage(50f);
		super.collected();
	}
	
}
