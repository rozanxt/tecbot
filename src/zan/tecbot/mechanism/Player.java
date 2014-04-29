package zan.tecbot.mechanism;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;

import zan.game.input.InputManager;
import zan.game.input.MouseEvent;
import zan.tecbot.mechanism.weapon.*;
import zan.tecbot.object.bullet.Bullet;
import zan.tecbot.object.entity.Tecbot;

public class Player {
	
	protected Tecbot tecbot;
	
	protected Weapon[] weapons;
	protected int weapon;
	protected boolean trigger;
	protected float energy;
	protected float energyReg;
	protected float maxEnergy;
	
	protected int playerLife;
	protected Vector2f playerSpawn;
	
	public Player(Tecbot st, ArrayList<Bullet> sb) {
		tecbot = st;
		weapons = new Weapon[3];
		weapons[0] = new NoWeapon(sb, this);
		weapons[1] = new PlasmaCannon(sb, this);
		weapons[2] = new GatlingGun(sb, this);
		weapon = 1;
		trigger = false;
		energy = 100f;
		energyReg = 0.5f;
		maxEnergy = 200f;
		playerLife = 3;
		playerSpawn = new Vector2f(0f, 0f);
	}
	
	public void spawn() {
		tecbot.spawn();
		tecbot.setPos(playerSpawn.x, playerSpawn.y);
		tecbot.setVel(0f, 0f);
	}
	public void respawn() {
		if (playerLife > 0) {
			tecbot.spawn();
			tecbot.setPos(playerSpawn.x, playerSpawn.y);
			tecbot.setVel(0f, 0f);
			energy = 100f;
			playerLife--;
		}
	}
	public void telespawn() {
		tecbot.setPos(playerSpawn.x, playerSpawn.y);
		tecbot.setVel(0f, 0f);
		tecbot.setAnchor(null);
	}
	
	public Tecbot getTecbot() {return tecbot;}
	
	public int getPlayerLife() {return playerLife;}
	
	public void setPlayerSpawn(float sx, float sy) {playerSpawn.set(sx, sy);}
	public Vector2f getPlayerSpawn() {return playerSpawn;}
	
	public void setWeapon(int sw) {weapon = sw;}
	public int getWeapon() {return weapon;}
	
	public void healDamage(float sh) {
		tecbot.healDamage(sh);
	}
	public float getHealth() {return tecbot.getHealth();}
	public float getMaxHealth() {return tecbot.getMaxHealth();}
	public void addAmmo(int sa) {
		GatlingGun gg = (GatlingGun)weapons[2];
		gg.addAmmo(sa);
	}
	public int getAmmo() {
		GatlingGun gg = (GatlingGun)weapons[2];
		return gg.getAmmo();
	}
	public int getMaxAmmo() {
		GatlingGun gg = (GatlingGun)weapons[2];
		return gg.getMaxAmmo();
	}
	public boolean isBurnedOut() {
		GatlingGun gg = (GatlingGun)weapons[2];
		return gg.isBurnedOut();
	}
	public int getEnergyLoad() {
		PlasmaCannon pc = (PlasmaCannon)weapons[1];
		return pc.getEnergyLoad();
	}
	public float getEnergy() {return energy;}
	public float getMaxEnergy() {return maxEnergy;}
	
	public void regEnergy() {
		energy += energyReg;
		if (energy > maxEnergy) energy = maxEnergy;
	}
	public void drainEnergy(float ed) {
		energy -= ed;
		if (energy < 0f) energy = 0f;
	}
	
	public void input() {
		ArrayList<MouseEvent> mouseEvents = InputManager.getMouseEvents();
		if (tecbot.isAlive()) {
			for (int e=0;e<mouseEvents.size();e++) {
				MouseEvent event = mouseEvents.get(e);
				if (event.isButtonDown()) {
					if (event.isButton(0)) {
						trigger = true;
						weapons[weapon].trigger();
					}
				} else {
					if (event.isButton(0)) {
						trigger = false;
						weapons[weapon].release();
					}
				}
			}
			
			if (trigger) weapons[weapon].onTrigger();
			else {
				weapons[weapon].onRelease();
				if (InputManager.isKeyPressed(Keyboard.KEY_0)) setWeapon(0);
				else if (InputManager.isKeyPressed(Keyboard.KEY_1)) setWeapon(1);
				else if (InputManager.isKeyPressed(Keyboard.KEY_2)) setWeapon(2);
			}
			
			for (int i=0;i<weapons.length;i++) weapons[i].update();
		} else {
			trigger = false;
			for (int i=0;i<weapons.length;i++) weapons[i].cancel();
		}
	}
	
}
