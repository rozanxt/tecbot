package zan.tecbot.panel;

import org.lwjgl.input.Keyboard;

import zan.game.input.InputManager;
import zan.game.util.GameUtility;
import zan.tecbot.object.bullet.*;
import zan.tecbot.object.entity.Tecbot;

public class Player {
	
	private GamePanel gamePanel;
	private Tecbot playerBot;
	
	private int weapon;
	private int shotdelay;
	
	public Player(GamePanel gp, Tecbot bot) {
		gamePanel = gp;
		playerBot = bot;
		weapon = 0;
		shotdelay = 0;
	}
	
	public void setWeapon(int sw) {weapon = sw;}
	public int getWeapon() {return weapon;}
	
	public void input() {
		if (InputManager.isKeyPressed(Keyboard.KEY_0)) setWeapon(0);
		else if (InputManager.isKeyPressed(Keyboard.KEY_1)) setWeapon(1);
		else if (InputManager.isKeyPressed(Keyboard.KEY_2)) setWeapon(2);
		
		if (InputManager.isButtonDown(0) && shotdelay == 0) {
			if (getWeapon() == 1) {
				float shotangle=playerBot.getGunAngle();
				PlasmaBullet b = new PlasmaBullet();
				b.setPos(playerBot.getX()+(float)Math.cos(shotangle*(Math.PI/180f))*40f, playerBot.getY()-(float)Math.sin(shotangle*(Math.PI/180f))*40f);
				b.setVel((float)Math.cos(shotangle*(Math.PI/180f))*b.getSpeed(), -(float)Math.sin(shotangle*(Math.PI/180f))*b.getSpeed());
				b.setAngle(shotangle);
				b.spawn();
				gamePanel.getBullets().add(b);
				shotdelay = 50;
			} else if (getWeapon() == 2) {
				float shotangle=playerBot.getGunAngle()+GameUtility.getRnd().nextInt(20)*0.1f-1f;
				GatlingBullet b = new GatlingBullet();
				b.setPos(playerBot.getX()+(float)Math.cos(shotangle*(Math.PI/180f))*20f, playerBot.getY()-(float)Math.sin(shotangle*(Math.PI/180f))*20f);
				b.setVel((float)Math.cos(shotangle*(Math.PI/180f))*b.getSpeed(), -(float)Math.sin(shotangle*(Math.PI/180f))*b.getSpeed());
				b.setAngle(shotangle);
				b.spawn();
				gamePanel.getBullets().add(b);
			}
		}
		
		if (shotdelay > 0) shotdelay--;
	}
	
}
