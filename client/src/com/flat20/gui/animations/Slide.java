package com.flat20.gui.animations;

import android.util.Log;

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
		mFixedCurrentX += (mFixedDestX - mFixedCurrentX) >> 2;
		mFixedCurrentY += (mFixedDestY - mFixedCurrentY) >> 2;
		mSprite.x = mFixedCurrentX >> 16;
		mSprite.y = mFixedCurrentY >> 16;

		// Finished check
		if ( (++timer < 30) )
			return true;
		else {
			mSprite.x = mFixedDestX >> 16;
			mSprite.y = mFixedDestY >> 16;
			return false;
		}
	}

}
