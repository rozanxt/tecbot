package zan.tecbot.object.block;

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
import zan.tecbot.mechanism.GridMap;
import zan.tecbot.object.bullet.PlasmaBullet;

public class EnergySwitch extends SwitchBlock {
	
	public EnergySwitch(int sx, int sy, GridMap gm) {
		super(sx, sy, gm);
		shape = new Shape();
		shape.addPoint(0f, 0f);
		shape.addPoint(0f, 1f);
		shape.addPoint(1f, 1f);
		shape.addPoint(1f, 0f);
		shape.fix();
		setSolid(true);
		setSwitchAble(true);
	}
	
	public boolean collide(BaseObject obj, Collision col) {
		if (obj instanceof PlasmaBullet) {
			PlasmaBullet o = (PlasmaBullet)obj;
			if (playerOnly && !o.isPlayerBullet()) return false;
			if (getType() == 0 && !o.isFullyLoaded()) return false;
			switchPower();
			return true;
		}
		return false;
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
