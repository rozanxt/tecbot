package zan.tecbot.object.entity;

public abstract class BadBot extends BaseEntity {
	
	protected boolean stompAble;
	
	public BadBot() {
		super();
		stompAble = false;
		facing = 1;
	}
	
	public boolean isStompAble() {return stompAble;}
	
	public void stomp() {
		inflictDamage(30f);
	}
	
}
