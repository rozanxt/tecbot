package zan.tecbot.object.entity;

import zan.game.object.Shape;
import zan.game.sprite.AnimatedSprite;
import zan.game.sprite.ISprite;
import zan.game.sprite.Sprite;
import zan.game.sprite.SpriteManager;
import zan.game.util.GameUtility;
import zan.tecbot.mechanism.GridMap;

public class DummBot extends BadBot {
	
	protected ISprite[] sprite;
	
	public DummBot(GridMap gm) {
		super(gm);
		setSize(100f);
		shape = new Shape();
		shape.addPoint(0.31f, 0.15f);
		shape.addPoint(0.31f, 0.8f);
		shape.addPoint(0.69f, 0.8f);
		shape.addPoint(0.69f, 0.15f);
		shape.fix();
		
		sprite = new ISprite[2];
		sprite[0] = SpriteManager.getSprite("bot_idle");
		ISprite[] ani = new Sprite[12];
		for (int i=0;i<12;i++) ani[i] = SpriteManager.getSprite("bot_move" + i);
		sprite[1] = new AnimatedSprite((Sprite[])ani);
		AnimatedSprite anim = (AnimatedSprite) sprite[1];
		anim.setAnimation(true, false, 3);
		
		setMaxHealth(50f);
		setCap(2.5f, 10f);
	}
	
	public void update() {
		AnimatedSprite anim = (AnimatedSprite) sprite[1];
		moving = false;
		if (isAlive()) {
			if (ground) {
				setDY(-5f);
				if (GameUtility.getRnd().nextInt(100) == 64) {
					setDY(0f);
					applyForceY(7f);
					ground = false;
					onground = false;
				}
				if (facing == 1) {applyForceX(-0.5f); moving = true;}
				else if (facing == 0) {applyForceX(0.5f); moving = true;}
			} else {
				applyForceY(-0.25f);
				if (facing == 1) {applyForceX(-0.1f);}
				else if (facing == 0) {applyForceX(0.1f);}
			}
			
			if (GameUtility.getRnd().nextInt(100) == 32) {
				if (facing == 0) facing = 1;
				else if (facing == 1) facing = 0;
			}
			
			if (onmoving && !moving) anim.setCurFrame(0);
			onmoving = moving;
			angle = 0f;
		} else {
			applyForceY(-0.25f);
			angle = 30f;
		}
		ground = false;
		super.update();
		anim.update();
	}
	
	public void render() {
		if (isAlive()) {
			if (ground && moving) sprite[1].render(getX(), getY(), getSize(), angle, facing, 1f);
			else sprite[0].render(getX(), getY(), getSize(), angle, facing, 1f);
		} else sprite[0].render(getX(), getY(), getSize(), angle, facing, 0.5f);
		
		/*glColor4f(1f, 0f, 1f, 1f);
		super.render();
		glColor4f(1f, 1f, 1f, 1f);*/
	}
	
}
