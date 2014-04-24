package zan.tecbot.object.entity;

import org.lwjgl.util.vector.Vector2f;

import zan.game.object.BaseObject;
import zan.game.object.Collision;
import zan.game.object.Shape;
import zan.game.sprite.AnimatedSprite;
import zan.game.sprite.ISprite;
import zan.game.sprite.Sprite;
import zan.game.sprite.SpriteManager;
import zan.game.util.GameUtility;
import zan.tecbot.object.block.Block;
import zan.tecbot.panel.GridMap;

public class GummBot extends BadBot {
	
	protected ISprite[] sprite;
	
	protected boolean turnOnEdge;
	protected boolean jumpOnEdge;
	
	protected int turnFlag;
	protected boolean jumpFlag;
	
	public GummBot() {
		super();
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
		jumpOnEdge = true;
		turnFlag = 0;
		jumpFlag = false;
	}
	
	public boolean collide(BaseObject obj, Collision col) {
		if (super.collide(obj, col)) {
			if (obj instanceof Block) {
				if (!col.normFriction()) {
					// QUICK FIX: turns randomly if in air
					Vector2f foot = getSupportPoint(new Vector2f(0f, -1f));
					if (turnFlag == 0 && foot.y < obj.getGroundSupportPoint(foot).y-15f) {
						if (!moving && GameUtility.getRnd().nextInt(100) > 5) return false;
						if (facing == 0) facing = 1;
						else if (facing == 1) facing = 0;
						turnFlag = 50;
					}
				}
			}
			return true;
		}
		return false;
	}
	
	public void correction() {
		if (isAlive()) {
			if (onground && !ground) {
				if (jumpOnEdge) jumpFlag = true;
			}
		}
		super.correction();
	}
	
	public void update() {
		AnimatedSprite anim = (AnimatedSprite) sprite[1];
		moving = false;
		if (isAlive()) {
			if (ground) {
				setDY(-5f);
				// QUICK FIX: getSize()/4f
				if (facing == 1) {
					moving = true;
					applyForceX(-0.5f);
					if (turnOnEdge && turnFlag == 0 && !GridMap.isSolidBlock(GridMap.getTileX(getX())-1, GridMap.getTileY(getY()-(getSize()/4f))-1) && !GridMap.isSolidBlock(GridMap.getTileX(getX())-1, GridMap.getTileY(getY()-(getSize()/4f))-2)) {
						facing = 0;
						turnFlag = 50;
					}
				} else if (facing == 0) {
					moving = true;
					applyForceX(0.5f);
					if (turnOnEdge && turnFlag == 0 && !GridMap.isSolidBlock(GridMap.getTileX(getX())+1, GridMap.getTileY(getY()-(getSize()/4f))-1) && !GridMap.isSolidBlock(GridMap.getTileX(getX())+1, GridMap.getTileY(getY()-(getSize()/4f))-2)) {
						facing = 1;
						turnFlag = 50;
					}
				}
			} else {
				applyForceY(-0.25f);
				if (jumpFlag) {
					jumpFlag = false;
					setDY(0f);
					applyForceY(7f);
					ground = false;
					onground = false;
				}
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
		if (turnFlag > 0) turnFlag--;
		if (getY() < -1000f) {
			if (isAlive()) inflictDamage(3f);
			else despawn();
		}
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
		
		/*glDisable(GL_TEXTURE_2D);
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
		glEnable(GL_TEXTURE_2D);*/
	}
	
}
