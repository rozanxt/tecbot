package zan.tecbot.object.block;

import org.lwjgl.util.vector.Vector2f;

import zan.game.object.BaseObject;
import zan.game.object.Collision;
import zan.game.object.Shape;
import zan.tecbot.object.entity.BaseEntity;

public class BumperBlock extends Block {
	
	public BumperBlock() {
		super();
		shape = new Shape();
		shape.addPoint(0f, 0f);
		shape.addPoint(0f, 1.2f);
		shape.addPoint(1f, 1.2f);
		shape.addPoint(1f, 0f);
		shape.fix();
	}
	
	public boolean collide(BaseObject obj, Collision col) {
		if (obj instanceof BaseEntity) {
			Vector2f norm = col.normal;
			Vector2f negnorm = new Vector2f();
			norm.negate(negnorm);
			
			if (col.normFriction() && negnorm.y > 0f && obj.getSupportPoint(negnorm).y >= getSupportPoint(norm).y && obj.getDY() < 0f) {
		 		BaseEntity entity = (BaseEntity) obj;
		 		entity.bump();
		 		return true;
			}
		}
		return false;
	}
	
}
