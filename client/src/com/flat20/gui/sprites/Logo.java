package com.flat20.gui.sprites;

import com.flat20.fingerplay.R;
import com.flat20.gui.textures.TextureManager;

public class Logo extends SimpleSprite {
	public Logo() {
		super( TextureManager.createResourceTexture(R.drawable.fingerplay_background_darker, 512, 512) );
	}
}
