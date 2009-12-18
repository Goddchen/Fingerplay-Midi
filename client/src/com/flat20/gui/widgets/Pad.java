package com.flat20.gui.widgets;

import com.flat20.fingerplay.midicontrollers.IMidiController;
import com.flat20.fingerplay.midicontrollers.Parameter;
import com.flat20.gui.Materials;
import com.flat20.gui.sprites.MaterialSprite;

public class Pad extends MidiWidget implements IMidiController {

	//final private static ResourceTexture sMeterTex = TextureManager.createResourceTexture(R.drawable.controllers_meter, 4, 4);
	//final private static StretchedMaterial sMeterMat = new StretchedMaterial(sMeterTex);
	//final private static ResourceTexture sMeterOffTex = TextureManager.createResourceTexture(R.drawable.controllers_meter_off, 4, 4);
	//final private static StretchedMaterial sMeterOffMat = new StretchedMaterial(sMeterOffTex);

	final protected MaterialSprite mMeter;
	//final protected MaterialSprite mMeterOff;

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

		mMeter = new MaterialSprite(Materials.MC_METER);
		//mMeterOff = new MaterialSprite(sMeterOffMat);

		addSprite(mBackground);
		addSprite(mMeter);
		addSprite(mOutline);
		addSprite(mOutlineSelected);
		//addSprite(mTvScanlines);

		mBackground.x = 1;
		mBackground.y = 1;

		mMeter.visible = false;

        mOutlineSelected.x = -3;
        mOutlineSelected.y = -3;

		mTvScanlines.x = 2;
		mTvScanlines.y = 2;

		setSize(32, 32);
	}

	public void setSize(int w, int h) {
		super.setSize(w, h);

		mMeter.setSize(w-2, h-2);
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
		mMeter.visible = true;
	}
	
	@Override
	protected void release(float pressure) {
		sendNoteOff(CC_TOUCH, Math.min(0x7F, Math.round(0x7F * (pressure*3))));
		mMeter.visible = false;
	}

}
