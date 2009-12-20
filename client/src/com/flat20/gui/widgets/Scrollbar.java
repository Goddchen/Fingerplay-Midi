package com.flat20.gui.widgets;

import com.flat20.gui.Materials;
import com.flat20.gui.sprites.MaterialSprite;

/**
 * TODO Needs a better system for sending scroll messages back and forth.
 * And should be in the GUI code.
 * 
 * @author andreas
 */
public class Scrollbar extends Widget implements IScrollListener {

	private MaterialSprite mBackground;
	private MaterialSprite mThumb;

	private IScrollable mTarget;
	private int mVisibleArea; // the screen height for our fullscreen scroll.

	public Scrollbar(int width, int height, IScrollable target, int visibleArea) {
		super(width, height);

		mTarget = target;
		mVisibleArea = visibleArea;

		mTarget.setUpdateListener(this);

		mBackground = new MaterialSprite(Materials.NAVIGATION_SCROLLER_BACKGROUND, width, height);
		addSprite(mBackground);

		// Thumb
		mThumb = new MaterialSprite(Materials.NAVIGATION_SCROLLER_THUMB, width-2, height);
		mThumb.x = 1;
		addSprite(mThumb);

        setSize(width, height);
	}

	public void onScrollChanged(int newY) {
		float dy = Math.abs(newY) / (float)mTarget.getHeight();

		int touchY = (int) (dy * height);

		int realY = Math.max(0, Math.min(height-mThumb.height, touchY));
		mThumb.y = realY;
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);

		mBackground.setSize(width, height);

		float dh = mVisibleArea / (float)mTarget.getHeight();
		mThumb.setSize(mThumb.width, (int)(dh*height));
	}

	@Override
	public boolean onTouchMove(int touchX, int touchY, float pressure) {
		scroll(touchY);
		return true;
	}

	@Override
	public boolean onTouchUp(int touchX, int touchY, float pressure) {
		snap(touchY);
		return true;
	}

	@Override
	public boolean onTouchUpOutside(int touchX, int touchY, float pressure) {
		snap(touchY);
		return true;
	}

	private void snap(int touchY) {
		int sy = (int)(touchY / (float)mThumb.height);
		int scrollY = sy*mThumb.height + (mThumb.height>>1);
		//Log.i("Scrollbar", "snap touchY = " + touchY + ", to " + scrollY);
		scroll( scrollY );
	}

	private void scroll(int touchY) {
		// keep it inside our area.
		int realY = Math.max(0, Math.min(height-mThumb.height, touchY - (mThumb.height>>1)));
		//Log.i("Scrollbar", "touchY = " + touchY + ", th: " + mThumb.height + ", h: " + height + " realY: " + realY);
		mThumb.y = realY;
		float dy = realY / (float)height;

		mTarget.scrollTo((int) -(dy*mTarget.getHeight()));
	}


	public interface IScrollable {
		public int getHeight();
		public void scrollTo(int destY);
		public void setUpdateListener(IScrollListener listener);
	};

}
