package zan.tecbot.object.entity;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;

import zan.game.GameCore;
import zan.game.input.InputManager;
import zan.game.object.BaseObject;
import zan.game.object.Collision;
import zan.game.object.Shape;
import zan.game.sprite.AnimatedSprite;
import zan.game.sprite.ISprite;
import zan.game.sprite.Sprite;
import zan.game.sprite.SpriteManager;


public class Tecbot extends BaseEntity {
	
	protected ISprite[] sprite;
	
	protected int invulnerable;
	
	protected float gunangle;
	
	public Tecbot() {
		super();
		setSize(100f);
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
		
		setMaxHealth(100f);
		setCap(4f, 10f);
		invulnerable = 0;
		gunangle = 0f;
	}
	
	public void spawn() {
		super.spawn();
		invulnerable = 50;
	}
	
	public float getGunAngle() {return gunangle;}
	
	public boolean collide(BaseObject obj, Collision col) {
		if (super.collide(obj, col)) {
			if (invulnerable == 0 && obj instanceof BadBot) {
				Vector2f norm = col.normal;
				setDY(0f);
				applyForce(norm.x*10f, 4f);
				ground = false;
				onground = false;
				inflictDamage(20f);
				invulnerable = 50;
			}
			return true;
		}
		return false;
	}
	
	public void update() {
		AnimatedSprite anim = (AnimatedSprite) sprite[1];
		moving = false;
		if (isAlive()) {
			if (ground) {
				setDY(-5f);
				if (InputManager.isKeyDown(Keyboard.KEY_W)) {
					setDY(0f);
					applyForceY(7f);
					ground = false;
					onground = false;
				}
				if (InputManager.isKeyDown(Keyboard.KEY_D)) {applyForceX(0.5f); moving = true;}
				if (InputManager.isKeyDown(Keyboard.KEY_A)) {applyForceX(-0.5f); moving = true;}
			} else {
				applyForceY(-0.25f);
				if (InputManager.isKeyDown(Keyboard.KEY_D)) {applyForceX(0.1f);}
				if (InputManager.isKeyDown(Keyboard.KEY_A)) {applyForceX(-0.1f);}
			}
			
			if (onmoving && !moving) anim.setCurFrame(0);
			onmoving = moving;
			angle = 0f;
		} else {
			applyForceY(-0.25f);
			angle = 30f;
		}
		if (invulnerable > 0) invulnerable--;
		if (getY() < -1000f) {
			if (isAlive()) inflictDamage(3f);
			else despawn();
		}
		ground = false;
		super.update();
		anim.update();
		
		if (isAlive()) {
			// QUICK FIX
			float ox = InputManager.getMouseX()-(GameCore.SCR_WIDTH/2f);
			float oy = InputManager.getMouseY()-(GameCore.SCR_HEIGHT/2f);
			
			if (ox > 0f) {
				if (oy > 0f) gunangle = 360f-(float)(Math.atan(oy/ox)*(180f/Math.PI));
				else if (oy < 0f) gunangle = -(float)(Math.atan(oy/ox)*(180f/Math.PI));
				else gunangle = 0f;
				facing = 0;
			} else if (ox < 0f) {
				gunangle = 180f-(float)(Math.atan(oy/ox)*(180f/Math.PI));
				facing = 1;
			} else {
				if (oy > 0f) gunangle = -90f;
				else if (oy < 0f) gunangle = 90f;
			}
		}
	}
	
	public void render() {
		if (isAlive()) {
			if (invulnerable > 0) {
				if (moving) sprite[1].render(getX(), getY(), getSize(), angle, facing, 0.75f);
				else sprite[0].render(getX(), getY(), getSize(), angle, facing, 0.75f);
			} else {
				if (moving) sprite[1].render(getX(), getY(), getSize(), angle, facing, 1f);
				else sprite[0].render(getX(), getY(), getSize(), angle, facing, 1f);
			}
		} else sprite[0].render(getX(), getY(), getSize(), angle, facing, 0.5f);
		
		/*glColor4f(1f, 0f, 1f, 1f);
		super.render();
		glColor4f(1f, 1f, 1f, 1f);*/
		
		if (isAlive()) {
			glPushMatrix();
			
			glTranslatef(pos.x, pos.y, 0f);
			glScalef(size, size, 0f);
			glRotatef(-gunangle, 0f, 0f, 1f);
			
			Sprite spr = (Sprite)SpriteManager.getSprite("plasmacannon");
			glBindTexture(GL_TEXTURE_2D, spr.getTextureID());
			glBegin(GL_QUADS);
				glTexCoord2f(0f, 1f);
				glVertex2f(-0.1f, -0.1f);
				glTexCoord2f(0f, 0f);
				glVertex2f(-0.1f, 0.1f);
				glTexCoord2f(1f, 0f);
				glVertex2f(0.4f, 0.1f);
				glTexCoord2f(1f, 1f);
				glVertex2f(0.4f, -0.1f);
			glEnd();
			
			glPopMatrix();
		}
		
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
