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
import zan.tecbot.object.entity.BaseEntity;

public class MovingPlatform extends Block {
	
	protected float theta;
	protected float anchorDX;
	
	public MovingPlatform(int sx, int sy) {
		super(sx, sy);
		shape = new Shape();
		shape.addPoint(0f, 1f/3f);
		shape.addPoint(0f, 2f/3f);
		shape.addPoint(1f, 2f/3f);
		shape.addPoint(1f, 1f/3f);
		shape.fix();
		setSolid(true);
		setPowered(true);
		theta = 0f;
	}
	
	public float getAnchorDX() {return anchorDX;}
	
	public boolean collide(BaseObject obj, Collision col) {
		if (isPowered() && obj instanceof BaseEntity) {
			Vector2f norm = col.normal;
			Vector2f negnorm = new Vector2f();
			norm.negate(negnorm);
			
			if (col.normFriction() && negnorm.y > 0f && obj.getSupportPoint(negnorm).y >= getSupportPoint(norm).y && obj.getDY() < 0f) {
		 		BaseEntity entity = (BaseEntity) obj;
		 		entity.setAnchor(this);
		 		return true;
			}
		}
		return false;
	}
	
	public void update() {
		if (isPowered()) {
			float beforeX = (float)(GridMap.getGameX(getTileX())+Math.sin(theta*(Math.PI/180f))*3*GridMap.tileWidth);
			theta += 1f;
			if (theta >= 360f) theta -= 360f;
			else if (theta < 0f) theta += 360f;
			float afterX = (float)(GridMap.getGameX(getTileX())+Math.sin(theta*(Math.PI/180f))*3*GridMap.tileWidth);
			anchorDX = beforeX - afterX;
			setX((float)(GridMap.getGameX(getTileX())+Math.sin(theta*(Math.PI/180f))*3*GridMap.tileWidth));
		}
		super.update();
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
			glVertex2f(-1f/12f, -1f/12f);
			glVertex2f(-1f/12f, 1f/12f);
			glVertex2f(1f/12f, 1f/12f);
			glVertex2f(1f/12f, -1f/12f);
		glEnd();
		glColor4f(1f, 1f, 1f, 1f);
		
		glPopMatrix();
		glEnable(GL_TEXTURE_2D);
	}
	
}
