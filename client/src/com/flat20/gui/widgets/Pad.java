package com.flat20.gui.widgets;

import com.flat20.fingerplay.midicontrollers.IMidiController;
import com.flat20.gui.Materials;
import com.flat20.gui.sprites.MaterialSprite;

public class Pad extends MidiWidget {

	// A dropshadow from DefaultMidiWidget.
	final protected static int BACKGROUND_PADDING = 6;
	final protected MaterialSprite mBackground;

	final private MaterialSprite mDefault;
	final private MaterialSprite mClicked;


	// IMidiController implementations and variables.

	final private static int CC_TOUCH = 0;

	//protected Parameter[] mParameters = {new Parameter(CC_TOUCH, 0, "Press", Parameter.TYPE_NOTE, false)};
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
	public Pad(IMidiController midiController) {
		super(midiController);

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
	public boolean onTouchDown(int touchX, int touchY, float pressure, int pointerId) {
		press(pressure);
		//sendNoteOn(0, Math.min(0x7F, Math.round(0x7F * (pressure*3))));
		//mMeter.visible = true;
		return true;
	}



	@Override
	public boolean onTouchUp(int touchX, int touchY, float pressure, int pointerId) {
		release(pressure);
		//sendNoteOff(CC_TOUCH, Math.min(0x7F, Math.round(0x7F * (pressure*3))));
		//mMeter.visible = false;
		return true;
	}

	@Override
	public boolean onTouchUpOutside(int touchX, int touchY, float pressure, int pointerId) {
		release(pressure);
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

		mClicked.visible = true;
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

		mClicked.visible = false;
	}

}
