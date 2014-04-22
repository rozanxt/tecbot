package zan.game.input;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/** Input manager class */
public class InputManager {
	
	private static ArrayList<MouseEvent> mouseEvents;
	private static ArrayList<Character> charEvents;
	
	private static boolean[] keyPressed = new boolean[Keyboard.KEYBOARD_SIZE];
	private static boolean[] keyReleased = new boolean[Keyboard.KEYBOARD_SIZE];
	
	public static void init() {
		mouseEvents = new ArrayList<MouseEvent>();
		charEvents = new ArrayList<Character>();
	}
	
	public static void poll() {
		mouseEvents.clear();
		while (Mouse.next()) {
			MouseEvent event = new MouseEvent();
			event.poll();
			mouseEvents.add(event);
		}
		
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
	}
	
	public static ArrayList<MouseEvent> getMouseEvents() {
		return mouseEvents;
	}
	
	public static ArrayList<Character> getCharEvents() {
		return charEvents;
	}
	
	public static boolean isKeyDown(int key) {
		return Keyboard.isKeyDown(key);
	}
	
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
