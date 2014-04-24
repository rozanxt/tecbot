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
	
	public void inflictDamage(float dmg) {health -= dmg;}
	public void setHealth(float sh) {health = sh;}
	public void setMaxHealth(float mh) {maxHealth = mh;}
	public float getHealth() {return health;}
	public float getMaxHealth() {return maxHealth;}
	
	public void BlocksInRange(ArrayList<Pair> pairs, ArrayList<Block> blocks) {
		if (isAlive()) {
			ArrayList<Block> inrange = new ArrayList<Block>();
			ArrayList<Float> distinrange = new ArrayList<Float>();
			for (int i=0;i<blocks.size();i++) {
				Vector2f dist = Vector2f.sub(getPos(), blocks.get(i).getPos(), null);
				if (dist.lengthSquared() < 10000f) {
					//blocks.get(i).highlight();
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
	}
	
	public void EntitiesInRange(ArrayList<Pair> pairs, ArrayList<BaseEntity> entities) {
		ArrayList<BaseEntity> inrange = new ArrayList<BaseEntity>();
		ArrayList<Float> distinrange = new ArrayList<Float>();
		for (int i=0;i<entities.size();i++) {
			Vector2f dist = Vector2f.sub(getPos(), entities.get(i).getPos(), null);
			if (entities.get(i).isAlive() && dist.lengthSquared() < 10000f) {
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
		if (isAlive() && health <= 0f) {
			setAlive(false);
			health = 0f;
			setDY(0f);
			applyForceY(5f);
		}
		super.update();
	}
	
}
