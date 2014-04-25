package zan.tecbot.mechanism.weapon;

import java.util.ArrayList;

import zan.tecbot.mechanism.Player;
import zan.tecbot.object.bullet.Bullet;

public class NoWeapon extends Weapon {
	
	public NoWeapon(ArrayList<Bullet> sb, Player su) {super(sb, su);}
	
	public void update() {}
	public void trigger() {}
	public void release() {}
	public void onTrigger() {user.regEnergy();}
	public void onRelease() {user.regEnergy();}
	
}
