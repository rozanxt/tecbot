package zan.tecbot.object;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;

import zan.game.object.BaseObject;
import zan.game.object.Collision;
import zan.game.object.Pair;

public abstract class BaseEntity extends BaseObject {
	
	protected boolean ground;
	protected boolean onground;
	protected boolean moving;
	protected boolean onmoving;
	protected int facing;
	
	public BaseEntity() {
		super();
		onground = false;
		ground = false;
		onmoving = false;
		moving = false;
		facing = 0;
	}
	
	public void BlocksInRange(ArrayList<Pair> pairs, ArrayList<Block> blocks) {
		ArrayList<Block> inrange = new ArrayList<Block>();
		ArrayList<Float> distinrange = new ArrayList<Float>();
		for (int i=0;i<blocks.size();i++) {
			Vector2f dist = Vector2f.sub(getPos(), blocks.get(i).getPos(), null);
			if (dist.lengthSquared() < 10000f) {
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
	
	public void collide(BaseObject obj, Collision col) {
		if (obj.getName() == "block") {
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
	}
	
	public void correction() {
		if (onground && !ground) {setY(getY()-getDY()); setDY(0f);}
		onground = ground;
	}
	
	public void bump() {
		applyForceY(9f);
		setDY(0f);
		ground = false;
		onground = false;
	}
	
	/*public void update() {
		// Add moving = false; at the start of child's update function
		// Call super.update(); at the end of child's update function
		
		if (ground) setDY(-5f);
		else applyForceY(-0.25f);
		
		onmoving = moving;
		ground = false;
		super.update();
	}*/
	
}
