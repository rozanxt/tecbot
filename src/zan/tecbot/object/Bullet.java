package zan.tecbot.object;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.util.vector.Vector2f;

import zan.game.object.BaseObject;
import zan.game.object.Shape;
import zan.game.util.GameUtility;

public class Bullet extends BaseObject {
	
	protected int time;
	
	protected float alpha;
	
	public Bullet() {
		super();
		setName("bullet");
		shape = new Shape();
		shape.addPoint(0f, 0.4f);
		shape.addPoint(0f, 0.6f);
		shape.addPoint(1f, 0.6f);
		shape.addPoint(1f, 0.4f);
		shape.fix();
		time = 0;
		alpha = 1f;
	}
	
	public void spawn() {
		super.spawn();
		time = 100;
		alpha = 0.2f+GameUtility.getRnd().nextInt(8)*0.1f;
	}
	
	public void update() {
		super.update();
		if (time > 0) time --;
		else despawn();
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
