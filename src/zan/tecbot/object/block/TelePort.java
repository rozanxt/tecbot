package zan.tecbot.object.block;

import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;

import zan.game.input.InputManager;
import zan.game.object.BaseObject;
import zan.game.object.Collision;
import zan.game.object.Shape;
import zan.tecbot.mechanism.GridMap;
import zan.tecbot.object.entity.Tecbot;

public class TelePort extends Block {
	
	protected GridMap gridMap;
	
	protected Vector2f teleDest;
	protected boolean onPort;
	
	public TelePort(int sx, int sy, GridMap gm) {
		super(sx, sy);
		gridMap = gm;
		shape = new Shape();
		shape.addPoint(0f, 0f);
		shape.addPoint(0f, 1f);
		shape.addPoint(1f, 1f);
		shape.addPoint(1f, 0f);
		shape.fix();
		setSolid(false);
		setPowered(true);
		teleDest = null;
		onPort = false;
	}
	
	public boolean collide(BaseObject obj, Collision col) {
		if (obj instanceof Tecbot) {
			if (teleDest != null) {
				obj.setPos(teleDest.x, teleDest.y);
				obj.setVel(0f, 0f);
				teleDest = null;
			} else onPort = true;
			return true;
		}
		return false;
	}
	
	public void update() {
		if (isPowered() && onPort) {
			if (InputManager.isKeyPressed(Keyboard.KEY_SPACE)) {
				teleDest = gridMap.getTeleDest(getTileX(), getTileY(), wireID);
			}
			onPort = false;
		}
	}
	
	public void render() {
		glDisable(GL_TEXTURE_2D);
		glPushMatrix();
		
		glTranslatef(pos.x, pos.y, 0f);
		glScalef(size, size, 0f);
		glRotatef(-angle, 0f, 0f, 1f);
		
		if (onPort) glColor4f(0f, 1f, 0f, 1f);
		else glColor4f(1f, 0f, 1f, 1f);
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
