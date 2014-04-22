package zan.game.sprite;

/** Sprite class */
public class AnimatedSprite implements ISprite {
	
	/** Sprites */
	private Sprite[] sprites;
	
	/** Animated state */
	private boolean animated;
	
	/** Reversed animation state */
	private boolean reversed;

	/** Current frame index */
	private int curFrame;
	
	/** Frame tick */
	private int frameTick;
	
	/** Frame period */
	private int framePeriod;
	
	/** Constructor */
	public AnimatedSprite(Sprite[] sprites) {
		this.sprites = sprites;
		animated = false;
		reversed = false;
		curFrame = 0;
		frameTick = 0;
		framePeriod = 0;
	}
	
	/** Set animation */
	public void setAnimation(boolean ani, boolean rev, int period) {
		animated = ani;
		curFrame = 0;
		frameTick = 0;
		if (animated) {
			reversed = rev;
			framePeriod = period;
		} else {
			reversed = false;
			framePeriod = 0;
		}
	}
	
	/** Set current frame */
	public void setCurFrame(int cf) {
		curFrame = cf;
		frameTick = 0;
	}
	
	/** @return true if animated */
	public boolean isAnimated() {
		return animated;
	}
	
	/** @return true if animation is reversed */
	public boolean isReversed() {
		return reversed;
	}
	
	/** @return number of frames */
	public int getNumFrames() {
		return sprites.length;
	}
	
	/** Update animation */
	public void update() {
		if (animated) {
			frameTick++;
			if (frameTick >= framePeriod) {
				frameTick = 0;
				if (reversed) curFrame--;
				else curFrame++;
				if (curFrame >= getNumFrames()) curFrame = curFrame % getNumFrames();
				if (curFrame < 0) curFrame = (curFrame % getNumFrames()) + getNumFrames();
			}
		}
	}
	
	/** Render basic sprite */
	public void render(float sx, float sy, float size) {
		render(sx, sy, size, 0f, 0, 1f);
	}
	
	/** Render raw sprite */
	public void render(float sx, float sy, float size, float rot, int flip, float alpha) {
		sprites[curFrame].render(sx, sy, size, rot, flip, alpha);
	}
	
}
