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
	
	protected GridMap gridMap;
	
	protected float theta;
	protected float anchorDX, anchorDY;
	
	public MovingPlatform(int sx, int sy, GridMap gm) {
		super(sx, sy);
		gridMap = gm;
		shape = new Shape();
		shape.addPoint(-1f, 0f);
		shape.addPoint(-1f, 1f);
		shape.addPoint(2f, 1f);
		shape.addPoint(2f, 0f);
		shape.fix();
		setSolid(true);
		setPowered(true);
		theta = 0f;
		anchorDX = anchorDY = 0f;
	}
	
	public void setTheta(float st) {theta = st;}
	public float getTheta() {return theta;}
	
	public float getAnchorDX() {return anchorDX;}
	public float getAnchorDY() {return anchorDY;}
	
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
		if (getType() == 0) {
			if (isPowered()) {
				float beforeX = (float)(GridMap.getGameX(getTileX())+Math.sin(theta*(Math.PI/180f))*3*GridMap.tileWidth);
				theta += 1f;
				if (theta >= 360f) theta -= 360f;
				else if (theta < 0f) theta += 360f;
				float afterX = (float)(GridMap.getGameX(getTileX())+Math.sin(theta*(Math.PI/180f))*3*GridMap.tileWidth);
				anchorDX = afterX-beforeX;
			}
			setX((float)(GridMap.getGameX(getTileX())+Math.sin(theta*(Math.PI/180f))*3*GridMap.tileWidth));
		} else if (getType() == 1) {
			if (isPowered()) {
				float beforeY = (float)(GridMap.getGameY(getTileY())+Math.sin(theta*(Math.PI/180f))*3*GridMap.tileHeight);
				theta += 1f;
				if (theta >= 360f) theta -= 360f;
				else if (theta < 0f) theta += 360f;
				float afterY = (float)(GridMap.getGameY(getTileY())+Math.sin(theta*(Math.PI/180f))*3*GridMap.tileHeight);
				anchorDY = afterY-beforeY;
			}
			setY((float)(GridMap.getGameY(getTileY())+Math.sin(theta*(Math.PI/180f))*3*GridMap.tileHeight));
		}
		int tx = GridMap.getTileX(getX());
		int ty = GridMap.getTileY(getY());
		gridMap.addTempSolid(tx, ty);
		gridMap.addTempSolid(tx+1, ty);
		gridMap.addTempSolid(tx-1, ty);
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
			glVertex2f(-0.25f, -0.25f);
			glVertex2f(-0.25f, 0.25f);
			glVertex2f(0.25f, 0.25f);
			glVertex2f(0.25f, -0.25f);
		glEnd();
		glColor4f(1f, 1f, 1f, 1f);
		
		glPopMatrix();
		glEnable(GL_TEXTURE_2D);
	}
	
}
