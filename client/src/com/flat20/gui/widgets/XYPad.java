package com.flat20.gui.widgets;

import com.flat20.fingerplay.midicontrollers.IMidiController;
import com.flat20.gui.Materials;
import com.flat20.gui.sprites.MaterialSprite;

public class XYPad extends DefaultMidiWidget {

	final protected MaterialSprite mMeter;
	final protected MaterialSprite mMeterOff;

	// IMidiController implementations and variables.
/*
	final protected Parameter[] mParameters = {
			new Parameter(CC_TOUCH, "Press", Parameter.TYPE_CONTROL_CHANGE, true),
			new Parameter(CC_X, "Horizontal", Parameter.TYPE_CONTROL_CHANGE, true),
			new Parameter(CC_Y, "Vertical", Parameter.TYPE_CONTROL_CHANGE, true)
			};
*/
	protected static final int CC_TOUCH = 0;
	protected static final int CC_X = 1;
	protected static final int CC_Y = 2;
/*
	public Parameter[] getParameters() {
		return mParameters;
	}

	public Parameter getParameterById(int parameterId) {
		for (int i=0; i<mParameters.length; i++) {
			if (mParameters[i].id == parameterId)
				return mParameters[i];
		}
		return null;
	}
*/
	int lastValueX = -1;
	int lastValueY = -1;

	public XYPad(IMidiController midiController) {
		super(midiController);

		mMeter = new MaterialSprite(Materials.MC_XYPAD_INDICATOR);
		mMeterOff = new MaterialSprite(Materials.MC_XYPAD_INDICATOR_OFF);

		addSprite(mBackground);
		addSprite(mMeter);
		addSprite(mMeterOff);
		addSprite(mOutline);
		addSprite(mOutlineSelected);
		addSprite(mTvScanlines);

        //mBackground.x = 1;
		//mBackground.y = 1;

		mMeter.visible = false;

        mOutlineSelected.x = -3;
        mOutlineSelected.y = -3;

		mTvScanlines.x = 2;
		mTvScanlines.y = 2;

		setSize(32, 32);
	}

	public void setSize(int w, int h) {
		super.setSize(w, h);

		//mMeter.setSize(w, h);
		//mMeterOff.setSize(w, h);

	}

	@Override
	public boolean onTouchDown(int touchX, int touchY, float pressure, int pointerId) {
		press(pressure);
		return true;
	}

	@Override
	public boolean onTouchMove(int touchX, int touchY, float pressure, int pointerId) {
		int valueX = (int) Math.max(0, Math.min(0x7F, ((float) touchX / width) * 0x7F) );
		int valueY = (int) Math.max(0, Math.min(0x7F, ((float) touchY / height) * 0x7F) );
		if (valueX != lastValueX) {
			getMidiController().sendParameter(CC_X, valueX);
			//sendControlChange(CC_X, valueX );
			mMeter.x = Math.max(0, Math.min(width-32, touchX-16));
			mMeterOff.x = mMeter.x;
			lastValueX = valueX;
		}
		if (valueY != lastValueY) {
			getMidiController().sendParameter(CC_Y, valueY);
			//sendControlChange(CC_Y, valueY );
			mMeter.y = Math.max(0, Math.min(height-32, touchY-16));
			mMeterOff.y = mMeter.y;
			lastValueY = valueY;
		}
		return true;
	}

	@Override
	public boolean onTouchUp(int touchX, int touchY, float pressure, int pointerId) {
		//if (!isHolding())
			release(pressure);
		return true;
	}

	@Override
	public boolean onTouchUpOutside(int touchX, int touchY, float pressure, int pointerId) {
		//if (!isHolding())
			release(pressure);
		return true;
	}

	@Override
	protected void press(float pressure) {
		getMidiController().sendParameter(CC_TOUCH, 0x7F);
/*
		if (mParameters[CC_TOUCH].type == Parameter.TYPE_CONTROL_CHANGE)
			sendControlChange(CC_TOUCH, 0x7F);
		else
			sendNoteOn(CC_TOUCH, Math.min(0x7F, Math.round(0x7F * (pressure*3))));
*/
		mMeter.visible = true;
		mMeterOff.visible = false;
	}

	@Override
	protected void release(float pressure) {
		getMidiController().sendParameter(CC_TOUCH, 0x00);
/*
		if (mParameters[CC_TOUCH].type == Parameter.TYPE_CONTROL_CHANGE)
			sendControlChange(CC_TOUCH, 0x00);
		else
			sendNoteOff(CC_TOUCH, 0x00);
*/
		mMeter.visible = false;
		mMeterOff.visible = true;
	}

}
