package zan.game.object;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.util.vector.Vector2f;

public abstract class BaseObject {
	
	protected String name;
	
	public static final float gravity = 0.25f;
	
	protected Vector2f pos = new Vector2f();
	protected Vector2f vel = new Vector2f();
	protected Vector2f acc = new Vector2f();
	protected float dxcap, dycap;
	
	protected float angle;
	protected float size;
	
	protected Shape shape;
	
	protected boolean active;
	
	public BaseObject() {
		name = null;
		pos.set(0f, 0f);
		vel.set(0f, 0f);
		acc.set(0f, 0f);
		dxcap = 10f;
		dycap = 10f;
		angle = 0f;
		size = 1f;
		shape = null;
		active = false;
	}
	
	public void setName(String sn) {name = sn;}
	public String getName() {return name;}
	
	public void spawn() {active = true;}
	public void despawn() {active = false;}
	public boolean isActive() {return active;}
	
	public void setPos(float sx, float sy) {pos.set(sx, sy);}
	public void setX(float sx) {pos.setX(sx);}
	public void setY(float sy) {pos.setY(sy);}
	public float getX() {return pos.getX();}
	public float getY() {return pos.getY();}
	public Vector2f getPos() {return pos;}
	
	public void setVel(float sx, float sy) {vel.set(sx, sy);}
	public void setDX(float sx) {vel.setX(sx);}
	public void setDY(float sy) {vel.setY(sy);}
	public float getDX() {return vel.getX();}
	public float getDY() {return vel.getY();}
	public Vector2f getVel() {return vel;}
	
	public void setCap(float sx, float sy) {dxcap = sx; dycap = sy;}
	public float getDXCap() {return dxcap;}
	public float getDYCap() {return dycap;}
	
	public void applyForce(float sx, float sy) {acc.x += sx; acc.y += sy;}
	public void applyForceX(float sx) {acc.x += sx;}
	public void applyForceY(float sy) {acc.y += sy;}
	public void applyGravity() {acc.y -= gravity;}
	
	public void setAngle(float sa) {angle = sa;}
	public float getAngle() {return angle;}
	
	public void setSize(float ss) {size = ss;}
	public float getSize() {return size;}
	
	public void setShape(Shape ss) {shape = ss;}
	public Shape getShape() {return shape;}
	public ArrayList<Vector2f> getShapePoints() {
		return shape.getPoints(pos.x, pos.y, size, angle);
	}
	public Vector2f getSupportPoint(Vector2f dir) {
		ArrayList<Vector2f> points = getShapePoints();
		
		float bestProjection = -Float.MAX_VALUE;
		Vector2f bestPoint = null;
		
		for (int i=0;i<shape.getNumPoints();i++) {
			Vector2f v = new Vector2f(points.get(i).x, points.get(i).y);
			float projection = Vector2f.dot(v, dir);
			
			if (projection > bestProjection) {
				bestProjection = projection;
				bestPoint = v;
			}
		}
		
		return bestPoint;
	}
	public Vector2f getNearestPoint(Vector2f pnt) {
		ArrayList<Vector2f> points = getShapePoints();
		
		float nearest = Float.MAX_VALUE;
		Vector2f nearestPoint = null;
		
		for (int i=0;i<shape.getNumPoints();i++) {
			Vector2f v = Vector2f.sub(pnt, points.get(i), null);
			float distance = v.lengthSquared();
			
			if (distance < nearest) {
				nearest = distance;
				nearestPoint = points.get(i);
			}
		}
		
		return nearestPoint;
	}
	public Vector2f getGroundSupportPoint(Vector2f pnt) {
		ArrayList<Vector2f> points = getShapePoints();
		
		float nearest = Float.MAX_VALUE;
		float highest = -Float.MAX_VALUE;
		Vector2f nearestPoint = null;
		
		for (int i=0;i<shape.getNumPoints();i++) {
			float distance = Math.abs(pnt.x - points.get(i).x);
			float high = points.get(i).y;
			
			if (distance < nearest) {
				nearest = distance;
				highest = high;
				nearestPoint = points.get(i);
			} else if (distance == nearest) {
				if (high > highest) {
					nearest = distance;
					highest = high;
					nearestPoint = points.get(i);
				}
			}
		}
		
		return nearestPoint;
	}
	
	public boolean collide(BaseObject obj, Collision col) {return false;}
	public void correction() {}
	
	public void update() {
		vel.x += acc.x;
		vel.y += acc.y;
		
		if (Math.abs(vel.x) > dxcap) vel.x = vel.x*dxcap/Math.abs(vel.x);
		if (Math.abs(vel.y) > dycap) vel.y = vel.y*dycap/Math.abs(vel.y);
		
		pos.x += vel.x;
		pos.y += vel.y;
		
		acc.set(0f, 0f);
	}
	
	public void render() {
		glDisable(GL_TEXTURE_2D);
		
		glPushMatrix();
		
		glTranslatef(pos.x, pos.y, 0f);
		glScalef(size, size, 0f);
		glRotatef(-angle, 0f, 0f, 1f);
		
		glBegin(GL_LINE_LOOP);
			for (int i=0;i<shape.getNumPoints();i++) {
				Vector2f vertex = shape.getPoint(i);
				glVertex2f(vertex.x - 0.5f, vertex.y - 0.5f);
			}
		glEnd();
		
		/*glColor3f(1f, 0f, 0f);
		glBegin(GL_POLYGON);
			glVertex2f(-0.01f, 0.01f);
			glVertex2f(0.01f, 0.01f);
			glVertex2f(0.01f, -0.01f);
			glVertex2f(-0.01f, -0.01f);
		glEnd();
		glColor3f(1f, 1f, 1f);*/
		
		glPopMatrix();
		
		glEnable(GL_TEXTURE_2D);
	}
	
}
