package zan.tecbot.object.block;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.util.vector.Vector2f;

import zan.game.object.BaseObject;
import zan.game.object.Collision;

public abstract class Block extends BaseObject {
	
	protected int tileX, tileY;
	
	protected int type;
	protected char wireID;
	protected int side;
	protected boolean solid;
	protected boolean bottomPass;
	protected boolean powered;
	protected boolean inverse;
	protected boolean playerOnly;
	protected boolean highlight;
	
	public Block(int sx, int sy) {
		super();
		tileX = sx;
		tileY = sy;
		type = 0;
		wireID = ' ';
		side = 0;
		solid = false;
		bottomPass = false;
		powered = false;
		inverse = false;
		playerOnly = false;
		highlight = false;
	}
	
	public void setTilePos(int sx, int sy) {tileX = sx; tileY = sy;}
	public void setTileX(int sx) {tileX = sx;}
	public void setTileY(int sy) {tileY = sy;}
	public int getTileX() {return tileX;}
	public int getTileY() {return tileY;}
	
	public void setType(int st) {type = st;}
	public int getType() {return type;}
	
	public void setWireID(char id) {wireID = id;}
	public char getWireID() {return wireID;}
	
	public void setSide(int ss) {
		side = ss;
		if (side == 1) setAngle(90f);
		else if (side == 2) setAngle(180f);
		else if (side == 3) setAngle(270f);
		else setAngle(0f);
	}
	public int getSide() {return side;}
	
	public void setSolid(boolean ss) {solid = ss;}
	public boolean isSolid() {return solid;}
	
	public void setBottomPass(boolean bp) {bottomPass = bp;}
	public boolean isBottomPass() {return bottomPass;}
	
	public void setPowered(boolean so) {powered = so;}
	public boolean isPowered() {
		if (isInverse()) return !powered;
		return powered;
	}
	
	public void setInverse(boolean si) {inverse = si;}
	public boolean isInverse() {return inverse;}
	
	public void setPlayerOnly(boolean po) {playerOnly = po;}
	public boolean isPlayerOnly() {return playerOnly;}
	
	public void highlight() {highlight = true;}
	
	public int hitSide(BaseObject obj, Collision col) {
		Vector2f norm = col.normal;
		
		if (col.normFriction()) {
			if (norm.y > 0f) return 2;
			else if(norm.y < 0f) return 0;
		} else {
			if (norm.x > 0f && obj.getDX() > 0f) return 3;
			else if(norm.x < 0f && obj.getDX() > 0f) return 1;
		}
		return -1;
	}
	
	public void update() {
		super.update();
		highlight = false;
	}
	
	public void render() {
		glDisable(GL_TEXTURE_2D);
		glPushMatrix();
		
		glTranslatef(pos.x, pos.y, 0f);
		glScalef(size, size, 0f);
		glRotatef(-angle, 0f, 0f, 1f);
		
		if (highlight) glColor4f(0f, 0f, 1f, 1f);
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
