package zan.game.object;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;

public class Collision {
	
	public BaseObject a, b;
	public float distance;
	public Vector2f normal;
	public Vector2f contact;
	
	public static final float normangle = 0.5f;
	
	public Collision() {
		a = null;
		b = null;
		distance = 0f;
		normal = null;
		contact = null;
	}
	
	public boolean normFriction() {
		if (Math.abs(normal.y) > Collision.normangle) return true;
		return false;
	}
	
	public static void createPairs(ArrayList<BaseObject> entity, ArrayList<Pair> pairs) {
		for (int i=0;i<entity.size();i++) {
			for (int j=0;j<entity.size();j++) {
				if (i == j) continue;
				
				Pair pair = new Pair(entity.get(i), entity.get(j));
				boolean equal = false;
				for (int k=0;k<pairs.size();k++) {
					if (Pair.equalPair(pairs.get(k), pair)) {
						equal = true;
						continue;
					}
				}
				if (!equal) pairs.add(pair);
			}
		}
	}
	
	private static Collision checkCollision(BaseObject sa, BaseObject sb) {
		ArrayList<Vector2f> acol = sa.getShapePoints();
		
		float bestDistance = -Float.MAX_VALUE;
		
		ArrayList<Vector2f> anorm =  new ArrayList<Vector2f>();
		for (int i=0;i<acol.size();i++) {
			int j = i+1;
			if (i == acol.size()-1) j = 0;
			
			float distx = acol.get(j).x - acol.get(i).x;
			float disty = acol.get(j).y - acol.get(i).y;
			Vector2f n = new Vector2f(-disty, distx);
			if (n.length() == 0f) return null;
			n.normalise();
			anorm.add(n);
		}
		
		Collision collision = new Collision();
		collision.a = sa;
		collision.b = sb;
		
		for (int i=0;i<acol.size();i++) {
			Vector2f n = anorm.get(i);
			Vector2f s = sb.getSupportPoint((Vector2f) n.negate());
			Vector2f p = Vector2f.sub(s, acol.get(i), null);
			float distance = Vector2f.dot((Vector2f) n.negate(), p);
			if (distance > bestDistance) {
				bestDistance = distance;
				
				collision.distance = bestDistance;
				collision.normal = n;
				collision.contact = s;
			}
		}
		
		if (collision.distance > 0f) return null;
		return collision;
	}
	
	public static Collision isCollided(BaseObject sa, BaseObject sb) {
		Collision ca = checkCollision(sa, sb);
		Collision cb = checkCollision(sb, sa);
		
		if (ca == null) return null;
		if (cb == null) return null;
		
		if (ca.distance > cb.distance) return ca;
		return cb;
	}
	
	public static void resolveCollision(BaseObject sa, BaseObject sb) {
		Collision col = isCollided(sa, sb);
		if (col == null) return;
		col.b.collide(col.a, col);
		col.normal.negate();
		col.a.collide(col.b, col);
	}
	
}
