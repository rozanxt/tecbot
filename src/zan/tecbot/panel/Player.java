package zan.tecbot.panel;

import org.lwjgl.input.Keyboard;

import zan.game.input.InputManager;
import zan.game.util.GameUtility;
import zan.tecbot.object.bullet.*;

public class Player {
	
	private GamePanel gamePanel;
	
	private int weapon;
	private int shotdelay;
	
	public Player(GamePanel gp) {
		gamePanel = gp;
		weapon = 0;
		shotdelay = 0;
	}
	
	public void setWeapon(int sw) {weapon = sw;}
	public int getWeapon() {return weapon;}
	
	public void input() {
		if (gamePanel.getTecbot().isAlive()) {
			if (InputManager.isKeyPressed(Keyboard.KEY_0)) setWeapon(0);
			else if (InputManager.isKeyPressed(Keyboard.KEY_1)) setWeapon(1);
			else if (InputManager.isKeyPressed(Keyboard.KEY_2)) setWeapon(2);
			
			if (InputManager.isButtonDown(0) && shotdelay == 0) {
				if (getWeapon() == 1) {
					float shotangle=gamePanel.getTecbot().getGunAngle();
					PlasmaBullet b = new PlasmaBullet();
					b.setPos(gamePanel.getTecbot().getX()+(float)Math.cos(shotangle*(Math.PI/180f))*40f, gamePanel.getTecbot().getY()-(float)Math.sin(shotangle*(Math.PI/180f))*40f);
					b.setVel((float)Math.cos(shotangle*(Math.PI/180f))*b.getSpeed(), -(float)Math.sin(shotangle*(Math.PI/180f))*b.getSpeed());
					b.setAngle(shotangle);
					b.spawn();
					gamePanel.getBullets().add(b);
					shotdelay = 50;
				} else if (getWeapon() == 2) {
					float shotangle=gamePanel.getTecbot().getGunAngle()+GameUtility.getRnd().nextInt(20)*0.1f-1f;
					GatlingBullet b = new GatlingBullet();
					b.setPos(gamePanel.getTecbot().getX()+(float)Math.cos(shotangle*(Math.PI/180f))*20f, gamePanel.getTecbot().getY()-(float)Math.sin(shotangle*(Math.PI/180f))*20f);
					b.setVel((float)Math.cos(shotangle*(Math.PI/180f))*b.getSpeed(), -(float)Math.sin(shotangle*(Math.PI/180f))*b.getSpeed());
					b.setAngle(shotangle);
					b.spawn();
					gamePanel.getBullets().add(b);
				}
			}
		}
		if (shotdelay > 0) shotdelay--;
	}
	
}
