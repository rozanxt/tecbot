package zan.tecbot.mechanism.weapon;

import java.util.ArrayList;

import zan.game.util.GameUtility;
import zan.tecbot.mechanism.Player;
import zan.tecbot.object.bullet.Bullet;
import zan.tecbot.object.bullet.GatlingBullet;

public class GatlingGun extends Weapon {
	
	protected int ammo;
	protected int maxAmmo;
	protected int burnout;
	
	public GatlingGun(ArrayList<Bullet> sb, Player su) {
		super(sb, su);
		ammo = 500;
		maxAmmo = 1000;
		burnout = 0;
	}
	
	public void addAmmo(int sa) {
		ammo += sa;
		if (ammo > maxAmmo) ammo = maxAmmo;
	}
	public int getAmmo() {return ammo;}
	public int getMaxAmmo() {return maxAmmo;}
	
	public boolean isBurnedOut() {
		if (burnout > 0) return true;
		return false;
	}
	
	public void update() {
		if (isBurnedOut()) burnout--;
	}
	
	public void trigger() {}
	
	public void release() {}
	
	public void onTrigger() {
		if (!isBurnedOut() && ammo > 0) {
			float shotangle=user.getTecbot().getGunAngle()+GameUtility.getRnd().nextInt(20)*0.1f-1f;
			GatlingBullet b = new GatlingBullet();
			b.setPos(user.getTecbot().getX()+(float)Math.cos(shotangle*(Math.PI/180f))*20f, user.getTecbot().getY()-(float)Math.sin(shotangle*(Math.PI/180f))*20f);
			b.setVel((float)Math.cos(shotangle*(Math.PI/180f))*b.getSpeed(), -(float)Math.sin(shotangle*(Math.PI/180f))*b.getSpeed());
			b.setAngle(shotangle);
			b.setPlayerBullet(true);
			b.spawn();
			bullets.add(b);
			ammo--;
			
			user.drainEnergy(0.5f);
			if (user.getEnergy() == 0f) burnout = 200;
		} else user.regEnergy();
	}
	
	public void onRelease() {
		user.regEnergy();
	}
	
}
