package zan.tecbot.object;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;

import zan.game.object.BaseObject;
import zan.game.object.Collision;
import zan.game.object.Pair;
import zan.game.object.Shape;
import zan.game.util.GameUtility;

public class Bullet extends BaseObject {
	
	protected int type;
	
	protected boolean hostile;
	protected int time;
	
	protected float alpha;
	
	public Bullet(int st) {
		super();
		setName("bullet");
		type = st;
		hostile = false;
		time = 0;
		alpha = 1f;
		shape = new Shape();
		if (type == 0) {
			shape.addPoint(0f, 0.4f);
			shape.addPoint(0f, 0.6f);
			shape.addPoint(1f, 0.6f);
			shape.addPoint(1f, 0.4f);
		} else if (type == 1) {
			for (int i=0;i<12;i++) {
				float angle = (float)(-i*30f*Math.PI/180f);
				shape.addPoint((float)(0.5f+0.5f*Math.cos(angle)), (float)(0.5f+0.5f*Math.sin(angle)));
			}
		} else {
			shape.addPoint(0f, 0f);
			shape.addPoint(0f, 1f);
			shape.addPoint(1f, 1f);
			shape.addPoint(1f, 0f);
		}
		shape.fix();
	}
	
	public void spawn() {
		super.spawn();
		hostile = true;
		if (type == 0) {
			time = 50;
			alpha = 0.2f+GameUtility.getRnd().nextInt(8)*0.1f;
		} else if (type == 1) {
			time = 100;
		}
	}
	
	public void BlocksInRange(ArrayList<Pair> pairs, ArrayList<Block> blocks) {
		ArrayList<Block> inrange = new ArrayList<Block>();
		ArrayList<Float> distinrange = new ArrayList<Float>();
		for (int i=0;i<blocks.size();i++) {
			Vector2f dist = Vector2f.sub(getPos(), blocks.get(i).getPos(), null);
			if (dist.lengthSquared() < 2500f) {
				blocks.get(i).color();
				inrange.add(blocks.get(i));
				distinrange.add(dist.length());
			}
		}
		while (distinrange.size() > 0) {
			float bestDist = Float.MAX_VALUE;
			int next = 0;
			for (int i=0;i<distinrange.size();i++) {
				if (distinrange.get(i) < bestDist) {
					bestDist = distinrange.get(i);
					next = i;
				}
			}
			pairs.add(new Pair(this, inrange.get(next)));
			inrange.remove(next);
			distinrange.remove(next);
		}
	}
	public void EntitiesInRange(ArrayList<Pair> pairs, ArrayList<BaseEntity> entities) {
		ArrayList<BaseEntity> inrange = new ArrayList<BaseEntity>();
		ArrayList<Float> distinrange = new ArrayList<Float>();
		for (int i=0;i<entities.size();i++) {
			Vector2f dist = Vector2f.sub(getPos(), entities.get(i).getPos(), null);
			if (dist.lengthSquared() < 2500f) {
				inrange.add(entities.get(i));
				distinrange.add(dist.length());
			}
		}
		while (distinrange.size() > 0) {
			float bestDist = Float.MAX_VALUE;
			int next = 0;
			for (int i=0;i<distinrange.size();i++) {
				if (distinrange.get(i) < bestDist) {
					bestDist = distinrange.get(i);
					next = i;
				}
			}
			pairs.add(new Pair(this, inrange.get(next)));
			inrange.remove(next);
			distinrange.remove(next);
		}
	}
	
	public void collide(BaseObject obj, Collision col) {
		if (hostile) {
			hostile = false;
			time = 20;
			Vector2f norm = new Vector2f();
			vel.normalise(norm);
			setX(getX()-norm.x*(1f-col.distance));
			setY(getY()-norm.y*(1f-col.distance));
			setVel(0f, 0f);
		} 
	}
	
	public void update() {
		super.update();
		if (!hostile) {
			if (type == 1) {
				setSize(40f-time);
				alpha = time/20f;
			}
		}
		if (time > 0) time --;
		else despawn();
	}
	
	public void render() {
		glDisable(GL_TEXTURE_2D);
		glPushMatrix();
		
		glTranslatef(pos.x, pos.y, 0f);
		glScalef(size, size, 0f);
		glRotatef(-angle, 0f, 0f, 1f);
		
		if (type == 0) glColor4f(alpha, alpha, 0f, 1f);
		else if (type == 1) glColor4f(0f, 1f, 1f, alpha);
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
