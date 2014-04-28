package zan.game.sprite;

public interface ISprite {
	
	public void render(float sx, float sy, float size);
	public void render(float sx, float sy, float size, float rot, int flip, float alpha);
	
}
