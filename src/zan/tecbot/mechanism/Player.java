package zan.tecbot.mechanism;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import zan.game.input.InputManager;
import zan.game.input.MouseEvent;
import zan.tecbot.mechanism.weapon.GatlingGun;
import zan.tecbot.mechanism.weapon.NoWeapon;
import zan.tecbot.mechanism.weapon.PlasmaCannon;
import zan.tecbot.mechanism.weapon.Weapon;
import zan.tecbot.object.bullet.Bullet;
import zan.tecbot.object.entity.Tecbot;
import zan.tecbot.panel.GamePanel;

public class Player {
	
	protected GamePanel gamePanel;
	
	protected Weapon[] weapons;
	protected int weapon;
	protected boolean trigger;
	protected float energy;
	protected float energyReg;
	protected float maxEnergy;
	
	public Player(GamePanel gp) {
		gamePanel = gp;
		weapons = new Weapon[3];
		weapons[0] = new NoWeapon(this);
		weapons[1] = new PlasmaCannon(this);
		weapons[2] = new GatlingGun(this);
		weapon = 0;
		trigger = false;
		energy = 100f;
		energyReg = 0.5f;
		maxEnergy = 200f;
	}
	
	public Tecbot getTecbot() {return gamePanel.getTecbot();}
	public ArrayList<Bullet> getBullets() {return gamePanel.getBullets();}
	
	public int getEnergyLoad() {
		PlasmaCannon pc = (PlasmaCannon)weapons[1];
		return pc.getEnergyLoad();
	}
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
	
	public void setWeapon(int sw) {weapon = sw;}
	public int getWeapon() {return weapon;}
	
	public void regEnergy() {
		energy += energyReg;
		if (energy > maxEnergy) energy = maxEnergy;
	}
	public void drainEnergy(float ed) {
		energy -= ed;
		if (energy < 0f) energy = 0f;
	}
	public float getEnergy() {return energy;}
	public float getMaxEnergy() {return maxEnergy;}
	
	public void input() {
		ArrayList<MouseEvent> mouseEvents = InputManager.getMouseEvents();
		if (gamePanel.getTecbot().isAlive()) {
			for (int e=0;e<mouseEvents.size();e++) {
				MouseEvent event = mouseEvents.get(e);
				if (event.isButtonDown()) {
					if (event.isButton(0)) {
						trigger = true;
						weapons[weapon].trigger();
					}
				} else {
					if (event.isButton(0)) {
						weapons[weapon].release();
						trigger = false;
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
		}
	}
	
}
