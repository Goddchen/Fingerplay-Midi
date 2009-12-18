package com.flat20.gui.animations;

import com.flat20.gui.sprites.Sprite;

public class Slide extends Animation {

	protected Sprite mSprite;

	protected int mFixedDestX;
	protected int mFixedDestY;
	protected int mFixedCurrentX;
	protected int mFixedCurrentY;

	protected int timer = 0;

	/**
	 * A fixed point Slide animation. Animations will be set with a value from 0.0-1.0 
	 * in the future, but this is quicker.
	 * 
	 * @param sprite
	 * @param destX
	 * @param destY
	 */
	public Slide(Sprite sprite, int destX, int destY) {
		mSprite = sprite;
		set(destX, destY);
	}

	public void set(int destX, int destY) {
		mFixedDestX = destX << 16;
		mFixedDestY = destY << 16;
		mFixedCurrentX = mSprite.x << 16;
		mFixedCurrentY = mSprite.y << 16;
		timer = 0;
	}

	@Override
	public boolean update() {
		// mul needs to be shifted back from <<32 to <<16
		mFixedCurrentX += (mFixedDestX - mFixedCurrentX) >> 3;
		mFixedCurrentY += (mFixedDestY - mFixedCurrentY) >> 3;
		mSprite.x = mFixedCurrentX >> 16;
		mSprite.y = mFixedCurrentY >> 16;
		return (++timer < 30);
	}

}
