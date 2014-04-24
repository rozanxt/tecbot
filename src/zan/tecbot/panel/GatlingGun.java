package zan.tecbot.panel;

import zan.game.util.GameUtility;
import zan.tecbot.object.bullet.GatlingBullet;

public class GatlingGun extends Weapon {
	
	protected int ammo;
	protected int maxAmmo;
	protected int burnout;
	
	public GatlingGun(Player su) {
		super(su);
		ammo = 1000;
		maxAmmo = 1000;
		burnout = 0;
	}
	
	public void addAmmo(int sa) {
		ammo += sa;
		if (ammo > maxAmmo) ammo = maxAmmo;
	}
	public int getAmmo() {return ammo;}
	public int getMaxAmmo() {return maxAmmo;}
	
	public void update() {
		if (burnout > 0) burnout--;
	}
	
	public void trigger() {}
	
	public void release() {}
	
	public void onTrigger() {
		if (burnout == 0 && ammo > 0) {
			float shotangle=user.gamePanel.getTecbot().getGunAngle()+GameUtility.getRnd().nextInt(20)*0.1f-1f;
			GatlingBullet b = new GatlingBullet();
			b.setPos(user.gamePanel.getTecbot().getX()+(float)Math.cos(shotangle*(Math.PI/180f))*20f, user.gamePanel.getTecbot().getY()-(float)Math.sin(shotangle*(Math.PI/180f))*20f);
			b.setVel((float)Math.cos(shotangle*(Math.PI/180f))*b.getSpeed(), -(float)Math.sin(shotangle*(Math.PI/180f))*b.getSpeed());
			b.setAngle(shotangle);
			b.spawn();
			user.gamePanel.getBullets().add(b);
			ammo--;
			
			user.drainEnergy(1f);
			if (user.energy == 0f) burnout = 200;
		} else user.regEnergy();
	}
	
	public void onRelease() {
		user.regEnergy();
	}
	
}
