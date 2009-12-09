package com.flat20.gui.widgets;

import android.util.Log;
import android.view.KeyEvent;

import com.flat20.gui.animations.AnimationManager;
import com.flat20.gui.animations.Slide;
import com.flat20.gui.sprites.Sprite;

/**
 * Expands after added content size.
 * 
 * @author andreas
 *
 */
public class MidiWidgetContainer extends WidgetContainer {

	private int mScreenWidth;
	private int mScreenHeight;

	private boolean mDragging = false;
	private int dragY;

	public MidiWidgetContainer(int screenWidth, int screenHeight) {
		super(0, 0);
		mScreenWidth = screenWidth;
		mScreenHeight = screenHeight;
	}

	// TOOD Move to GUI
	public void onKeyDown(int keyCode, KeyEvent event) {
		//Widget widget = mFocusedWidget;
		if (mFocusedWidget != null) {
			WidgetContainer wc = (WidgetContainer) mFocusedWidget;
			if (wc.getFocusedWidget() instanceof MidiWidget) {
				MidiWidget mw = (MidiWidget)wc.getFocusedWidget();
				mw.setHold( !mw.isHolding() );
			}
		}
		//return super.onKeyDown(keyCode, event);
	}

	@Override
	public void addSprite(Sprite sprite) {
		super.addSprite(sprite);

		if (sprite.y + sprite.height > height) {
			height = sprite.y + sprite.height;
		}

		if (sprite.x + sprite.width > width) {
			width = sprite.x + sprite.width;
		}

	}

	@Override
	public boolean onTouchDown(int touchX, int touchY, float pressure) {
		boolean result = super.onTouchDown(touchX, touchY, pressure);
		if (!result) {
			dragY = touchY - y;
			mDragging = true;
		}
		return true;
	}

	@Override 
	public boolean onTouchMove(int touchX, int touchY, float pressure) {
		if (mDragging) {
			int lastY = y;
			y = touchY - dragY;
			y = Math.max(-(this.height-mScreenHeight), Math.min(0, y));
			dragY += lastY-y;
			return true;
		} else
			return super.onTouchMove(touchX, touchY, pressure);
	}

	@Override
	public boolean onTouchUp(int touchX, int touchY, float pressure) {
		if (mDragging) {
			int velY = (touchY - dragY) - y;
			if (Math.abs(velY) > 3) {
				int newY = Math.max(-(this.height-mScreenHeight), Math.min(0, y+(velY*12)));
				AnimationManager.getInstance().add( new Slide(this, x, newY) );
			} 
			mDragging = false;
			return true;
		} else
			return super.onTouchUp(touchX, touchY, pressure);
	}
}