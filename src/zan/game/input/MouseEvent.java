package zan.game.input;

import org.lwjgl.input.Mouse;

/** Mouse event class */
public class MouseEvent {
	
	private int mx, my;
	private int dx, dy;
	
	private int dwheel;
	
	private int button;
	private boolean buttonDown;
	
	public MouseEvent() {}
	
	public void poll() {
		mx = Mouse.getEventX();
		my = Mouse.getEventY();
		dx = Mouse.getEventDX();
		dy = Mouse.getEventDY();
		dwheel = Mouse.getEventDWheel();
		button = Mouse.getEventButton();
		buttonDown = Mouse.getEventButtonState();
	}
	
	public int getMX() {return mx;}
	public int getMY() {return my;}
	
	public int getDX() {return dx;}
	public int getDY() {return dy;}
	
	public int getDWheel() {return dwheel;}
	
	public boolean isButton(int btn) {return button == btn;}
	public boolean isButtonDown() {return buttonDown;}
	
}
