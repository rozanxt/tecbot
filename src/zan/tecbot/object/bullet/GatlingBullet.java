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
import zan.game.util.GameUtility;

public class GatlingBullet extends Bullet {
	
	protected int hitTime;
	protected float alpha;
	
	public GatlingBullet() {
		super();
		shape = new Shape();
		shape.addPoint(0f, 0.4f);
		shape.addPoint(0f, 0.6f);
		shape.addPoint(1f, 0.6f);
		shape.addPoint(1f, 0.4f);
		shape.fix();
		hitTime = 0;
		alpha = 1f;
		setSize(10f);
		setSpeed(20f);
		setDamage(1.5f);
		setRange(800f);
		setCap(getSpeed(), getSpeed());
	}
	
	public void spawn() {
		super.spawn();
		alpha = 0.2f+GameUtility.getRnd().nextInt(8)*0.1f;
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
		
		glColor4f(alpha, alpha, 0f, 1f);
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
