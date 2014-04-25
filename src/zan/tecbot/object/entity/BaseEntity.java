package zan.tecbot.object.entity;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;

import zan.game.object.BaseObject;
import zan.game.object.Collision;
import zan.game.object.Pair;
import zan.tecbot.object.block.Block;

public abstract class BaseEntity extends BaseObject {
	
	protected boolean alive;
	protected float health;
	protected float maxHealth;
	
	protected boolean ground;
	protected boolean onground;
	protected boolean moving;
	protected boolean onmoving;
	protected int facing;
	
	public BaseEntity() {
		super();
		alive = false;
		health = 0f;
		maxHealth = 0f;
		onground = false;
		ground = false;
		onmoving = false;
		moving = false;
		facing = 0;
	}
	
	public void spawn() {
		super.spawn();
		alive = true;
		health = maxHealth;
	}
	
	public void setAlive(boolean sa) {alive = sa;}
	public boolean isAlive() {return alive;}
	
	public void healDamage(float sh) {
		health += sh;
		if (health > maxHealth) health = maxHealth;
	}
	public void inflictDamage(float sd) {
		health -= sd;
		if (health < 0f) health = 0f;
	}
	public void setHealth(float sh) {
		health = sh;
		if (health > maxHealth) health = maxHealth;
		if (health < 0f) health = 0f;
	}
	public void setMaxHealth(float mh) {maxHealth = mh;}
	public float getHealth() {return health;}
	public float getMaxHealth() {return maxHealth;}
	
	public void ObjectsInRange(ArrayList<Pair> pairs, ArrayList<BaseObject> objects) {
		if (isAlive()) {
			ArrayList<BaseObject> inrange = new ArrayList<BaseObject>();
			ArrayList<Float> distinrange = new ArrayList<Float>();
			for (int i=0;i<objects.size();i++) {
				Vector2f dist = Vector2f.sub(getPos(), objects.get(i).getPos(), null);
				if (dist.lengthSquared() < 10000f) {
					if (objects.get(i) instanceof BaseEntity) {
						BaseEntity o = (BaseEntity)objects.get(i);
						if (!o.isAlive()) continue;
					}
					if (objects.get(i) instanceof Block) {
						Block o = (Block)objects.get(i);
						o.highlight();
					}
					inrange.add(objects.get(i));
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
			inrange.clear();
			distinrange.clear();
		}
		objects.clear();
	}
	
	public boolean collide(BaseObject obj, Collision col) {
		if (isAlive()) {
			if (obj instanceof Block) {
				Vector2f norm = col.normal;
				Vector2f negnorm = new Vector2f();
				norm.negate(negnorm);
				
				if (col.normFriction() && norm.y > 0f && getSupportPoint(norm).y >= obj.getSupportPoint(negnorm).y && getDY() < 0f) ground = true;
				if (norm.y < 0f && getSupportPoint(norm).y <= obj.getSupportPoint(negnorm).y && getDY() > 0f) setDY(0f);
				
				// QUICK FIX
				if (!col.normFriction()) {
					Vector2f foot = getSupportPoint(new Vector2f(0f, -1f));
					if (foot.y >= obj.getGroundSupportPoint(foot).y-15f) {
						setY(getY()+norm.y*(1f-col.distance));
					} else {
						setX(getX()+norm.x*(1f-col.distance));
					}
				}
				if (col.normFriction()) setY(getY()+norm.y*(1f-col.distance));
				
				if (!moving) setDX(getDX()*0.7f);
			}
			return true;
		}
		return false;
	}
	
	public void correction() {
		if (isAlive()) {
			if (onground && !ground) {setY(getY()-getDY()); setDY(0f);}
			onground = ground;
		}
	}
	
	public void bump() {
		if (isAlive()) {
			applyForceY(9f);
			setDY(0f);
			ground = false;
			onground = false;
		}
	}
	
	public void update() {
		if (isAlive()) {
			if (health <= 0f) {
				setAlive(false);
				health = 0f;
				setDY(0f);
				applyForceY(5f);
			}
		}
		if (getY() < -1000f) {
			if (isAlive()) inflictDamage(3f);
			else despawn();
		}
		super.update();
	}
	
}
