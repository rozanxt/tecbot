package zan.tecbot.object.entity;

import zan.tecbot.mechanism.GridMap;

public abstract class BadBot extends BaseEntity {
	
	protected GridMap gridMap;
	
	protected boolean stompAble;
	
	public BadBot(GridMap gm) {
		super();
		gridMap = gm;
		stompAble = false;
		facing = 1;
	}
	
	public boolean isStompAble() {return stompAble;}
	
	public void stomp() {
		inflictDamage(30f);
	}
	
}
