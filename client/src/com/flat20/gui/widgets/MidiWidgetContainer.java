package com.flat20.gui.widgets;

import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11Ext;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.opengl.GLUtils;
import android.util.Log;
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

	//private int mScreenWidth;
	private int mScreenHeight;

	final private AnimationManager mAnimationManager;

	// Slide animation when you click the navigation buttons.
    private Slide mSlide = null;

    private boolean mDragging = false;
	private int dragY;
	private float mDragVelocityY = 0;
	private DragAnimation mDragAnimation;
	
	private IScrollListener mScrollListener;

	public MidiWidgetContainer(int screenWidth, int screenHeight) {
		super(0, 0);
		//mScreenWidth = screenWidth;
		mScreenHeight = screenHeight;

		mAnimationManager = AnimationManager.getInstance();
		mDragAnimation = new DragAnimation(this);
		mAnimationManager.add(mDragAnimation);

		mSlide = new Slide(this, 0, y);
	}

	/**
	 * Pauses our internal drag animation and slides to destY
	 * @param destY
	 */
	@Override
	public void scrollTo(int destY) {
		mDragAnimation.isRunning = false;
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
		if (mScrollListener != null)
			mScrollListener.onScrollChanged(y);
	}

	@Override
	public boolean onTouchDown(int touchX, int touchY, float pressure) {
		boolean result = super.onTouchDown(touchX, touchY, pressure);
		if (!result) {
			mDragAnimation.isRunning = false;
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
			setY(touchY - dragY); // update y

			dragY += lastY-y;

			mDragVelocityY = (mDragVelocityY + (y-lastY)) / 2.0f;

			return true;
		} else
			return super.onTouchMove(touchX, touchY, pressure);
	}

	@Override
	public boolean onTouchUp(int touchX, int touchY, float pressure) {
		if (mDragging) {
			mDragAnimation.y = this.y;
			mDragAnimation.velocityY = mDragVelocityY;
			mDragAnimation.isRunning = true;
			mDragging = false;
			return true;
		} else
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


/*
	private int mTextureID;
	boolean firstTime = true;

	@Override
	public void draw(GL10 gl) {
		super.draw(gl);

		if (firstTime) {
			Log.i("first", "time");
			Bitmap b = toBitmap(0, 0, width, height, gl);
			Log.i("first", "bitmap = " + b);
			mTextureID = createTextureFromBitmap(gl, b);
			Log.i("first", "tID = " + mTextureID);
			firstTime = false;
			
			
			
		}
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureID);

		//gl.glPushMatrix();
		//gl.glTranslatef(0, 0, 0);

		((GL11Ext) gl).glDrawTexfOES(20, 20, 1, 64, 64);
		// 8k / 5sec with draw call.
		//mGrid.draw(gl);
		
		//gl.glPopMatrix();

	}
*/
	protected Bitmap toBitmap(int x, int y, int w, int h, GL10 gl) {   
         final int b[] = new int[w*h]; 
         final int bt[] = new int[w*h]; 
         final IntBuffer ib = IntBuffer.wrap(b);

         gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, ib); 
         for(int by=0; by<h; by++) { 
              for(int bx=0; bx<w; bx++) { 
                   //correction of R and B 
                   int pix = b[by*w+bx]; 
                   int pb = (pix>>16)&0xff; 
                   int pr = (pix<<16)&0x00ff0000; 
                   int pix1 = (pix&0xff00ff00) | pr | pb; 
                   //correction of rows 
                   bt[(h-by-1)*w+bx] = pix1; 
              } 
         }
         return Bitmap.createBitmap(bt, w, h, Config.ALPHA_8); 
    } 

    private static int createTextureFromBitmap(GL10 gl, Bitmap bitmap) {
        int textureId = -1;
        if (gl != null) {
        	final int[] textureNameWorkspace = new int[1];
            gl.glGenTextures(1, textureNameWorkspace, 0);

            textureId = textureNameWorkspace[0];
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);

            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);//REPEAT
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

            gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);

            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

            int error = gl.glGetError();
            if (error != GL10.GL_NO_ERROR) {
                Log.e("TextureManager", "Texture Load GLError: " + error);
            }
        
        }

        return textureId;
    }

}