package zan.tecbot.panel;

public class NoWeapon extends Weapon {
	
	public NoWeapon(Player su) {super(su);}
	
	public void update() {}
	public void trigger() {}
	public void release() {}
	public void onTrigger() {user.regEnergy();}
	public void onRelease() {user.regEnergy();}
	
}
