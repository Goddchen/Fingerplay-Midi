package com.flat20.gui.widgets;

import java.util.ArrayList;

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
	private MaterialSprite mThumbHighlight;

	private IScrollable mTarget;
	private int mVisibleArea; // the screen height of the device or the app.

	private int mScreenYs[];

	public Scrollbar(int width, int height, IScrollable target, WidgetContainer widgetContainer, int visibleArea) {
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

		mThumbHighlight = new MaterialSprite(Materials.NAVIGATION_SCROLLER_THUMB_HIGHLIGHT, width-2, height);
		mThumbHighlight.x = 1;
		mThumbHighlight.visible = false;
		addSprite(mThumbHighlight);

        setSize(width, height);

        addScreensYIn(widgetContainer);
	}
	
	// TODO Replace this with the proper method of having a data class
	// for the parsed xml data.
	// We use this function to get all screenYs for the snapping.
	private void addScreensYIn(WidgetContainer widgetContainer) {
		ArrayList<Integer> screenYs = new ArrayList<Integer>();
		IWidget[] widgets = widgetContainer.getWidgets();
        for (int i=0; i<widgets.length; i++) {
        	IWidget w = widgets[i];
        	if (w instanceof WidgetContainer) {
				WidgetContainer wc = (WidgetContainer) w;
				screenYs.add(wc.y);
			}
        }

        // stupid Integer -> int hack
        Integer temp[] = new Integer[screenYs.size()];
        screenYs.toArray( temp );

    	mScreenYs = new int[screenYs.size()];
        for (int i=0; i<temp.length; i++) {
        	mScreenYs[i] = temp[i];
        }
	}


	public void onScrollChanged(int newY) {
		float dy = Math.abs(newY) / (float)mTarget.getHeight();

		int touchY = (int) (dy * height);

		int realY = Math.max(0, Math.min(height-mThumb.height, touchY));
		mThumb.y = realY;
		mThumbHighlight.y = mThumb.y;
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);

		mBackground.setSize(width, height);

		float dh = mVisibleArea / (float)mTarget.getHeight();
		mThumb.setSize(mThumb.width, (int)(dh*height));
		mThumbHighlight.setSize(mThumb.width, mThumb.height);
	}

	@Override
	public boolean onTouchDown(int touchX, int touchY, float pressure, int pointerId) {
		mThumbHighlight.visible = true;
		return true;
	}

	@Override
	public boolean onTouchMove(int touchX, int touchY, float pressure, int pointerId) {
		scroll(touchY, false);
		return true;
	}

	@Override
	public boolean onTouchUp(int touchX, int touchY, float pressure, int pointerId) {
		mThumbHighlight.visible = false;
		scroll(touchY, true);
		return true;
	}

	@Override
	public boolean onTouchUpOutside(int touchX, int touchY, float pressure, int pointerId) {
		mThumbHighlight.visible = false;
		scroll(touchY, true);
		return true;
	}

	private void scroll(int touchY, boolean snap) {
		// clamp y inside our height.
		touchY = Math.max(0, Math.min(height-mThumb.height, touchY - (mThumb.height>>1)));
		float dy = touchY / (float)height; // normalized point.
		mThumbHighlight.y = mThumb.y = touchY; // Update the thumb and the thumb highlighter.

		int snappedY = (int) (dy*mTarget.getHeight());
		if (snap) {

			
			int nearestDiff = 100000;
			int nearestScreenIndex = -1;
			final int length = mScreenYs.length;
			for (int i=0; i<length; i++) {
				int diff = Math.abs(snappedY - mScreenYs[i]);
				if (diff < nearestDiff) {
					nearestScreenIndex = i;
					nearestDiff = diff;
				} else if (diff > nearestDiff) {
					break;
				} 
				nearestDiff = diff;
			}

			if (nearestScreenIndex != -1) {
				snappedY = mScreenYs[nearestScreenIndex];

				float normalizedY = (float)snappedY / mTarget.getHeight();
				mThumbHighlight.y = mThumb.y = (int)(normalizedY * this.height);
			}

		}

		mTarget.scrollTo(-snappedY);
	}

	public interface IScrollable {
		public int getHeight();
		public void scrollTo(int destY);
		public void setUpdateListener(IScrollListener listener);
	};

}
