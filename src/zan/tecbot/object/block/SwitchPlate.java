package zan.tecbot.object.block;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.util.vector.Vector2f;

import zan.game.object.BaseObject;
import zan.game.object.Collision;
import zan.game.object.Shape;
import zan.tecbot.mechanism.GridMap;
import zan.tecbot.object.entity.BaseEntity;
import zan.tecbot.object.entity.Tecbot;

public class SwitchPlate extends SwitchBlock {
	
	protected boolean onPlate;
	protected int wasOnPlate;
	
	public SwitchPlate(int sx, int sy, GridMap gm) {
		super(sx, sy, gm);
		shape = new Shape();
		shape.addPoint(0f, 0f);
		shape.addPoint(0f, 1.1f);
		shape.addPoint(1f, 1.1f);
		shape.addPoint(1f, 0f);
		shape.fix();
		setSolid(true);
		setSwitchAble(true);
		onPlate = false;
		wasOnPlate = 0;
	}
	
	public boolean collide(BaseObject obj, Collision col) {
		if (obj instanceof BaseEntity) {
			if (playerOnly && !(obj instanceof Tecbot)) return false;
			if (hitSide(obj, col) == side) {
		 		if (!onPlate) {
		 			switchPower();
		 			onPlate = true;
		 		}
		 		wasOnPlate = 20;
		 		return true;
			}
		}
		return false;
	}
	
	public void update() {
		super.update();
		if (wasOnPlate > 0) wasOnPlate--;
		if (onPlate && wasOnPlate == 0) onPlate = false;
	}
	
	public void render() {
		glDisable(GL_TEXTURE_2D);
		glPushMatrix();
		
		glTranslatef(pos.x, pos.y, 0f);
		glScalef(size, size, 0f);
		glRotatef(-angle, 0f, 0f, 1f);
		
		if (highlight) glColor4f(0f, 0f, 1f, 1f);
		glBegin(GL_LINE_LOOP);
			for (int i=0;i<shape.getNumPoints();i++) {
				Vector2f vertex = shape.getPoint(i);
				glVertex2f(vertex.x - 0.5f, vertex.y - 0.5f);
			}
		glEnd();
		glColor4f(1f, 1f, 1f, 1f);
		
		if (isPowered()) glColor4f(0f, 1f, 1f, 1f);
		else glColor4f(1f, 0f, 0f, 1f);
		glBegin(GL_LINE_LOOP);
			glVertex2f(-0.25f, 0f);
			glVertex2f(0f, 0.25f);
			glVertex2f(0.25f, 0f);
			glVertex2f(0f, -0.25f);
		glEnd();
		glColor4f(1f, 1f, 1f, 1f);
		
		glPopMatrix();
		glEnable(GL_TEXTURE_2D);
	}
	
}
