package zan.tecbot.object.entity;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;

import zan.game.object.BaseObject;
import zan.game.object.Collision;
import zan.game.object.Pair;
import zan.tecbot.object.block.Block;
import zan.tecbot.object.block.MovingPlatform;
import zan.tecbot.object.block.SpikeBlock;

public abstract class BaseEntity extends BaseObject {
	
	protected boolean alive;
	protected float health;
	protected float maxHealth;
	protected float jumpPower;
	
	protected boolean ground;
	protected boolean onground;
	protected boolean moving;
	protected boolean onmoving;
	protected int facing;
	
	protected MovingPlatform anchor;
	protected float anchorX;
	protected boolean squashUp, squashDown, squashLeft, squashRight;
	protected int passBottom;
	
	public BaseEntity() {
		super();
		alive = false;
		health = 0f;
		maxHealth = 0f;
		jumpPower = 0f;
		ground = false;
		onground = false;
		moving = false;
		onmoving = false;
		facing = 0;
		anchor = null;
		anchorX = 0f;
		squashUp = squashDown = squashLeft = squashRight = false;
		passBottom = 0;
	}
	
	public void spawn() {
		super.spawn();
		alive = true;
		health = maxHealth;
		setVel(0f, 0f);
		setAnchor(null);
	}
	
	public void setAlive(boolean sa) {alive = sa;}
	public boolean isAlive() {return alive;}
	
	public boolean isOutOfBound() {
		if (getY() < -1000f) return true;
		return false;
	}
	
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
	public float getHealth() {return health;}
	public void setMaxHealth(float mh) {maxHealth = mh;}
	public float getMaxHealth() {return maxHealth;}
	public void setJumpPower(float sj) {jumpPower = sj;}
	
	public void setAnchor(MovingPlatform sa) {
		if (anchor != sa) {
			anchor = sa;
			if (anchor != null) anchorX = getX()-anchor.getX();
		}
	}
	
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
				Block b = (Block)obj;
				if (b.isSolid()) {
					if (b.isBottomPass()) {
						Vector2f norm = col.normal;
						Vector2f foot = getSupportPoint(new Vector2f(0f, -1f));
						
						if (passBottom == 0 && col.normFriction() && norm.y > 0f && foot.y >= obj.getGroundSupportPoint(foot).y-15f && getDY() < 0f) {
							ground = true;
							setY(getY()+norm.y*(1f-col.distance));
							if (!moving) setDX(getDX()*0.7f);
						}
						
						if (col.distance < -15f) {
							if (col.normFriction()) {
								if (norm.y > 0f) squashDown = true;
							}
						}
						
					} else {
						Vector2f norm = col.normal;
						
						if (col.normFriction() && norm.y > 0f && getDY() < 0f) ground = true;
						if (norm.y < 0f && getDY() > 0f) setDY(0f);
						
						if (col.normFriction()) setY(getY()+norm.y*(1f-col.distance));
						else {
							Vector2f foot = getSupportPoint(new Vector2f(0f, -1f));
							if (foot.y >= obj.getGroundSupportPoint(foot).y-15f) {
								setY(getY()+norm.y*(1f-col.distance));
							} else {
								setX(getX()+norm.x*(1f-col.distance));
							}
							if (anchor != null) {anchorX = getX()-anchor.getX();}
						}
						
						if (col.distance < -15f) {
							if (col.normFriction()) {
								if (norm.y > 0f) squashDown = true;
								else if(norm.y < 0f) squashUp = true;
							} else {
								if (norm.x > 0f) squashLeft = true;
								else if(norm.x < 0f) squashRight = true;
							}
						}
						
						if (!moving) setDX(getDX()*0.7f);
					}
				}
				if (obj instanceof SpikeBlock) {
					if (b.isPowered()) die();
				}
			}
			return true;
		}
		return false;
	}
	
	public void correction() {
		if (isAlive()) {
			if (onground && !ground) {setY(getY()-getDY()); setDY(0f);}
			onground = ground;
			if (anchor != null && !ground) {
				setDX(getDX()+anchor.getAnchorDX());
				setDY(getDY()+anchor.getAnchorDY());
				anchor = null;
			}
		}
	}
	
	public void stop() {moving = false;}
	public void moveRight() {applyForceX(0.5f);	moving = true;}
	public void moveLeft() {applyForceX(-0.5f);	moving = true;}
	public void airRight() {applyForceX(0.1f);}
	public void airLeft() {applyForceX(-0.1f);}
	public void passBottom() {passBottom = 20;}
	
	public void jump() {
		setDY(0f);
		applyForceY(jumpPower);
		ground = false;
		onground = false;
	}
	
	public void bump(int side) {
		if (isAlive()) {
			if (side == 1) applyForce(8f, 4f);
			else if (side == 2) applyForceY(-4f);
			else if (side == 3) applyForce(-8f, 4f);
			else applyForceY(9f);
			setDY(0f);
			ground = false;
			onground = false;
		}
	}
	
	public void die() {
		setAlive(false);
		health = 0f;
		setDY(0f);
		applyForceY(5f);
	}
	
	public void update() {
		if (isAlive()) {
			if (health <= 0f) die();
			if ((squashUp && squashDown) || (squashLeft && squashRight)) die();
			squashUp = squashDown = squashLeft = squashRight = false;
			if (passBottom > 0) passBottom--;
		}
		if (isOutOfBound()) {
			if (isAlive()) inflictDamage(3f);
			else despawn();
		}
		if (isAlive() && anchor != null) {
			vel.x += acc.x;
			vel.y += acc.y;
			
			if (Math.abs(vel.x) > dxcap) vel.x = vel.x*dxcap/Math.abs(vel.x);
			if (Math.abs(vel.y) > dycap) vel.y = vel.y*dycap/Math.abs(vel.y);
			
			anchorX += vel.x;
			pos.x = anchor.getX()+anchorX;
			pos.y += vel.y;
			
			acc.set(0f, 0f);
		} else super.update();
	}
	
}
