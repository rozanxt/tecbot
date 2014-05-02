package zan.tecbot.object.block;

public class TileCoord {
	
	public int x, y;
	
	public TileCoord(int sx, int sy) {
		set(sx, sy);
	}
	
	public void set(int sx, int sy) {
		x = sx;
		y = sy;
	}
	public void setTileX(int sx) {x = sx;}
	public void setTileY(int sy) {y = sy;}
	public int getTileX() {return x;}
	public int getTileY() {return y;}
	
}
