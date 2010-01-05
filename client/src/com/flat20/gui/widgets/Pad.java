package com.flat20.gui.widgets;

import com.flat20.fingerplay.midicontrollers.IMidiController;
import com.flat20.fingerplay.midicontrollers.Parameter;
import com.flat20.gui.Materials;
import com.flat20.gui.sprites.MaterialSprite;

public class Pad extends MidiWidget implements IMidiController {

	// A dropshadow from DefaultMidiWidget.
	final protected static int BACKGROUND_PADDING = 6;
	final protected MaterialSprite mBackground;

	final private MaterialSprite mDefault;
	final private MaterialSprite mClicked;


	// IMidiController implementations and variables.

	final private static int CC_TOUCH = 0;

	final protected static Parameter[] sParameters = {new Parameter(0, "Press", Parameter.TYPE_NOTE, false)};

	public Parameter[] getParameters() {
		return sParameters;
	}


	// are we pushing down
	//protected boolean mPressed = false;

	// is button turned on/off
	//protected boolean mActive = false;

	public Pad(String name) {
		super(name);

		mBackground = new MaterialSprite(Materials.MC_BACKGROUND);
        mBackground.x = -BACKGROUND_PADDING;
        mBackground.y = -BACKGROUND_PADDING;
        addSprite( mBackground );

		mDefault = new MaterialSprite(Materials.BUTTON_GREY, width, height);
		addSprite(mDefault);

		mClicked = new MaterialSprite(Materials.BUTTON_GREY_HIGHLIGHT, width, height);
		mClicked.visible = false;
		addSprite(mClicked);

		setSize(32, 32);
	}

	public void setSize(int w, int h) {
		super.setSize(w, h);

		mBackground.setSize(w + (BACKGROUND_PADDING*2), h + (BACKGROUND_PADDING*2));

		mDefault.setSize(w, h);
		mClicked.setSize(w, h);
	}

	@Override
	public boolean onTouchDown(int touchX, int touchY, float pressure) {
		press(pressure);
		//sendNoteOn(0, Math.min(0x7F, Math.round(0x7F * (pressure*3))));
		//mMeter.visible = true;
		return true;
	}



	@Override
	public boolean onTouchUp(int touchX, int touchY, float pressure) {
		release(pressure);
		//sendNoteOff(CC_TOUCH, Math.min(0x7F, Math.round(0x7F * (pressure*3))));
		//mMeter.visible = false;
		return true;
	}

	@Override
	public boolean onTouchUpOutside(int touchX, int touchY, float pressure) {
		release(pressure);
		return true;
	}

	/*
	private void setSelected(boolean selected) {
		if (selected) {
			sendControlChange(CC_TOUCH, 0x7F);
			meter.visible = true;
			mActive = true;
		} else {
			sendControlChange(CC_TOUCH, 0x00);
			meter.visible = false;
			mActive = false;
		}
	}*/

	@Override
	protected void press(float pressure) {
		sendNoteOn(0, Math.min(0x7F, Math.round(0x7F * (pressure*3))));
		mClicked.visible = true;
	}
	
	@Override
	protected void release(float pressure) {
		sendNoteOff(CC_TOUCH, Math.min(0x7F, Math.round(0x7F * (pressure*3))));
		mClicked.visible = false;
	}

}
