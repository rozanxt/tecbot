package zan.tecbot.object;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;

import zan.game.input.InputManager;
import zan.game.object.BaseObject;
import zan.game.object.Collision;
import zan.game.object.Shape;
import zan.game.sprite.AnimatedSprite;
import zan.game.sprite.ISprite;
import zan.game.sprite.Sprite;
import zan.game.sprite.SpriteManager;

public class Tecbot extends BaseObject {
	
	protected ISprite[] sprite;
	
	protected boolean onground;
	protected boolean ground;
	protected boolean onmoving;
	protected boolean moving;
	protected int facing;
	
	public Tecbot() {
		super();
		setName("tecbot");
		shape = new Shape();
		shape.addPoint(0.31f, 0.15f);
		shape.addPoint(0.31f, 0.8f);
		shape.addPoint(0.69f, 0.8f);
		shape.addPoint(0.69f, 0.15f);
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
	}
	
	public void collide(BaseObject obj, Collision col) {
		Vector2f norm = col.normal;
		Vector2f negnorm = new Vector2f();
		norm.negate(negnorm);
		
		if (col.normFriction() && norm.y > 0f && getSupportPoint(norm).y >= obj.getSupportPoint(negnorm).y && getDY() < 0f) ground = true;
		if (norm.y < 0f && getSupportPoint(norm).y <= obj.getSupportPoint(negnorm).y && getDY() > 0f) setDY(0f);
		
		// QUICK FIX
		if (!col.normFriction()) {
			/*if (obj.getName() == "block") { 
				if (getSupportPoint(new Vector2f(0f, -1f)).y >= obj.getSupportPoint(new Vector2f(0f, 1f)).y-10f) {
					setY(getY()+norm.y*(1f-col.distance));
				} else {
					setX(getX()+norm.x*(1f-col.distance));
				}
			} else if (obj.getName() == "slope") {*/
				Vector2f foot = getSupportPoint(new Vector2f(0f, -1f));
				if (foot.y >= obj.getGroundSupportPoint(foot).y-15f) {
					setY(getY()+norm.y*(1f-col.distance));
				} else {
					setX(getX()+norm.x*(1f-col.distance));
				}
			//}
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
			if (InputManager.isKeyDown(Keyboard.KEY_D)) {
				applyForceX(0.5f); moving = true; facing = 0;
			}
			if (InputManager.isKeyDown(Keyboard.KEY_A)) {
				applyForceX(-0.5f); moving = true; facing = 1;
				
			}
		} else {
			if (InputManager.isKeyDown(Keyboard.KEY_D)) {applyForceX(0.1f); facing = 0;}
			if (InputManager.isKeyDown(Keyboard.KEY_A)) {applyForceX(-0.1f); facing = 1;}
			applyForce(0f, -0.25f);
		}
		
		if (onmoving && !moving) anim.setCurFrame(0);
		onmoving = moving;
		ground = false;
		super.update();
		anim.update();
	}
	
	public void render() {
		if (moving) sprite[1].render(getX(), getY(), getSize(), 0f, facing, 1f);
		else sprite[0].render(getX(), getY(), getSize(), 0f, facing, 1f);
		glColor4f(1f, 0f, 1f, 1f);
		super.render();
		glColor4f(1f, 1f, 1f, 1f);
	}
	
}
