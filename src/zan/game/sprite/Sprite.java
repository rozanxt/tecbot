package zan.game.sprite;

import static org.lwjgl.opengl.GL11.*;

/** Sprite class */
public class Sprite implements ISprite {
	
	/** Texture ID */
	private int textureID;
	
	/** Texture dimensions */
	private int textureWidth, textureHeight;
	
	/** Texture absolute coordinates */
	private int textureX0, textureY0, textureX1, textureY1;
	
	/** Constructor */
	public Sprite(int tid, int tw, int th, int tcx0, int tcy0, int tcx1, int tcy1) {
		textureID = tid;
		textureWidth = tw; textureHeight = th;
		textureX0 = tcx0; textureY0 = tcy0;
		textureX1 = tcx1; textureY1 = tcy1;
	}
	
	/** @return Texture ID */
	public int getTextureID() {return textureID;}
	
	/** @return Sprite dimensions */
	public int getSpriteWidth() {return textureX1 - textureX0;}
	public int getSpriteHeight() {return textureY1 - textureY0;}
	
	/** @return Sprite width to height ratio */
	public float getSpriteRatio() {return (float)getSpriteWidth() / (float)getSpriteHeight();}
	
	/** @return Texture relative coordinates */
	private float getTexCoordX0() {return (float)textureX0 / (float)textureWidth;}
	private float getTexCoordY0() {return (float)textureY0 / (float)textureHeight;}
	private float getTexCoordX1() {return (float)textureX1 / (float)textureWidth;}
	private float getTexCoordY1() {return (float)textureY1 / (float)textureHeight;}
	
	/** 
	 * Render basic sprite
	 * @param sx
	 * @param sy
	 * @param size
	 */
	public void render(float sx, float sy, float size) {
		render(sx, sy, size, 0f, 0, 1f);
	}
	
	/**
	 * Render raw sprite
	 * @param sx
	 * @param sy
	 * @param size
	 * @param rot
	 * @param flip
	 * @param alpha
	 */
	public void render(float sx, float sy, float size, float rot, int flip, float alpha) {
		float x0 = -0.5f;
		float y0 = -0.5f;
		float x1 = 0.5f;
		float y1 = 0.5f;
		
		if (flip == 1 || flip == 3) {
			x0 = 0.5f;
			x1 = -0.5f;
		}
		if (flip == 2 || flip == 3) {
			y0 = 0.5f;
			y1 = -0.5f;
		}
		
		if (getSpriteRatio() > 1f) {
			y0 /= getSpriteRatio();
			y1 /= getSpriteRatio();
		} else if (getSpriteRatio() < 1f) {
			x0 *= getSpriteRatio();
			x1 *= getSpriteRatio();
		}
		
		glPushMatrix();
		
		glTranslatef(sx, sy, 0f);
		glScalef(size, size, 0f);
		glRotatef(rot, 0f, 0f, 1f);
		
		glColor4f(1f, 1f, 1f, alpha);
		glBindTexture(GL_TEXTURE_2D, textureID);
		
		glBegin(GL_QUADS);
			glTexCoord2f(getTexCoordX0(), getTexCoordY1());
			glVertex2f(x0, y0);
			glTexCoord2f(getTexCoordX1(), getTexCoordY1());
			glVertex2f(x1, y0);
			glTexCoord2f(getTexCoordX1(), getTexCoordY0());
			glVertex2f(x1, y1);
			glTexCoord2f(getTexCoordX0(), getTexCoordY0());
			glVertex2f(x0, y1);
		glEnd();
		
		glColor4f(1f, 1f, 1f, 1f);
		glPopMatrix();
	}
		
}
