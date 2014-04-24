package zan.tecbot.mechanism.weapon;

import zan.tecbot.mechanism.Player;

public abstract class Weapon {
	
	protected Player user;
	
	public Weapon(Player su) {
		user = su;
	}
	
	public abstract void update();
	public abstract void trigger();
	public abstract void release();
	public abstract void onTrigger();
	public abstract void onRelease();
	
}
