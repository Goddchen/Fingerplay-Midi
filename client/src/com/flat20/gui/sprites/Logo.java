package com.flat20.gui.sprites;

import com.flat20.fingerplay.R;
import com.flat20.gui.textures.TextureManager;

public class Logo extends SimpleSprite {
	public Logo(int screenWidth, int screenHeight) {
		super( TextureManager.createResourceTexture(R.drawable.fingerplay_background_darker, 512, 512) );
		
		// Make sure our logo is drawn over the whole screen (or more)
		if (screenWidth > width || screenHeight > height) {
			if (screenWidth > screenHeight) {
				float ratio = screenWidth / (float)width;
				setSize(screenWidth, (int)(height*ratio));
			} else {
				float ratio = screenHeight / (float)height;
				setSize((int)(width*ratio), screenHeight);
			}
		} else { // center if bigger than screen
			x = screenWidth / 2 - width / 2;
			y = screenHeight / 2 - height / 2;
		}

	}
}
