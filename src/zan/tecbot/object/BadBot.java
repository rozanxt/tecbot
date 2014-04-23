package zan.tecbot.object;

import zan.game.object.Shape;
import zan.game.sprite.AnimatedSprite;
import zan.game.sprite.ISprite;
import zan.game.sprite.Sprite;
import zan.game.sprite.SpriteManager;
import zan.game.util.GameUtility;

public class BadBot extends BaseEntity {
	
	protected ISprite[] sprite;
	
	public BadBot() {
		super();
		setName("badbot");
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
		
		setCap(2.5f, 10f);
	}
	
	public void update() {
		AnimatedSprite anim = (AnimatedSprite) sprite[1];
		moving = false;
		
		if (ground) {
			setDY(-5f);
			if (GameUtility.getRnd().nextInt(100) == 64) {applyForceY(7f); setDY(0f); ground = false; onground = false;}
			if (facing == 1) {applyForceX(-0.5f); moving = true;}
			else if (facing == 0) {applyForceX(0.5f); moving = true;}
		} else {
			applyForceY(-0.25f);
			if (facing == 1) {applyForceX(-0.1f);}
			else if (facing == 0) {applyForceX(0.1f);}
		}
		
		if (GameUtility.getRnd().nextInt(100) == 32) {
			if (facing == 0) facing = 1;
			else if (facing == 1) facing = 0;
		}
		
		if (onmoving && !moving) anim.setCurFrame(0);
		onmoving = moving;
		ground = false;
		super.update();
		anim.update();
	}
	
	public void render() {
		if (moving) sprite[1].render(getX(), getY(), getSize(), 0f, facing, 0.8f);
		else sprite[0].render(getX(), getY(), getSize(), 0f, facing, 0.8f);
		
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