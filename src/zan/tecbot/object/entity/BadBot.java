package zan.tecbot.object.entity;

import zan.tecbot.mechanism.GridMap;

public abstract class BadBot extends BaseEntity {
	
	protected GridMap gridMap;
	
	protected boolean targetInSight;
	
	protected boolean stompAble;
	
	public BadBot(GridMap gm) {
		super();
		gridMap = gm;
		targetInSight = false;
		stompAble = false;
		facing = 1;
	}
	
	public void targetInSight(boolean ss) {targetInSight = ss;}
	public float getViewAngle() {
		if (facing == 0) return 0f;
		else return 180f;
	}
	
	public boolean isStompAble() {return stompAble;}
	
	public void stomp() {
		inflictDamage(30f);
	}
	
}
