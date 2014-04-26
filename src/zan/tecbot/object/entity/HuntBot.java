package zan.tecbot.object.entity;

import java.util.ArrayList;

import zan.game.object.Shape;
import zan.game.sprite.AnimatedSprite;
import zan.game.sprite.ISprite;
import zan.game.sprite.Sprite;
import zan.game.sprite.SpriteManager;
import zan.tecbot.mechanism.GridMap;
import zan.tecbot.object.bullet.Bullet;
import zan.tecbot.object.bullet.SightLine;

public class HuntBot extends BadBot {
	
	protected ISprite[] sprite;
	
	protected ArrayList<Bullet> bullets;
	protected BaseEntity target;
	
	protected SightLine sight;
	protected int targetFocused;
	protected boolean jumpOnEdge;
	protected int fallFlag;
	
	public HuntBot(ArrayList<Bullet> sb, GridMap gm) {
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
		setCap(3f, 10f);
		stompAble = true;
		bullets = sb;
		target = null;
		sight = new SightLine(this);
		sight.setRange(400f);
		sight.spawn();
		bullets.add(sight);
		targetInSight = false;
		targetFocused = 0;
		jumpOnEdge = false;
		fallFlag = 0;
	}
	
	public void setTarget(BaseEntity st) {target = st;}
	public void targetInSight(boolean ss) {
		targetInSight = ss;
		if (targetInSight) targetFocused = 100;
	}
	
	public void despawn() {
		sight.despawn();
		super.despawn();
	}
	
	public float getViewAngle() {
		if (target != null) {
			float viewangle = 180f;
			float ox = target.getX()-getX();
			float oy = target.getY()-getY();
			if (ox > 0f && facing == 0) {
				if (oy > 0f) viewangle = 360f-(float)(Math.atan(oy/ox)*(180f/Math.PI));
				else if (oy < 0f) viewangle = -(float)(Math.atan(oy/ox)*(180f/Math.PI));
				else viewangle = 0f;
			} else if (ox < 0f && facing == 1) {
				viewangle = 180f-(float)(Math.atan(oy/ox)*(180f/Math.PI));
			} else if (ox == 0f) {
				if (oy > 0f) viewangle = -90f;
				else if (oy < 0f) viewangle = 90f;
			} else return super.getViewAngle();
			return viewangle;
		}
		return super.getViewAngle();
	}
	
	public void update() {
		AnimatedSprite anim = (AnimatedSprite) sprite[1];
		int tx = GridMap.getTileX(getX());
		int ty = GridMap.getTileY(getY()-(getSize()/4f));
		moving = false;
		
		if (isAlive()) {
			if (target != null && target.isAlive()) {
				if (targetFocused > 0) {
					float ox = target.getX()-getX();
					float oy = target.getY()-getY();
					if (Math.abs(ox) > 20f) moving = true;
					else moving = false;
					if (ox < 0) facing = 1;
					else if (ox > 0) facing = 0;
					if (oy > 0) jumpOnEdge = true;
					else jumpOnEdge = false;
				}
			}
			
			if (ground) {
				setDY(-5f);
				if (targetFocused > 0) {
					setCap(3f, 10f);
					if (facing == 1) {
						if (!gridMap.isSolidBlock(tx-1, ty-1) && !gridMap.isSolidBlock(tx-1, ty-2) && !gridMap.isSolidBlockType(tx, ty, 2)) {
							if (jumpOnEdge) {
								setDY(0f);
								applyForceY(7f);
								ground = false;
								onground = false;
							} else {
								if (moving) applyForceX(-0.5f);
							}
						} else if ((gridMap.isSolidBlockType(tx-1, ty, 1) || gridMap.isSolidBlockType(tx-1, ty, 2)) && gridMap.isSolidBlock(tx-1, ty+2)) {
							moving = false;
						} else if ((gridMap.isSolidBlockType(tx, ty, 1) || gridMap.isSolidBlockType(tx, ty, 2)) && !gridMap.isSolidBlockType(tx-1, ty+1, 0)) {
							if (moving) applyForceX(-0.5f);
						} else if (gridMap.isSolidBlockType(tx-1, ty, 0) && !gridMap.isSolidBlock(tx-1, ty+2) && !gridMap.isSolidBlock(tx-1, ty+3) && !gridMap.isSolidBlock(tx, ty+2) && !gridMap.isSolidBlock(tx, ty+3) && !gridMap.isSolidBlockType(tx, ty, 1) && !gridMap.isSolidBlockType(tx, ty, 2)) {
							setDY(0f);
							applyForceY(7f);
							ground = false;
							onground = false;
						} else if (!gridMap.isSolidBlockType(tx-1, ty, 0) && !gridMap.isSolidBlockType(tx-1, ty+1, 0) && !gridMap.isSolidBlockType(tx-1, ty, 1)) {
							if (moving) applyForceX(-0.5f);
						} else {
							moving = false;
						}
					} else if (facing == 0) {
						if (!gridMap.isSolidBlock(tx+1, ty-1) && !gridMap.isSolidBlock(tx+1, ty-2) && !gridMap.isSolidBlockType(tx, ty, 1)) {
							if (jumpOnEdge) {
								setDY(0f);
								applyForceY(7f);
								ground = false;
								onground = false;
							} else {
								if (moving) applyForceX(0.5f);
							}
						} else if ((gridMap.isSolidBlockType(tx+1, ty, 1) || gridMap.isSolidBlockType(tx+1, ty, 2)) && gridMap.isSolidBlock(tx+1, ty+2)) {
							moving = false;
						} else if ((gridMap.isSolidBlockType(tx, ty, 1) || gridMap.isSolidBlockType(tx, ty, 2)) && !gridMap.isSolidBlockType(tx+1, ty+1, 0)) {
							if (moving) applyForceX(0.5f);
						} else if (gridMap.isSolidBlockType(tx+1, ty, 0) && !gridMap.isSolidBlock(tx+1, ty+2) && !gridMap.isSolidBlock(tx+1, ty+3) && !gridMap.isSolidBlock(tx, ty+2) && !gridMap.isSolidBlock(tx, ty+3) && !gridMap.isSolidBlockType(tx, ty, 1) && !gridMap.isSolidBlockType(tx, ty, 2)) {
							setDY(0f);
							applyForceY(7f);
							ground = false;
							onground = false;
						} else if (!gridMap.isSolidBlockType(tx+1, ty, 0) && !gridMap.isSolidBlockType(tx+1, ty+1, 0) && !gridMap.isSolidBlockType(tx+1, ty, 2)) {
							if (moving) applyForceX(0.5f);
						} else {
							moving = false;
						}
					}
				} else {
					setCap(2f, 10f);
					if (facing == 1) {
						if (!gridMap.isSolidBlock(tx-1, ty-1) && !gridMap.isSolidBlock(tx-1, ty-2) && !gridMap.isSolidBlockType(tx, ty, 2)) {
							if (fallFlag == 0) {
								fallFlag = 30;
								facing = 0;
							} else {
								moving = true;
								applyForceX(-0.5f);
							}
						} else if ((gridMap.isSolidBlockType(tx-1, ty, 1) || gridMap.isSolidBlockType(tx-1, ty, 2)) && gridMap.isSolidBlock(tx-1, ty+2)) {
							facing = 0;
						} else if ((gridMap.isSolidBlockType(tx, ty, 1) || gridMap.isSolidBlockType(tx, ty, 2)) && !gridMap.isSolidBlockType(tx-1, ty+1, 0)) {
							moving = true;
							applyForceX(-0.5f);
						} else if (gridMap.isSolidBlockType(tx-1, ty, 0) && !gridMap.isSolidBlock(tx-1, ty+1) && !gridMap.isSolidBlock(tx, ty+2) && !gridMap.isSolidBlock(tx-1, ty+2) && !gridMap.isSolidBlockType(tx, ty, 1) && !gridMap.isSolidBlockType(tx, ty, 2)) {
							setDY(0f);
							applyForceY(7f);
							ground = false;
							onground = false;
						} else if (!gridMap.isSolidBlockType(tx-1, ty, 0) && !gridMap.isSolidBlockType(tx-1, ty+1, 0) && !gridMap.isSolidBlockType(tx-1, ty, 1)) {
							moving = true;
							applyForceX(-0.5f);
						} else {
							facing = 0;
						}
					} else if (facing == 0) {
						if (!gridMap.isSolidBlock(tx+1, ty-1) && !gridMap.isSolidBlock(tx+1, ty-2) && !gridMap.isSolidBlockType(tx, ty, 1)) {
							if (fallFlag == 0) {
								fallFlag = 30;
								facing = 1;
							} else {
								moving = true;
								applyForceX(-0.5f);
							}
						} else if ((gridMap.isSolidBlockType(tx+1, ty, 1) || gridMap.isSolidBlockType(tx+1, ty, 2)) && gridMap.isSolidBlock(tx+1, ty+2)) {
							facing = 1;
						} else if ((gridMap.isSolidBlockType(tx, ty, 1) || gridMap.isSolidBlockType(tx, ty, 2)) && !gridMap.isSolidBlockType(tx+1, ty+1, 0)) {
							moving = true;
							applyForceX(0.5f);
						} else if (gridMap.isSolidBlockType(tx+1, ty, 0) && !gridMap.isSolidBlock(tx+1, ty+1) && !gridMap.isSolidBlock(tx, ty+2) && !gridMap.isSolidBlock(tx+1, ty+2) && !gridMap.isSolidBlockType(tx, ty, 1) && !gridMap.isSolidBlockType(tx, ty, 2)) {
							setDY(0f);
							applyForceY(7f);
							ground = false;
							onground = false;
						} else if (!gridMap.isSolidBlockType(tx+1, ty, 0) && !gridMap.isSolidBlockType(tx+1, ty+1, 0) && !gridMap.isSolidBlockType(tx+1, ty, 2)) {
							moving = true;
							applyForceX(0.5f);
						} else {
							facing = 1;
						}
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
		if (targetFocused > 0) targetFocused--;
		if (fallFlag > 0) fallFlag--;
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
	}
	
}
