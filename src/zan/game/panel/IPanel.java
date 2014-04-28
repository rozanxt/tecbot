package zan.game.panel;

public interface IPanel {
	
	public void init();
	public void destroy();
	public void update();
	public void render();
	
	public boolean isInitialized();
	
}
