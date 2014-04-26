package zan.tecbot.object.entity;

import zan.game.object.Shape;
import zan.game.sprite.AnimatedSprite;
import zan.game.sprite.ISprite;
import zan.game.sprite.Sprite;
import zan.game.sprite.SpriteManager;
import zan.tecbot.mechanism.GridMap;

public class GummBot extends BadBot {
	
	protected ISprite[] sprite;
	
	protected boolean turnOnEdge;
	protected int fallFlag;
	
	public GummBot(GridMap gm) {
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
		stompAble = true;
		turnOnEdge = true;
		fallFlag = 0;
	}
	
	public void update() {
		AnimatedSprite anim = (AnimatedSprite) sprite[1];
		int tx = GridMap.getTileX(getX());
		int ty = GridMap.getTileY(getY()-(getSize()/4f));
		moving = false;
		if (isAlive()) {
			if (ground) {
				setDY(-5f);
				if (facing == 1) {
					if (turnOnEdge && !gridMap.isSolidBlock(tx-1, ty-1) && !gridMap.isSolidBlock(tx-1, ty-2) && !gridMap.isSolidBlockType(tx, ty, 2)) {
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
						applyForceY(5f);
						ground = false;
						onground = false;
					} else if (!gridMap.isSolidBlockType(tx-1, ty, 0) && !gridMap.isSolidBlockType(tx-1, ty+1, 0) && !gridMap.isSolidBlockType(tx-1, ty, 1)) {
						moving = true;
						applyForceX(-0.5f);
					} else {
						facing = 0;
					}
				} else if (facing == 0) {
					if (turnOnEdge && !gridMap.isSolidBlock(tx+1, ty-1) && !gridMap.isSolidBlock(tx+1, ty-2) && !gridMap.isSolidBlockType(tx, ty, 1)) {
						if (fallFlag == 0) {
							fallFlag = 30;
							facing = 1;
						} else {
							moving = true;
							applyForceX(0.5f);
						}
					} else if ((gridMap.isSolidBlockType(tx+1, ty, 1) || gridMap.isSolidBlockType(tx+1, ty, 2)) && gridMap.isSolidBlock(tx+1, ty+2)) {
						facing = 1;
					} else if ((gridMap.isSolidBlockType(tx, ty, 1) || gridMap.isSolidBlockType(tx, ty, 2)) && !gridMap.isSolidBlockType(tx+1, ty+1, 0)) {
						moving = true;
						applyForceX(0.5f);
					} else if (gridMap.isSolidBlockType(tx+1, ty, 0) && !gridMap.isSolidBlock(tx+1, ty+1) && !gridMap.isSolidBlock(tx, ty+2) && !gridMap.isSolidBlock(tx+1, ty+2) && !gridMap.isSolidBlockType(tx, ty, 1) && !gridMap.isSolidBlockType(tx, ty, 2)) {
						setDY(0f);
						applyForceY(5f);
						ground = false;
						onground = false;
					} else if (!gridMap.isSolidBlockType(tx+1, ty, 0) && !gridMap.isSolidBlockType(tx+1, ty+1, 0) && !gridMap.isSolidBlockType(tx+1, ty, 2)) {
						moving = true;
						applyForceX(0.5f);
					} else {
						facing = 1;
					}
				}
			} else {
				applyForceY(-0.25f);
				if (facing == 1) {applyForceX(-0.1f);}
				else if (facing == 0) {applyForceX(0.1f);}
			}
			
			if (onmoving && !moving) anim.setCurFrame(0);
			onmoving = moving;
			angle = 0f;
		} else {
			applyForceY(-0.25f);
			angle = 30f;
		}
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
