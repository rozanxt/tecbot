package zan.tecbot.object.bullet;

import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;

import org.lwjgl.util.vector.Vector2f;

import zan.game.object.BaseObject;
import zan.game.object.Collision;
import zan.game.object.Shape;

public class PlasmaBullet extends Bullet {
	
	protected int hitTime;
	protected float alpha;
	
	public PlasmaBullet() {
		super();
		shape = new Shape();
		for (int i=0;i<12;i++) {
			float angle = (float)(-i*30f*Math.PI/180f);
			shape.addPoint((float)(0.5f+0.5f*Math.cos(angle)), (float)(0.5f+0.5f*Math.sin(angle)));
		}
		shape.fix();
		hitTime = 0;
		alpha = 1f;
		setSize(20f);
		setSpeed(10f);
		setDamage(30f);
		setRange(800f);
		setCap(getSpeed(), getSpeed());
	}
	
	public boolean collide(BaseObject obj, Collision col) {
		if (super.collide(obj, col)) {
			hitTime = 20;
			return true;
		}
		return false;
	}
	
	public void update() {
		super.update();
		if (!hostile) {
			setSize(40f-hitTime);
			alpha = hitTime/20f;
			if (hitTime > 0) hitTime --;
			else despawn();
		}
	}
	
	public void render() {
		glDisable(GL_TEXTURE_2D);
		glPushMatrix();
		
		glTranslatef(pos.x, pos.y, 0f);
		glScalef(size, size, 0f);
		glRotatef(-angle, 0f, 0f, 1f);
		
		glColor4f(0f, 1f, 1f, alpha);
		glBegin(GL_LINE_LOOP);
			for (int i=0;i<shape.getNumPoints();i++) {
				Vector2f vertex = shape.getPoint(i);
				glVertex2f(vertex.x - 0.5f, vertex.y - 0.5f);
			}
		glEnd();
		glColor4f(1f, 1f, 1f, 1f);
		
		glPopMatrix();
		glEnable(GL_TEXTURE_2D);
	}
	
}
