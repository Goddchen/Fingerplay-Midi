package com.flat20.gui.widgets;

import android.util.Log;
import android.view.KeyEvent;
import android.view.VelocityTracker;

import com.flat20.gui.animations.Animation;
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
	private float mDragVelocityY = 0;
	private DragAnimation mDragAnimation;

	public MidiWidgetContainer(int screenWidth, int screenHeight) {
		super(0, 0);
		mScreenWidth = screenWidth;
		mScreenHeight = screenHeight;

		mDragAnimation = new DragAnimation(this);
		AnimationManager.getInstance().add(mDragAnimation);
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
	
	public void setY(int newY) {
		y = Math.max(-(this.height-mScreenHeight), Math.min(0, newY));
	}

	@Override
	public boolean onTouchDown(int touchX, int touchY, float pressure) {
		boolean result = super.onTouchDown(touchX, touchY, pressure);
		if (!result) {
			dragY = touchY - y;
			mDragVelocityY = 0;
			mDragging = true;
		}
		return true;
	}

	@Override 
	public boolean onTouchMove(int touchX, int touchY, float pressure) {
		if (mDragging) {
			int lastY = y;
			//y = touchY - dragY;
			//y = Math.max(-(this.height-mScreenHeight), Math.min(0, y));
			setY(touchY - dragY);

			dragY += lastY-y;

			mDragVelocityY = (mDragVelocityY + (y-lastY)) / 2.0f;

			//mDragVelocityY += y-lastY;

			return true;
		} else
			return super.onTouchMove(touchX, touchY, pressure);
	}

	@Override
	public boolean onTouchUp(int touchX, int touchY, float pressure) {
		if (mDragging) {
			mDragAnimation.y = this.y;
			mDragAnimation.velocityY = mDragVelocityY;
			//Log.i("mwc", "animation velY = " + mDragAnimation.velocityY);
			/*
			int velY = (touchY - dragY) - y;
			if (Math.abs(velY) > 3) {
				int newY = Math.max(-(this.height-mScreenHeight), Math.min(0, y+(velY*12)));
				AnimationManager.getInstance().add( new Slide(this, x, newY) );
			}*/
			mDragging = false;
			return true;
		} else
			return super.onTouchUp(touchX, touchY, pressure);
	}
	
	class DragAnimation extends Animation {

		private MidiWidgetContainer mContainer;
 
		public float velocityY;
		public float y = 0;

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
			//y += velocityY;
			mContainer.setY( (int)(mContainer.y + velocityY) );
			//mContainer.y += velocityY;
			velocityY *= 0.9f;
			//Log.i("MidiWidgetcontainer", "velY = " + velocityY);
			return true;
		}

	}

}