package zan.tecbot.object;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;

import zan.game.GameCore;
import zan.game.input.InputManager;
import zan.game.object.BaseObject;
import zan.game.object.Collision;
import zan.game.object.Pair;
import zan.game.object.Shape;
import zan.game.sprite.AnimatedSprite;
import zan.game.sprite.ISprite;
import zan.game.sprite.Sprite;
import zan.game.sprite.SpriteManager;

public class Tecbot extends BaseObject {
	
	protected ISprite[] sprite;
	
	protected boolean ground;
	protected boolean onground;
	protected boolean moving;
	protected boolean onmoving;
	protected int facing;
	
	public float gunangle;
	
	public Tecbot() {
		super();
		setName("tecbot");
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
		
		setCap(4f, 10f);
		onground = false;
		ground = false;
		onmoving = false;
		moving = false;
		facing = 0;
		gunangle = 0f;
	}
	
	public void BlocksInRange(ArrayList<Pair> pairs, ArrayList<Block> blocks) {
		ArrayList<Block> inrange = new ArrayList<Block>();
		ArrayList<Float> distinrange = new ArrayList<Float>();
		for (int i=0;i<blocks.size();i++) {
			Vector2f dist = Vector2f.sub(getPos(), blocks.get(i).getPos(), null);
			if (dist.lengthSquared() < 10000f) {
				blocks.get(i).color();
				inrange.add(blocks.get(i));
				distinrange.add(dist.length());
			}
		}
		while (distinrange.size() > 0) {
			float bestDist = Float.MAX_VALUE;
			int next = 0;
			for (int i=0;i<distinrange.size();i++) {
				if (distinrange.get(i) < bestDist) {
					bestDist = distinrange.get(i);
					next = i;
				}
			}
			pairs.add(new Pair(this, inrange.get(next)));
			inrange.remove(next);
			distinrange.remove(next);
		}
	}
	
	public void collide(BaseObject obj, Collision col) {
		Vector2f norm = col.normal;
		Vector2f negnorm = new Vector2f();
		norm.negate(negnorm);
		
		if (col.normFriction() && norm.y > 0f && getSupportPoint(norm).y >= obj.getSupportPoint(negnorm).y && getDY() < 0f) ground = true;
		if (norm.y < 0f && getSupportPoint(norm).y <= obj.getSupportPoint(negnorm).y && getDY() > 0f) setDY(0f);
		
		// QUICK FIX
		if (!col.normFriction()) {
			Vector2f foot = getSupportPoint(new Vector2f(0f, -1f));
			if (foot.y >= obj.getGroundSupportPoint(foot).y-15f) {
				setY(getY()+norm.y*(1f-col.distance));
			} else {
				setX(getX()+norm.x*(1f-col.distance));
			}
		}
		if (col.normFriction()) setY(getY()+norm.y*(1f-col.distance));
		
		if (!moving) setDX(getDX()*0.7f);
	}
	
	public void correction() {
		if (onground && !ground) {setY(getY()-getDY()); setDY(0f);}
		onground = ground;
	}
	
	public void update() {
		AnimatedSprite anim = (AnimatedSprite) sprite[1];
		moving = false;
		
		if (ground) {
			setDY(-5f);
			if (InputManager.isKeyDown(Keyboard.KEY_W)) {applyForceY(7f); setDY(0f); ground = false; onground = false;}
			if (InputManager.isKeyDown(Keyboard.KEY_D)) {applyForceX(0.5f); moving = true;}
			if (InputManager.isKeyDown(Keyboard.KEY_A)) {applyForceX(-0.5f); moving = true;}
		} else {
			applyForceY(-0.25f);
			if (InputManager.isKeyDown(Keyboard.KEY_D)) {applyForceX(0.1f);}
			if (InputManager.isKeyDown(Keyboard.KEY_A)) {applyForceX(-0.1f);}
		}
		
		if (onmoving && !moving) anim.setCurFrame(0);
		onmoving = moving;
		ground = false;
		super.update();
		anim.update();
		
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
	
	public void render() {
		if (moving) sprite[1].render(getX(), getY(), getSize(), 0f, facing, 1f);
		else sprite[0].render(getX(), getY(), getSize(), 0f, facing, 1f);
		
		/*glColor4f(1f, 0f, 1f, 1f);
		super.render();
		glColor4f(1f, 1f, 1f, 1f);*/
		
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
