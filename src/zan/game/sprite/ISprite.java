package zan.game.sprite;

public interface ISprite {
	
	void render(float sx, float sy, float size);
	void render(float sx, float sy, float size, float rot, int flip, float alpha);
	
}
