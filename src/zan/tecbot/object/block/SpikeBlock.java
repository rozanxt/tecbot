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

import zan.game.object.Shape;

public class SpikeBlock extends Block {
	
	public SpikeBlock(int sx, int sy) {
		super(sx, sy);
		shape = new Shape();
		shape.addPoint(0f, 0f);
		shape.addPoint(0f, 0.5f);
		shape.addPoint(1f, 0.5f);
		shape.addPoint(1f, 0f);
		shape.fix();
		setSolid(false);
		setPowered(true);
	}
	
	public void render() {
		glDisable(GL_TEXTURE_2D);
		glPushMatrix();
		
		glTranslatef(pos.x, pos.y, 0f);
		glScalef(size, size, 0f);
		glRotatef(-angle, 0f, 0f, 1f);
		
		glColor4f(1f, 0f, 0f, 1f);
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
