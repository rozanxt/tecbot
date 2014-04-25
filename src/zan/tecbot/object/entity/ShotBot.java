package zan.tecbot.object.entity;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import zan.game.object.Shape;
import zan.game.sprite.AnimatedSprite;
import zan.game.sprite.ISprite;
import zan.game.sprite.Sprite;
import zan.game.sprite.SpriteManager;
import zan.game.util.GameUtility;
import zan.tecbot.mechanism.GridMap;
import zan.tecbot.object.bullet.Bullet;
import zan.tecbot.object.bullet.PlasmaBullet;

public class ShotBot extends BadBot {
	
	protected ISprite[] sprite;
	
	protected ArrayList<Bullet> bullets;
	protected BaseEntity target;
	protected float gunangle;
	protected int shotdelay;
	protected boolean targetInSight;
	
	public ShotBot(ArrayList<Bullet> sb, GridMap gm) {
		super(gm);
		setSize(80f);
		shape = new Shape();
		shape.addPoint(0.31f, 0.15f);
		shape.addPoint(0.31f, 0.8f);
		shape.addPoint(0.69f, 0.8f);
		shape.addPoint(0.69f, 0.15f);
		shape.fix();
		
		sprite = new ISprite[2];
		sprite[0] = SpriteManager.getSprite("bot_idle");
		ISprite[] ani = new Sprite[12];
		for (int i=0;i<12;i++) ani[i] = SpriteManager.getSprite("bot_move" + i);
		sprite[1] = new AnimatedSprite((Sprite[])ani);
		AnimatedSprite anim = (AnimatedSprite) sprite[1];
		anim.setAnimation(true, false, 3);
		
		setMaxHealth(50f);
		setCap(2.5f, 10f);
		stompAble = false;
		bullets = sb;
		target = null;
		gunangle = 0f;
		shotdelay = 0;
		targetInSight = false;
	}
	
	public void setTarget(BaseEntity st) {target = st;}
	
	public void update() {
		AnimatedSprite anim = (AnimatedSprite) sprite[1];
		int tx = GridMap.getTileX(getX());
		int ty = GridMap.getTileY(getY()-(getSize()/4f));
		moving = false;
		if (isAlive()) {
			if (target != null) {
				float ox = target.getX()-getX();
				float oy = target.getY()-getY();
				
				if (Math.sqrt(ox*ox+oy*oy) < 800f) targetInSight = true;
				else targetInSight = false;
				
				if (targetInSight && Math.abs(ox) > 50f) moving = true;
				else moving = false;
				if (ox < 0f) facing = 1;
				else if (ox > 0f) facing = 0;
				
				if (ox > 0f) {
					if (oy > 0f) gunangle = 360f-(float)(Math.atan(oy/ox)*(180f/Math.PI));
					else if (oy < 0f) gunangle = -(float)(Math.atan(oy/ox)*(180f/Math.PI));
					else gunangle = 0f;
				} else if (ox < 0f) {
					gunangle = 180f-(float)(Math.atan(oy/ox)*(180f/Math.PI));
				} else {
					if (oy > 0f) gunangle = -90f;
					else if (oy < 0f) gunangle = 90f;
				}
				
				if (targetInSight && shotdelay == 0) {
					float shotangle=gunangle+GameUtility.getRnd().nextInt(10)-5f;
					PlasmaBullet b = new PlasmaBullet();
					b.setPos(getX()+(float)Math.cos(shotangle*(Math.PI/180f))*40f, getY()-(float)Math.sin(shotangle*(Math.PI/180f))*40f);
					b.setVel((float)Math.cos(shotangle*(Math.PI/180f))*b.getSpeed(), -(float)Math.sin(shotangle*(Math.PI/180f))*b.getSpeed());
					b.setAngle(shotangle);
					b.spawn();
					bullets.add(b);
					shotdelay = 100;
				}
			}
			
			if (ground) {
				setDY(-5f);
				if (facing == 1) {
					
					if (!gridMap.isSolidBlock(tx-1, ty-1) && !gridMap.isSolidBlock(tx-1, ty-2)) {
						moving = false;
					} else if ((gridMap.isSolidBlockType(tx-1, ty, 1) || gridMap.isSolidBlockType(tx-1, ty, 2)) && gridMap.isSolidBlock(tx-1, ty+2)) {
						moving = false;
					} else if ((gridMap.isSolidBlockType(tx, ty, 1) || gridMap.isSolidBlockType(tx, ty, 2)) && !gridMap.isSolidBlockType(tx-1, ty+1, 0)) {
						if (moving) applyForceX(-0.5f);
					} else if (gridMap.isSolidBlockType(tx-1, ty, 0) && !gridMap.isSolidBlock(tx-1, ty+1) && !gridMap.isSolidBlock(tx, ty+2) && !gridMap.isSolidBlock(tx-1, ty+2) && !gridMap.isSolidBlockType(tx, ty, 1) && !gridMap.isSolidBlockType(tx, ty, 2)) {
						setDY(0f);
						applyForceY(5f);
						ground = false;
						onground = false;
					} else if (!gridMap.isSolidBlockType(tx-1, ty, 0) && !gridMap.isSolidBlockType(tx-1, ty+1, 0)) {
						if (moving) applyForceX(-0.5f);
					} else {
						moving = false;
					}
				} else if (facing == 0) {
					
					if (!gridMap.isSolidBlock(tx+1, ty-1) && !gridMap.isSolidBlock(tx+1, ty-2)) {
						moving = false;
					} else if ((gridMap.isSolidBlockType(tx+1, ty, 1) || gridMap.isSolidBlockType(tx+1, ty, 2)) && gridMap.isSolidBlock(tx+1, ty+2)) {
						moving = false;
					} else if ((gridMap.isSolidBlockType(tx, ty, 1) || gridMap.isSolidBlockType(tx, ty, 2)) && !gridMap.isSolidBlockType(tx+1, ty+1, 0)) {
						if (moving) applyForceX(0.5f);
					} else if (gridMap.isSolidBlockType(tx+1, ty, 0) && !gridMap.isSolidBlock(tx+1, ty+1) && !gridMap.isSolidBlock(tx, ty+2) && !gridMap.isSolidBlock(tx+1, ty+2) && !gridMap.isSolidBlockType(tx, ty, 1) && !gridMap.isSolidBlockType(tx, ty, 2)) {
						setDY(0f);
						applyForceY(5f);
						ground = false;
						onground = false;
					} else if (!gridMap.isSolidBlockType(tx+1, ty, 0) && !gridMap.isSolidBlockType(tx+1, ty+1, 0)) {
						if (moving) applyForceX(0.5f);
					} else {
						moving = false;
					}
				}
			} else {
				applyForceY(-0.25f);
				if (facing == 1 && moving) {applyForceX(-0.1f);}
				else if (facing == 0 && moving) {applyForceX(0.1f);}
			}
			
			if (onmoving && !moving) anim.setCurFrame(0);
			onmoving = moving;
			angle = 0f;
		} else {
			applyForceY(-0.25f);
			angle = 30f;
		}
		if (shotdelay > 0) shotdelay--;
		ground = false;
		super.update();
		anim.update();
	}
	
	public void render() {
		if (isAlive()) {
			if (moving) sprite[1].render(getX(), getY(), getSize(), angle, facing, 1f);
			else sprite[0].render(getX(), getY(), getSize(), angle, facing, 1f);
		} else sprite[0].render(getX(), getY(), getSize(), angle, facing, 0.5f);
		
		/*glColor4f(1f, 0f, 1f, 1f);
		super.render();
		glColor4f(1f, 1f, 1f, 1f);*/
		
		glDisable(GL_TEXTURE_2D);
		glPushMatrix();
		
		glTranslatef(pos.x, pos.y, 0f);
		glScalef(size, size, 0f);
		glRotatef(-gunangle, 0f, 0f, 1f);
		
		glColor4f(1f, 0f, 0f, 1f);
		glBegin(GL_LINE_LOOP);
			glVertex2f(-0.05f, -0.05f);
			glVertex2f(-0.05f, 0.05f);
			glVertex2f(0.4f, 0.05f);
			glVertex2f(0.4f, -0.05f);
		glEnd();
		glColor4f(1f, 1f, 1f, 1f);
		
		glPopMatrix();
		glEnable(GL_TEXTURE_2D);
	}
	
}
