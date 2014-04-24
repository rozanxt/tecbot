package zan.tecbot.panel;

import zan.tecbot.object.bullet.PlasmaBullet;

public class PlasmaCannon extends Weapon {
	
	protected int energyLoad;
	
	public PlasmaCannon(Player su) {
		super(su);
	}
	
	public void update() {}
	
	public void trigger() {
		user.drainEnergy(10f);
	}
	
	public void release() {
		if (energyLoad > 0) {
			float shotangle=user.gamePanel.getTecbot().getGunAngle();
			PlasmaBullet b = new PlasmaBullet();
			b.setPos(user.gamePanel.getTecbot().getX()+(float)Math.cos(shotangle*(Math.PI/180f))*40f, user.gamePanel.getTecbot().getY()-(float)Math.sin(shotangle*(Math.PI/180f))*40f);
			b.setVel((float)Math.cos(shotangle*(Math.PI/180f))*b.getSpeed(), -(float)Math.sin(shotangle*(Math.PI/180f))*b.getSpeed());
			b.setAngle(shotangle);
			if (energyLoad > 50) {
				b.setSize(energyLoad*0.2f);
				b.setDamage((float)Math.floor(energyLoad/40)*10f);
			}
			b.spawn();
			user.gamePanel.getBullets().add(b);
			energyLoad = 0;
		}
	}
	
	public void onTrigger() {
		if (user.energy > 0f) {
			energyLoad++;
			if (energyLoad > 200) energyLoad = 200;
			else user.drainEnergy(0.5f);
		}
	}
	
	public void onRelease() {
		user.regEnergy();
	}
	
}
