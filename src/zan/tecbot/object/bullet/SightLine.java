package zan.tecbot.object.bullet;

/*import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.util.vector.Vector2f;*/

import zan.game.object.BaseObject;
import zan.game.object.Collision;
import zan.game.object.Shape;
import zan.tecbot.object.block.DestroyAbleBlock;
import zan.tecbot.object.entity.BadBot;
import zan.tecbot.object.entity.Tecbot;

public class SightLine extends Bullet {
	
	protected BadBot owner;
	
	public SightLine(BadBot so) {
		super();
		shape = new Shape();
		shape.addPoint(0f, 0.4f);
		shape.addPoint(0f, 0.6f);
		shape.addPoint(1f, 0.6f);
		shape.addPoint(1f, 0.4f);
		shape.fix();
		setSize(80f);
		setSpeed(80f);
		setDamage(0f);
		setRange(0f);
		setCap(getSpeed(), getSpeed());
		owner = so;
	}
	
	protected void outOfRange() {
		setVel(0f, 0f);
		hostile = false;
		dist = 0f;
	}
	
	public boolean collide(BaseObject obj, Collision col) {
		if (hostile) {
			if (obj instanceof DestroyAbleBlock) return false;
			if (obj instanceof BadBot) return false;
			if (obj instanceof Tecbot) {
				owner.targetInSight(true);
				return true;
			}
		}
		outOfRange();
		owner.targetInSight(false);
		return false;
	}
	
	public void update() {
		if (!isHostile() && owner.isAlive()) {
			setPos(owner.getX()+(float)Math.cos(owner.getViewAngle()*(Math.PI/180f)), owner.getY()-(float)Math.sin(owner.getViewAngle()*(Math.PI/180f)));
			setVel((float)Math.cos(owner.getViewAngle()*(Math.PI/180f))*getSpeed(), -(float)Math.sin(owner.getViewAngle()*(Math.PI/180f))*getSpeed());
			setAngle(owner.getViewAngle());
			spawn();
		}
		super.update();
	}
	
	 public void render() {
		/*glDisable(GL_TEXTURE_2D);
		glPushMatrix();
		
		glTranslatef(pos.x, pos.y, 0f);
		glScalef(size, size, 0f);
		glRotatef(-angle, 0f, 0f, 1f);
		
		glColor4f(1f, 1f, 1f, 0.5f);
		glBegin(GL_LINE_LOOP);
			for (int i=0;i<shape.getNumPoints();i++) {
				Vector2f vertex = shape.getPoint(i);
				glVertex2f(vertex.x - 0.5f, vertex.y - 0.5f);
			}
		glEnd();
		glColor4f(1f, 1f, 1f, 1f);
		
		glPopMatrix();
		glEnable(GL_TEXTURE_2D);*/
	}
	
}
