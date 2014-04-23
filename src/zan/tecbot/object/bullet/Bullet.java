package zan.tecbot.object.bullet;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;

import zan.game.object.BaseObject;
import zan.game.object.Collision;
import zan.game.object.Pair;
import zan.tecbot.object.block.Block;
import zan.tecbot.object.entity.BadBot;
import zan.tecbot.object.entity.BaseEntity;

public class Bullet extends BaseObject {
	
	protected boolean hostile;
	protected boolean playerBullet;
	
	protected float speed;
	protected float damage;
	protected float range;
	protected float dist;
	
	public Bullet() {
		super();
		setName("bullet");
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
	
	public void setPlayerBullet(boolean pb) {playerBullet = pb;}
	public void setSpeed(float ss) {speed = ss;}
	public float getSpeed() {return speed;}
	public void setDamage(float sd) {damage = sd;}
	public float getDamage() {return damage;}
	public void setRange(float sr) {range = sr;}
	public float getRange() {return range;}
	
	public boolean collide(BaseObject obj, Collision col) {
		if (hostile) {
			if (obj instanceof BadBot) {
				BadBot entity = (BadBot)obj;
				if (entity.isAlive()) {
					entity.inflictDamage(damage);
				} else return false;
			}
			Vector2f norm = new Vector2f();
			vel.normalise(norm);
			setX(getX()-norm.x*(1f-col.distance));
			setY(getY()-norm.y*(1f-col.distance));
			setVel(0f, 0f);
			hostile = false;
			dist = 0;
			return true;
		}
		return false;
	}
	
	public void BlocksInRange(ArrayList<Pair> pairs, ArrayList<Block> blocks) {
		ArrayList<Block> inrange = new ArrayList<Block>();
		ArrayList<Float> distinrange = new ArrayList<Float>();
		for (int i=0;i<blocks.size();i++) {
			Vector2f dist = Vector2f.sub(getPos(), blocks.get(i).getPos(), null);
			if (dist.lengthSquared() < 2500f) {
				blocks.get(i).highlight();
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
	
	public void update() {
		super.update();
		dist += vel.x*vel.x+vel.y*vel.y;
		if (hostile && dist > range) despawn();
	}
	
}
