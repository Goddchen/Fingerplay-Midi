package com.flat20.gui.widgets;

import com.flat20.fingerplay.R;
import com.flat20.fingerplay.midicontrollers.IMidiController;
import com.flat20.fingerplay.midicontrollers.IOnControlChangeListener;
import com.flat20.gui.sprites.MaterialSprite;
import com.flat20.gui.textures.NineSliceMaterial;
import com.flat20.gui.textures.ResourceTexture;
import com.flat20.gui.textures.TextureManager;
import com.flat20.gui.textures.TiledMaterial;

public abstract class MidiWidget extends Widget implements IMidiController {

	final protected static int SHADOW_PADDING = 6;
	
	final protected static ResourceTexture SHADOW_TEXTURE = TextureManager.createResourceTexture(R.drawable.dropshadow_50, 32, 32);
	final protected static NineSliceMaterial SHADOW_MATERIAL = new NineSliceMaterial(SHADOW_TEXTURE, 12, 12, 20, 20);

	final private static ResourceTexture sBackgroundTex = TextureManager.createResourceTexture(R.drawable.controllers_background, 4, 4);
	final private static ResourceTexture sOutlinesTex = TextureManager.createResourceTexture(R.drawable.controllers_outlines, 64, 256);
	final private static ResourceTexture sTvScanlinesTex = TextureManager.createResourceTexture(R.drawable.controllers_tv_scanlines, 4, 4);

	final private static TiledMaterial sBackgroundMat = new TiledMaterial(sBackgroundTex);
	final private static NineSliceMaterial sOutlineMat = new NineSliceMaterial(sOutlinesTex, 0,9,52,58,  0,6,58,64);
	final private static NineSliceMaterial sOutlineSelectedMat = new NineSliceMaterial(sOutlinesTex, 0,16,48,64,	65,75,124,134);
	final private static TiledMaterial sTvScanlinesMat = new TiledMaterial(sTvScanlinesTex);

	final protected MaterialSprite mShadow;

	final protected MaterialSprite mBackground;
	//final protected MaterialSprite mMeter;
	final protected MaterialSprite mOutline;
	final protected MaterialSprite mOutlineSelected;
	final protected MaterialSprite mTvScanlines;

	protected String mName = null;

	protected boolean mHold = false;

	public MidiWidget(String name) {
		super();

		setName(name);

        mShadow = new MaterialSprite(SHADOW_MATERIAL);
        mShadow.x = -SHADOW_PADDING;
        mShadow.y = -SHADOW_PADDING;
        addSprite( mShadow );

		mBackground = new MaterialSprite(sBackgroundMat);

		mOutline = new MaterialSprite(sOutlineMat);

		mOutlineSelected = new MaterialSprite(sOutlineSelectedMat);
		mOutlineSelected.visible = false;

		mTvScanlines = new MaterialSprite(sTvScanlinesMat);
	}

	public void setName(String name) {
		mName = name;
	}
	public String getName() {
		return mName;
	}
/*
	@Override
	public String[] getParameters() {
		return mParameters;
	}
*/
/*
	@Override
	public int getNumControllers() {
		return mNumControllers;
	}
*/

	public void setSize(int w, int h) {
		super.setSize(w, h);

		mShadow.setSize(w + (SHADOW_PADDING*2), h + (SHADOW_PADDING*2));

		mBackground.setSize(w-2, h-2);

		mOutline.setSize(w, h);

        mOutlineSelected.setSize(w+6, h+6);

		mTvScanlines.setSize(w-4, h-4);
	}

	public void sendControlChange(int index, int value) {
		if (listener != null) {
			listener.onControlChange(this, index, value);
		}
	}

	public void sendNoteOn(int key, int velocity) {
		if (listener != null) {
			listener.onNoteOn(this, key, velocity);
		}
	}

	public void sendNoteOff(int key, int velocity) {
		if (listener != null) {
			listener.onNoteOff(this, key, velocity);
		}
	}

	@Override
	public void setHold(boolean hold) {
		mHold = hold;
		if (hold) {
			press(1.0f);
		} else {
			release(1.0f);
		}
	}

	public boolean isHolding() {
		return mHold;
	}
/*
	protected void press() {
		press(1.0f);		
	}

	protected void release() {
		release(1.0f);
	}
*/
	protected void press(float pressure) {
		
	}

	protected void release(float pressure) {
		
	}


	@Override
	public void onFocusChanged(boolean focus) {
		super.onFocusChanged(focus);

		if (focus) {
			mOutline.visible = false;
			mOutlineSelected.visible = true;
			//mShadow.x = -SHADOW_PADDING-1;
			//mShadow.y = -SHADOW_PADDING-1;
			//mShadow.setSize(width + (SHADOW_PADDING*2)+2, height + (SHADOW_PADDING*2)+2);
		} else {
			mOutline.visible = true;
			mOutlineSelected.visible = false;
			//mShadow.x = -SHADOW_PADDING;
			//mShadow.y = -SHADOW_PADDING;
			//mShadow.setSize(width + (SHADOW_PADDING*2), height + (SHADOW_PADDING*2));
		}
	}



	protected IOnControlChangeListener listener;

    public void setOnControlChangeListener(IOnControlChangeListener l) {
    	listener = l;
    }

}
