package zan.game.input;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/** Input manager class */
public class InputManager {
	
	private static ArrayList<MouseEvent> mouseEvents;
	private static ArrayList<Character> charEvents;
	
	private static boolean mouseGrab;
	private static int mouseX, mouseY;
	private static int mouseDX, mouseDY;
	private static int mouseDW;
	
	private static boolean[] keyPressed = new boolean[Keyboard.KEYBOARD_SIZE];
	private static boolean[] keyReleased = new boolean[Keyboard.KEYBOARD_SIZE];
	
	public static void init() {
		mouseEvents = new ArrayList<MouseEvent>();
		charEvents = new ArrayList<Character>();
		mouseGrab = false;
		mouseX = Mouse.getX();
		mouseY = Mouse.getY();
		mouseDX = Mouse.getDX();
		mouseDY = Mouse.getDY();
		mouseDW = Mouse.getDWheel();
	}
	
	public static void poll() {
		mouseEvents.clear();
		while (Mouse.next()) {
			MouseEvent event = new MouseEvent();
			event.poll();
			mouseEvents.add(event);
		}
		mouseX = Mouse.getX();
		mouseY = Mouse.getY();
		mouseDX = Mouse.getDX();
		mouseDY = Mouse.getDY();
		mouseDW = Mouse.getDWheel();
		
		charEvents.clear();
		for (int i=0;i<Keyboard.KEYBOARD_SIZE;i++) {
			keyPressed[i] = false;
			keyReleased[i] = false;
		}
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				char ch = Keyboard.getEventCharacter();
				if (ch != Keyboard.CHAR_NONE) charEvents.add(ch);
				
				if (!Keyboard.isRepeatEvent()) {
					keyPressed[Keyboard.getEventKey()] = true;
				}
			} else {
				if (!Keyboard.isRepeatEvent()) {
					keyReleased[Keyboard.getEventKey()] = true;
				}
			}
		}
		
		/*if (isKeyPressed(Keyboard.KEY_ESCAPE)) mouseGrab = false;
		else if (isButtonDown(0)) mouseGrab = true;
		if (Mouse.isGrabbed() != mouseGrab) Mouse.setGrabbed(mouseGrab);*/
	}
	
	public static ArrayList<MouseEvent> getMouseEvents() {return mouseEvents;}
	public static ArrayList<Character> getCharEvents() {return charEvents;}
	
	public static boolean isMouseGrabbed() {return mouseGrab;}
	public static int getMouseX() {return mouseX;}
	public static int getMouseY() {return mouseY;}
	public static int getMouseDX() {return mouseDX;}
	public static int getMouseDY() {return mouseDY;}
	public static int getMouseDW() {return mouseDW;}
	public static boolean isButtonDown(int btn) {return Mouse.isButtonDown(btn);}
	
	public static boolean isKeyDown(int key) {return Keyboard.isKeyDown(key);}
	public static boolean isKeyPressed(int key) {
		boolean pressed = keyPressed[key];
		keyPressed[key] = false;
		return pressed;
	}
	public static boolean isKeyReleased(int key) {
		boolean released = keyReleased[key];
		keyReleased[key] = false;
		return released;
	}
	
}
