package zan.tecbot.object;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.util.vector.Vector2f;

import zan.game.object.BaseObject;
import zan.game.object.Collision;
import zan.game.object.Shape;

public class Block extends BaseObject {
	
	private int type;
	private boolean color;
	
	public Block(int st) {
		super();
		setName("block");
		type = st;
		color = false;
		shape = new Shape();
		if (type == 1) {
			shape.addPoint(0f, 0f);
			shape.addPoint(1f, 1f);
			shape.addPoint(1f, 0f);
		} else if (type == 2) {
			shape.addPoint(0f, 0f);
			shape.addPoint(0f, 1f);
			shape.addPoint(1f, 0f);
		} else if (type == 3) {
			shape.addPoint(0f, 0f);
			shape.addPoint(0f, 1f);
			shape.addPoint(1f, 1f);
		} else if (type == 4) {
			shape.addPoint(0f, 1f);
			shape.addPoint(1f, 1f);
			shape.addPoint(1f, 0f);
		} else if (type == 5) {
			shape.addPoint(0f, 0f);
			shape.addPoint(0f, 1.2f);
			shape.addPoint(1f, 1.2f);
			shape.addPoint(1f, 0f);
		} else {
			shape.addPoint(0f, 0f);
			shape.addPoint(0f, 1f);
			shape.addPoint(1f, 1f);
			shape.addPoint(1f, 0f);
		}
		shape.fix();
	}
	
	public void color() {color = true;}
	
	public void collide(BaseObject obj, Collision col) {
		if (type == 5) {
			 if (obj instanceof BaseEntity) {
				Vector2f norm = col.normal;
				Vector2f negnorm = new Vector2f();
				norm.negate(negnorm);
				
				if (col.normFriction() && negnorm.y > 0f && obj.getSupportPoint(negnorm).y >= getSupportPoint(norm).y && obj.getDY() < 0f) {
			 		BaseEntity entity = (BaseEntity) obj;
			 		entity.bump();
				}
			 }
		}
	}
	
	public void update() {
		super.update();
		color = false;
	}
	
	public void render() {
		glDisable(GL_TEXTURE_2D);
		glPushMatrix();
		
		glTranslatef(pos.x, pos.y, 0f);
		glScalef(size, size, 0f);
		glRotatef(-angle, 0f, 0f, 1f);
		
		if (color) glColor4f(0f, 0f, 1f, 1f);
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
