package zan.tecbot.mechanism.weapon;

import java.util.ArrayList;

import zan.tecbot.mechanism.Player;
import zan.tecbot.object.bullet.Bullet;
import zan.tecbot.object.bullet.PlasmaBullet;

public class PlasmaCannon extends Weapon {
	
	protected int energyLoad;
	
	public PlasmaCannon(ArrayList<Bullet> sb, Player su) {
		super(sb, su);
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
			if (energyLoad >= 50) {
				b.setSize(energyLoad*0.2f);
				b.setDamage((float)Math.floor(energyLoad/20)*10f);
			}
			b.setPlayerBullet(true);
			b.spawn();
			bullets.add(b);
			energyLoad = 0;
		}
	}
	
	public void onTrigger() {
		if (user.getEnergy() > 0f) {
			energyLoad++;
			if (energyLoad > 100) energyLoad = 100;
			else user.drainEnergy(0.5f);
		}
		user.getTecbot().fullyLoadedPlasma(energyLoad);
	}
	
	public void onRelease() {
		user.regEnergy();
		user.getTecbot().fullyLoadedPlasma(0f);
	}
	
	public void cancel() {energyLoad = 0;}
	
}
