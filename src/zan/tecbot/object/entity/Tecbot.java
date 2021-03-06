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
	
	protected int flpani;
	protected float flpcnt;
	
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
		setJumpPower(7f);
		setCap(4f, 10f);
		flpani = 0;
		flpcnt = 0f;
		invulnerable = 0;
		gunangle = 0f;
	}
	
	public void spawn() {
		super.spawn();
		invulnerable = 50;
	}
	
	public float getGunAngle() {return gunangle;}
	
	public void fullyLoadedPlasma(float ss) {flpcnt = ss;}
	
	public boolean collide(BaseObject obj, Collision col) {
		if (super.collide(obj, col)) {
			if (invulnerable == 0 && obj instanceof BadBot) {
				BadBot entity = (BadBot)obj;
				Vector2f norm = col.normal;
				Vector2f negnorm = new Vector2f();
				norm.negate(negnorm);
				setDY(0f);
				applyForce(norm.x*10f, 4f);
				ground = false;
				onground = false;
				if (entity.isStompAble() && col.normFriction() && norm.y > 0f) {
					entity.stomp();
					return true;
				}
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
			
			if (ground) {
				setDY(-5f);
				if (InputManager.isKeyDown(Keyboard.KEY_W)) {jump();}
				if (InputManager.isKeyDown(Keyboard.KEY_D)) {moveRight();}
				if (InputManager.isKeyDown(Keyboard.KEY_A)) {moveLeft();}
				if (InputManager.isKeyPressed(Keyboard.KEY_S)) {passBottom();}
			} else {
				applyGravity();
				if (InputManager.isKeyDown(Keyboard.KEY_D)) {airRight();}
				if (InputManager.isKeyDown(Keyboard.KEY_A)) {airLeft();}
			}
			
			if (onmoving && !moving) anim.setCurFrame(0);
			onmoving = moving;
			angle = 0f;
		} else {
			applyGravity();
			angle = 30f;
		}
		if (invulnerable > 0) invulnerable--;
		ground = false;
		super.update();
		anim.update();
		
		if (flpcnt > 0f) {
			flpani++;
			if (flpani >= 20) flpani = 0;
		}
	}
	
	public void render() {
		if (isAlive() && flpcnt > 0f) {
			glDisable(GL_TEXTURE_2D);
			glPushMatrix();
			
			glTranslatef(pos.x, pos.y, 0f);
			glScalef(flpcnt*0.4f, flpcnt*0.4f, 0f);
			glRotatef(flpani*18f, 0f, 0f, 1f);
			
			glColor4f(1f, 0f, 0f, 1f);
			glBegin(GL_LINE_LOOP);
				glVertex2f(-1f, -1f);
				glVertex2f(-1f, 1f);
				glVertex2f(1f, 1f);
				glVertex2f(1f, -1f);
			glEnd();
			glColor4f(1f, 1f, 1f, 1f);
			
			glPopMatrix();
			glEnable(GL_TEXTURE_2D);
		}
		
		if (isAlive()) {
			if (invulnerable > 0) {
				if (ground && moving) sprite[1].render(getX(), getY(), getSize(), angle, facing, 0.75f);
				else sprite[0].render(getX(), getY(), getSize(), angle, facing, 0.75f);
			} else {
				if (ground && moving) sprite[1].render(getX(), getY(), getSize(), angle, facing, 1f);
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
