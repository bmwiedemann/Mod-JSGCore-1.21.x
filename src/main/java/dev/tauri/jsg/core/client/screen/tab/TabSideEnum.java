package dev.tauri.jsg.core.client.screen.tab;

/**
 * Describes on which side the tab is.
 * 
 * @author MrJake222
 * 
 */
public enum TabSideEnum {
	LEFT,
	RIGHT;
	
	public boolean right() {
		return this == RIGHT;
	}
	
	public boolean left() {
		return this == LEFT;
	}
}
