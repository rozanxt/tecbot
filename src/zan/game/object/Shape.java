package zan.game.object;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;

public class Shape {
	
	private ArrayList<Vector2f> points;
	private boolean fixed;
	
	public Shape() {
		points = new ArrayList<Vector2f>();
		fixed = false;
	}
	
	public void fix() {fixed = true;}
	public void clear() {
		points.clear();
		fixed = false;
	}
	
	public void addPoint(float sx, float sy) {
		if (!fixed) points.add(new Vector2f(sx, sy));
	}
	
	public int getNumPoints() {return points.size();}
	
	public Vector2f getPoint(int index) {
		if (index < 0 && index >= points.size()) return null;
		return points.get(index);
	}
	
	public ArrayList<Vector2f> getPoints() {return points;}
	
	public Vector2f getPoint(int index, float sx, float sy, float size, float angle) {
		float raw_x = points.get(index).x - 0.5f;
		float raw_y = points.get(index).y - 0.5f;
		float tf_x = ((float)(raw_x * Math.cos(-angle*(Math.PI/180.0)) - raw_y * Math.sin(-angle*(Math.PI/180.0))) * size) + sx;
		float tf_y = ((float)(raw_x * Math.sin(-angle*(Math.PI/180.0)) + raw_y * Math.cos(-angle*(Math.PI/180.0))) * size) + sy;
		return new Vector2f(tf_x, tf_y);
	}
	
	public ArrayList<Vector2f> getPoints(float sx, float sy, float size, float angle) {
		ArrayList<Vector2f> dest = new ArrayList<Vector2f>();
		for (int i=0;i<points.size();i++) dest.add(getPoint(i, sx, sy, size, angle));
		return dest;
	}
	
}
