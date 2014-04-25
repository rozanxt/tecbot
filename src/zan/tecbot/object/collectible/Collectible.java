package zan.tecbot.object.collectible;

import zan.game.object.BaseObject;
import zan.game.object.Collision;
import zan.tecbot.mechanism.Player;
import zan.tecbot.object.entity.Tecbot;

public abstract class Collectible extends BaseObject {
	
	protected Player collector;
	
	protected boolean recur;
	protected int recurTime;
	protected int recurCount;
	
	public Collectible(Player sc) {
		collector = sc;
		recur = false;
		recurTime = 0;
		recurCount = 0;
	}
	
	public void collected() {
		if (recurTime > 0) {
			recur = true;
		} else despawn();
	}
	
	public boolean collide(BaseObject obj, Collision col) {
		if (!recur && obj instanceof Tecbot) {
			collected();
			return true;
		}
		return false;
	}
	
	public void update() {
		super.update();
		if (recur) {
			recurCount++;
			if (recurCount > recurTime) {
				recurCount = 0;
				recur = false;
			}
		}
	}
	
	public void render() {
		if (!recur) super.render();
	}
	
}
