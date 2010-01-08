package com.flat20.gui.animations;

import com.flat20.gui.sprites.Sprite;

/**
 * A sliding animation which is run at startup.
 * 
 * @author andreas
 *
 */
public class Splash extends Animation {

	protected Sprite mSprite;
	protected int mStartDelay;
	protected int mLength;

	protected int mFixedDestX;
	//protected int mFixedDestY;
	protected int mFixedCurrentX;
	//protected int mFixedCurrentY;

	protected int timer = 0;
	

	public Splash(Sprite sprite, int length, int startDelay, int startX, int endX) {
		mSprite = sprite;
		set(length, startDelay, startX, endX);
	}

	public void set(int length, int startDelay, int startX, int endX) {
		mLength = length;
		mStartDelay = startDelay;
		mFixedCurrentX = startX << 16;
		mFixedDestX = endX << 16;
		//mFixedDestY = destY << 16;
		//mFixedCurrentY = mSprite.y << 16;
		timer = 0;
	}

	@Override
	public boolean update() {
		if (++timer < mStartDelay)
			return true;

		mFixedCurrentX += (mFixedDestX - mFixedCurrentX) >> 3;
		//mFixedCurrentY += (mFixedDestY - mFixedCurrentY) >> 4;
		mSprite.x = mFixedCurrentX >> 16;
		//mSprite.y = mFixedCurrentY >> 16;

		//mSprite.x -= 1;

		// Finished check
		if ( (timer < mStartDelay+mLength) )
			return true;
		else {
			mSprite.x = mFixedDestX >> 16;
			//mSprite.y = mFixedDestY >> 16;
			return false;
		}
	}

}
