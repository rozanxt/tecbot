package zan.tecbot.mechanism.weapon;

import zan.tecbot.mechanism.Player;
import zan.tecbot.object.bullet.PlasmaBullet;

public class PlasmaCannon extends Weapon {
	
	protected int energyLoad;
	
	public PlasmaCannon(Player su) {
		super(su);
	}
	
	public int getEnergyLoad() {return energyLoad;}
	
	public void update() {}
	
	public void trigger() {
		user.drainEnergy(10f);
	}
	
	public void release() {
		if (energyLoad > 0) {
			float shotangle=user.getTecbot().getGunAngle();
			PlasmaBullet b = new PlasmaBullet();
			b.setPos(user.getTecbot().getX()+(float)Math.cos(shotangle*(Math.PI/180f))*40f, user.getTecbot().getY()-(float)Math.sin(shotangle*(Math.PI/180f))*40f);
			b.setVel((float)Math.cos(shotangle*(Math.PI/180f))*b.getSpeed(), -(float)Math.sin(shotangle*(Math.PI/180f))*b.getSpeed());
			b.setAngle(shotangle);
			if (energyLoad > 50) {
				b.setSize(energyLoad*0.2f);
				b.setDamage((float)Math.floor(energyLoad/20)*10f);
			}
			b.spawn();
			user.getBullets().add(b);
			energyLoad = 0;
		}
	}
	
	public void onTrigger() {
		if (user.getEnergy() > 0f) {
			energyLoad++;
			if (energyLoad > 100) energyLoad = 100;
			else user.drainEnergy(0.5f);
		}
	}
	
	public void onRelease() {
		user.regEnergy();
	}
	
}
