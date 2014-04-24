package zan.tecbot.object.block;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.util.vector.Vector2f;

import zan.game.object.BaseObject;

public abstract class Block extends BaseObject {
	
	protected int tileX, tileY;
	protected boolean highlight;
	
	public Block(int sx, int sy) {
		super();
		tileX = sx;
		tileY = sy;
		highlight = false;
	}
	
	public void setTilePos(int sx, int sy) {tileX = sx; tileY = sy;}
	public void setTileX(int sx) {tileX = sx;}
	public void setTileY(int sy) {tileY = sy;}
	public int getTileX() {return tileX;}
	public int getTileY() {return tileY;}
	
	public void highlight() {highlight = true;}
	
	public void update() {
		super.update();
		highlight = false;
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
		
		glPopMatrix();
		glEnable(GL_TEXTURE_2D);
	}
	
}