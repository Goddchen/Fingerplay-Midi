package com.flat20.gui.animations;

import com.flat20.gui.sprites.Sprite;

public class Resize extends Animation {

	protected Sprite mSprite;

	protected int mFixedDestWidth;
	protected int mFixedDestHeight;
	protected int mFixedCurrentWidth;
	protected int mFixedCurrentHeight;

	protected int timer = 0;

	public Resize(Sprite sprite, int destWidth, int destHeight) {
		mSprite = sprite;
		mFixedDestWidth = destWidth << 16;
		mFixedDestHeight = destHeight << 16;
		mFixedCurrentWidth = sprite.width << 16;
		mFixedCurrentHeight = sprite.height << 16;
	}

	@Override
	public boolean update() {
		mFixedCurrentWidth += (mFixedDestWidth - mFixedCurrentWidth) >> 2;
		mFixedCurrentHeight += (mFixedDestHeight - mFixedCurrentHeight) >> 2;
		mSprite.setSize(mFixedCurrentWidth >> 16, mFixedCurrentHeight >> 16);
		return (++timer < 30);
	}

}
