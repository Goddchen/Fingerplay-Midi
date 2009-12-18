package com.flat20.gui.widgets;

import com.flat20.gui.Materials;
import com.flat20.gui.sprites.MaterialSprite;

public class ScreenNavigator extends Button {
/*
	//TODO Put all textures in one static class for clarity.
	final private static Texture BACKGROUND_TEXTURE = TextureManager.createResourceTexture(R.drawable.navigation_bar, 32, 16);
	final private static NineSliceMaterial BACKGROUND_MATERIAL = new NineSliceMaterial(BACKGROUND_TEXTURE, 4, 4, 28, 12);

	final protected static ResourceTexture SKIN_TEXTURE = TextureManager.createResourceTexture(R.drawable.buttons_ps, 32, 64);
	//final protected static NineSliceMaterial DEFAULT_MATERIAL = new NineSliceMaterial(SKIN_TEXTURE, 0, 4, 28, 32,  0, 6, 10, 17);
	final protected static NineSliceMaterial CLICKED_MATERIAL = new NineSliceMaterial(SKIN_TEXTURE, 0, 4, 28, 32,  17, 23, 28, 34);
*/

	protected MaterialSprite mDefault;
	protected MaterialSprite mClicked;

	protected IScrollListener mScrollListener;

	public ScreenNavigator(int width, int height) {
		super(width, height);

		mDefault = new MaterialSprite(Materials.NAVIGATION_BAR, width, height);
		addSprite(mDefault);

		// calculate screenHeight / height and use that.
		mClicked = new MaterialSprite(Materials.MC_OUTLINE, width, height);
		addSprite(mClicked);

        setSize(width, height);
	}

	// important.
	public void updateScreenHeight(int screenHeight, int totalHeight) {
		float dh = screenHeight / (float)totalHeight;
		mClicked.setSize(mClicked.width, (int)(dh*height));
	}
	
	public void setScrollListener(IScrollListener listener) {
		mScrollListener = listener;
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);

		mDefault.setSize(width, height);
		mClicked.setSize(width, mClicked.height);
	}
/*
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
*/

	@Override
	public boolean onTouchMove(int touchX, int touchY, float pressure) {
		//float dy = (float)touchY / (float)height;
		//int realY = (int) (dy * height);
		// keep it inside our area.
		int realY = Math.max(0, Math.min(height-mClicked.height, touchY - (mClicked.height>>1)));
		mClicked.y = realY;//Math.max(0, Math.min(height-mClicked.height, realY - (mClicked.height>>1)));
		float dy = realY / (float)height;
		if (mScrollListener != null)
			mScrollListener.onScroll(dy);
		return true;
	}

	@Override
	protected void onPress() {
		//mDefault.visible = false;
		//mClicked.visible = true;
		super.onPress();
	}

	@Override
	protected void onRelease() {
		//mDefault.visible = true;
		//mClicked.visible = false;
		super.onRelease();
	}
	
	public interface IScrollListener {
		public void onScroll(float pos);
	}
}
