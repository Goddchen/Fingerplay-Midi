package com.flat20.gui.widgets;

import com.flat20.fingerplay.midicontrollers.IMidiController;
import com.flat20.gui.Materials;
import com.flat20.gui.sprites.MaterialSprite;

public class Slider extends DefaultMidiWidget {
/*
	final private static ResourceTexture sMeterTex = TextureManager.createResourceTexture(R.drawable.controllers_meter, 4, 4);
	final private static StretchedMaterial sMeterMat = new StretchedMaterial(sMeterTex);
	final private static ResourceTexture sMeterOffTex = TextureManager.createResourceTexture(R.drawable.controllers_meter_off, 4, 4);
	final private static StretchedMaterial sMeterOffMat = new StretchedMaterial(sMeterOffTex);
*/
	final protected MaterialSprite mMeter;
	final protected MaterialSprite mMeterOff;

	// IMidiController implementations

	final protected static int CC_TOUCH = 0;
	final protected static int CC_VALUE = 1;
/*
	final private static Parameter[] sParameters = {
			new Parameter(CC_TOUCH, "Press", Parameter.TYPE_CONTROL_CHANGE, true),
			new Parameter(CC_VALUE, "Vertical", Parameter.TYPE_CONTROL_CHANGE, true)};
*/
/*
	public Parameter[] getParameters() {
		return sParameters;
	}

	public Parameter getParameterById(int parameterId) {
		for (int i=0; i<sParameters.length; i++) {
			if (sParameters[i].id == parameterId)
				return sParameters[i];
		}
		return null;
	}
*/

	int lastValue = -1;

	public Slider(IMidiController midiController) {
		super(midiController);

		mMeter = new MaterialSprite(Materials.MC_INDICATOR);
		mMeterOff = new MaterialSprite(Materials.MC_INDICATOR_OFF);

		addSprite(mBackground);
		addSprite(mMeterOff);
		addSprite(mMeter);
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

		mMeter.setSize(w, h);
		mMeterOff.setSize(w, h);
	}

	
	
	@Override
	public boolean onTouchDown(int touchX, int touchY, float pressure, int pointerId) {
		press(1.0f);
		return true;
	}

	@Override
	public boolean onTouchMove(int touchX, int touchY, float pressure, int pointerId) {
		float dy = ((float)touchY / (float)height);
		int value = (int) Math.max(0, Math.min(dy * 0x7F, 0x7F));
		if (value != lastValue) {
			getMidiController().sendParameter(CC_VALUE, value);
			//sendControlChange(CC_VALUE, value);
			setMeterHeight( Math.max(0, Math.min(touchY, height)) );
			lastValue = value;
		}
		return true;
	}

	@Override
	public boolean onTouchUp(int touchX, int touchY, float pressure, int pointerId) {
		//if (!isHolding())
			release(1.0f);
		return true;
	}

	@Override
	public boolean onTouchUpOutside(int touchX, int touchY, float pressure, int pointerId) {
		//if (!isHolding())
			release(1.0f);
		return true;
	}

	@Override
	protected void press(float pressure) {

		getMidiController().sendParameter(CC_TOUCH, 0x7F);
/*
		if (sParameters[CC_TOUCH].type == Parameter.TYPE_CONTROL_CHANGE)
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
		if (sParameters[CC_TOUCH].type == Parameter.TYPE_CONTROL_CHANGE)
			sendControlChange(CC_TOUCH, 0x00);
		else
			sendNoteOff(CC_TOUCH,0x00);
*/
		mMeter.visible = false;
		mMeterOff.visible = true;
	}
 
	protected void setMeterHeight(int meterHeight) {
		mMeter.setSize(mMeter.width, meterHeight);
		mMeterOff.setSize(mMeterOff.width, meterHeight);
		/*
		meter.getGrid().updateVertice(2*3+1, height); //*3+1 means y coordinate.
		meter.getGrid().updateVertice(3*3+1, height);
		meterOff.getGrid().updateVertice(2*3+1, height); //*3+1 means y coordinate.
		meterOff.getGrid().updateVertice(3*3+1, height);
		*/
	}

}
