package zan.tecbot.mechanism.weapon;

import java.util.ArrayList;

import zan.tecbot.mechanism.Player;
import zan.tecbot.object.bullet.Bullet;

public abstract class Weapon {
	
	protected ArrayList<Bullet> bullets;
	protected Player user;
	
	public Weapon(ArrayList<Bullet> sb, Player su) {
		bullets = sb;
		user = su;
	}
	
	public abstract void update();
	public abstract void trigger();
	public abstract void release();
	public abstract void onTrigger();
	public abstract void onRelease();
	
}
