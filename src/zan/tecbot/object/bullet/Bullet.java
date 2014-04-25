package zan.tecbot.object.bullet;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;

import zan.game.object.BaseObject;
import zan.game.object.Collision;
import zan.game.object.Pair;
import zan.tecbot.object.block.Block;
import zan.tecbot.object.block.DestroyAbleBlock;
import zan.tecbot.object.entity.BadBot;
import zan.tecbot.object.entity.BaseEntity;
import zan.tecbot.object.entity.Tecbot;

public class Bullet extends BaseObject {
	
	protected boolean hostile;
	protected boolean playerBullet;
	
	protected float speed;
	protected float damage;
	protected float range;
	protected float dist;
	
	public Bullet() {
		super();
		hostile = false;
		playerBullet = false;
		speed = 0f;
		damage = 0f;
		range = 0f;
		dist = 0f;
	}
	
	public void spawn() {
		super.spawn();
		hostile = true;
		dist = 0f;
	}
	
	public boolean isHostile() {return hostile;}
	public void setPlayerBullet(boolean pb) {playerBullet = pb;}
	public boolean isPlayerBullet() {return playerBullet;}
	public void setSpeed(float ss) {speed = ss;}
	public float getSpeed() {return speed;}
	public void setDamage(float sd) {damage = sd;}
	public float getDamage() {return damage;}
	public void setRange(float sr) {range = sr;}
	public float getRange() {return range;}
	
	protected void outOfRange() {despawn();}
	
	public boolean collide(BaseObject obj, Collision col) {
		if (hostile) {
			if (obj instanceof Block) {
				if (obj instanceof DestroyAbleBlock) {
					DestroyAbleBlock o = (DestroyAbleBlock)obj;
					o.inflictDamage(damage);
				}
			} else if (obj instanceof BaseEntity) {
				if (playerBullet) {
					if (obj instanceof Tecbot) return false;
					else if (obj instanceof BadBot) {
						BadBot o = (BadBot)obj;
						o.inflictDamage(damage);
					}
				} else {
					if (obj instanceof BadBot) return false;
					else if (obj instanceof Tecbot) {
						Tecbot o = (Tecbot)obj;
						o.inflictDamage(damage);
					}
				}
			}
			Vector2f norm = new Vector2f();
			vel.normalise(norm);
			setX(getX()-norm.x*(1f-col.distance));
			setY(getY()-norm.y*(1f-col.distance));
			setVel(0f, 0f);
			hostile = false;
			dist = 0f;
			return true;
		}
		return false;
	}
	
	public void ObjectsInRange(ArrayList<Pair> pairs, ArrayList<BaseObject> objects) {
		if (isHostile()) {
			ArrayList<BaseObject> inrange = new ArrayList<BaseObject>();
			ArrayList<Float> distinrange = new ArrayList<Float>();
			for (int i=0;i<objects.size();i++) {
				Vector2f dist = Vector2f.sub(getPos(), objects.get(i).getPos(), null);
				if (dist.lengthSquared() < 2500f) {
					if (objects.get(i) instanceof BaseEntity) {
						BaseEntity o = (BaseEntity)objects.get(i);
						if (!o.isAlive()) continue;
					}
					/*if (objects.get(i) instanceof Block) {
						Block o = (Block)objects.get(i);
						o.highlight();
					}*/
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
	
	public void update() {
		super.update();
		dist += speed;
		if (hostile && dist > range) outOfRange();
	}
	
}
