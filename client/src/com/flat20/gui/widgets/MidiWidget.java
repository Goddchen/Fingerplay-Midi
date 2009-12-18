package com.flat20.gui.widgets;

import com.flat20.fingerplay.midicontrollers.IMidiController;
import com.flat20.fingerplay.midicontrollers.IOnControlChangeListener;
import com.flat20.gui.Materials;
import com.flat20.gui.sprites.MaterialSprite;

public abstract class MidiWidget extends Widget implements IMidiController {

	final protected static int SHADOW_PADDING = 6;

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

        mShadow = new MaterialSprite(Materials.SHADOW);
        mShadow.x = -SHADOW_PADDING;
        mShadow.y = -SHADOW_PADDING;
        addSprite( mShadow );

		mBackground = new MaterialSprite(Materials.MC_BACKGROUND);

		mOutline = new MaterialSprite(Materials.MC_OUTLINE);

		mOutlineSelected = new MaterialSprite(Materials.MC_OUTLINE_SELECTED);
		mOutlineSelected.visible = false;

		mTvScanlines = new MaterialSprite(Materials.MC_TVSCANLINES);
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
