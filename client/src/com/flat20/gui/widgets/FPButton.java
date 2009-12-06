package com.flat20.gui.widgets;

import com.flat20.fingerplay.R;
import com.flat20.gui.sprites.MaterialSprite;
import com.flat20.gui.sprites.SimpleSprite;
import com.flat20.gui.sprites.Sprite;
import com.flat20.gui.textures.NineSliceMaterial;
import com.flat20.gui.textures.ResourceTexture;
import com.flat20.gui.textures.TextureManager;

public class FPButton extends Button {

	final protected static ResourceTexture SKIN_TEXTURE = TextureManager.createResourceTexture(R.drawable.buttons_ps, 32, 64);
	final protected static NineSliceMaterial DEFAULT_MATERIAL = new NineSliceMaterial(SKIN_TEXTURE, 0, 4, 28, 32,  0, 6, 10, 17);
	final protected static NineSliceMaterial CLICKED_MATERIAL = new NineSliceMaterial(SKIN_TEXTURE, 0, 4, 28, 32,  17, 23, 28, 34);

	protected MaterialSprite mDefault;
	protected MaterialSprite mClicked;
	protected Sprite mIcon;
	
	protected boolean mIsActive = false;

	public FPButton(int iconResourceId, int width, int height) {
		super(width, height);

		mDefault = new MaterialSprite(DEFAULT_MATERIAL, width, height);
		addSprite(mDefault);

		mClicked = new MaterialSprite(CLICKED_MATERIAL, width, height);
		mClicked.visible = false;
		addSprite(mClicked);

        ResourceTexture iconTexture = TextureManager.createResourceTexture(iconResourceId, 32, 32);
        mIcon = new SimpleSprite(iconTexture);
        mIcon.x = width/2 - 16;
        mIcon.y = height/2 - 16;
        addSprite(mIcon);

        setSize(width, height);
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);

		mDefault.setSize(width, height);
		mClicked.setSize(width, height);
		mIcon.x = width/2 - 16;
		mIcon.y = height/2 - 16;
	}

	public void setActive(boolean active) {
		mIsActive = active;
		if (active) {
			mDefault.visible = false;
			mClicked.visible = true;
		} else {
			mDefault.visible = true;
			mClicked.visible = false;
		}
	}

	@Override
	protected void onPress() {
		mDefault.visible = false;
		mClicked.visible = true;
		super.onPress();
	}

	@Override
	protected void onRelease() {
		if (!mIsActive) {
			mDefault.visible = true;
			mClicked.visible = false;
		}
		super.onRelease();
	}
/*
	@Override
	public void onFocusChanged(boolean focus) {
		super.onFocusChanged(focus);

		if (focus) {
			mDefault.visible = false;
			mClicked.visible = true;
		} else if (!mIsActive) {
			mDefault.visible = true;
			mClicked.visible = false;
		}
	}
*/
}
