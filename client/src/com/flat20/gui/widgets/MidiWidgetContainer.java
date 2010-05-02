package com.flat20.gui.widgets;

import android.view.KeyEvent;

import com.flat20.gui.animations.Animation;
import com.flat20.gui.animations.AnimationManager;
import com.flat20.gui.animations.Slide;
import com.flat20.gui.sprites.Sprite;
import com.flat20.gui.widgets.IScrollListener;
import com.flat20.gui.widgets.Scrollbar.IScrollable;

/**
 * Expands after added content size.
 * 
 * @author andreas
 *
 */
public class MidiWidgetContainer extends WidgetContainer implements IScrollable {

	private int mScreenHeight;

	final private AnimationManager mAnimationManager;

	// Slide animation when you click the navigation buttons.
    private Slide mSlide = null;
	
	private IScrollListener mScrollListener;

	public MidiWidgetContainer(int screenWidth, int screenHeight) {
		super(0, 0);
		mScreenHeight = screenHeight;

		mAnimationManager = AnimationManager.getInstance();

		mSlide = new Slide(this, 0, y);
	}

	/**
	 * Pauses our internal drag animation and slides to destY
	 * @param destY
	 */
	@Override
	public void scrollTo(int destY) {
		mSlide.set(0, destY);

		if (!mAnimationManager.hasAnimation(mSlide));
			mAnimationManager.add( mSlide );
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void setUpdateListener(IScrollListener listener) {
		mScrollListener = listener;
	}

	// TOOD Move to GUI
	public void onKeyDown(int keyCode, KeyEvent event) {
		if (mFocusedWidget != null) {
			WidgetContainer wc = (WidgetContainer) mFocusedWidget;
			if (wc.getFocusedWidget() instanceof MidiWidget) {
				MidiWidget mw = (MidiWidget)wc.getFocusedWidget();
				mw.setHold( !mw.isHolding() );
			}
		}
		//return super.onKeyDown(keyCode, event);
	}

	/*
	 * Adds the Sprite to the list and expands width and height.
	 */
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

	public void setY(int newY) {
		y = Math.max(-(this.height-mScreenHeight), Math.min(0, newY));
		//Log.i("MWC", "setY = " + y + " height = " + this.height + ", " + Renderer.VIEWPORT_HEIGHT);
		if (mScrollListener != null)
			mScrollListener.onScrollChanged(y);
	}

	@Override
	public boolean onTouchDown(int touchX, int touchY, float pressure) {
		super.onTouchDown(touchX, touchY, pressure);
		return true;
	}

	@Override 
	public boolean onTouchMove(int touchX, int touchY, float pressure) {
		return super.onTouchMove(touchX, touchY, pressure);
	}

	@Override
	public boolean onTouchUp(int touchX, int touchY, float pressure) {
		return super.onTouchUp(touchX, touchY, pressure);
	}

	class DragAnimation extends Animation {

		private MidiWidgetContainer mContainer;

		public float velocityY = 0;
		public float y = 0;
		public boolean isRunning = true;

		/**
		 * A neverending animation for our MidiWidgetContainer 
		 * 
		 * @param sprite
		 * @param destX
		 * @param destY
		 */
		public DragAnimation(MidiWidgetContainer container) {
			mContainer = container;
		}

		@Override
		public boolean update() {
			if (isRunning) {
				if (Math.abs(velocityY) < 0.1) {
					isRunning = false;
				} else {
					y += velocityY;
					mContainer.setY( (int)y );
					velocityY *= 0.9f;
				}
				
			}
			return true;
		}

	}

}