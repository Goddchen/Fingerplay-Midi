package com.flat20.gui.widgets;

import com.flat20.fingerplay.midicontrollers.IMidiController;
import com.flat20.fingerplay.midicontrollers.Parameter;
import com.flat20.gui.Materials;
import com.flat20.gui.sprites.MaterialSprite;

public class XYPad extends DefaultMidiWidget implements IMidiController {
/*
	final private static ResourceTexture sMeterTex = TextureManager.createResourceTexture(R.drawable.touchpad_meter, 32, 32);
	final private static StretchedMaterial sMeterMat = new StretchedMaterial(sMeterTex);
	final private static ResourceTexture sMeterOffTex = TextureManager.createResourceTexture(R.drawable.touchpad_meter_off, 32, 32);
	final private static StretchedMaterial sMeterOffMat = new StretchedMaterial(sMeterOffTex);
*/
	final protected MaterialSprite mMeter;
	final protected MaterialSprite mMeterOff;

	// IMidiController implementations and variables.

	final protected Parameter[] mParameters = {
			new Parameter(CC_TOUCH, "Press", Parameter.TYPE_CONTROL_CHANGE, true),
			new Parameter(CC_X, "Horizontal", Parameter.TYPE_CONTROL_CHANGE, true),
			new Parameter(CC_Y, "Vertical", Parameter.TYPE_CONTROL_CHANGE, true)
			};

	private static final int CC_TOUCH = 0;
	private static final int CC_X = 1;
	private static final int CC_Y = 2;

	public Parameter[] getParameters() {
		return mParameters;
	}


	int lastValueX = -1;
	int lastValueY = -1;

	public XYPad(String name) {
		super(name);

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
	public boolean onTouchDown(int touchX, int touchY, float pressure) {
		press(pressure);
		return true;
	}

	@Override
	public boolean onTouchMove(int touchX, int touchY, float pressure) {
		int valueX = (int) Math.max(0, Math.min(0x7F, ((float) touchX / width) * 0x7F) );
		int valueY = (int) Math.max(0, Math.min(0x7F, ((float) touchY / height) * 0x7F) );
		if (valueX != lastValueX) {
			sendControlChange(CC_X, valueX );
			mMeter.x = Math.max(0, Math.min(width-32, touchX-16));
			mMeterOff.x = mMeter.x;
			lastValueX = valueX;
		}
		if (valueY != lastValueY) {
			sendControlChange(CC_Y, valueY );
			mMeter.y = Math.max(0, Math.min(height-32, touchY-16));
			mMeterOff.y = mMeter.y;
			lastValueY = valueY;
		}
		return true;
	}

	@Override
	public boolean onTouchUp(int touchX, int touchY, float pressure) {
		if (!isHolding())
			release(pressure);
		return true;
	}

	@Override
	public boolean onTouchUpOutside(int touchX, int touchY, float pressure) {
		if (!isHolding())
			release(pressure);
		return true;
	}

	@Override
	protected void press(float pressure) {
		sendControlChange(CC_TOUCH, 0x7F);
		mMeter.visible = true;
		mMeterOff.visible = false;
	}

	@Override
	protected void release(float pressure) {
		sendControlChange(CC_TOUCH, 0x00);
		mMeter.visible = false;
		mMeterOff.visible = true;
	}

}
