package com.flat20.gui.widgets;

import com.flat20.fingerplay.midicontrollers.IMidiController;
import com.flat20.fingerplay.midicontrollers.IOnControlChangeListener;

public abstract class MidiWidget extends Widget implements IMidiController {

	protected String mName = null;

	// Unique index for each controller. Assigned from MidiControllerManager
	// when the controller is added to its list. 
	private int mControllerNumber = CONTROLLER_NUMBER_UNASSIGNED;

	protected boolean mHold = false;

	public MidiWidget(String name, int controllerNumber) {
		super();

		setName(name);
		setControllerNumber(controllerNumber);
	}

	public void setName(String name) {
		mName = name;
	}
	public String getName() {
		return mName;
	}


	@Override
	public void setControllerNumber(int number) {
		mControllerNumber = number;
	}

	@Override
	public int getControllerNumber() {
		return mControllerNumber;
	}

	public void sendControlChange(int index, int value) {
		if (listener != null) {
			listener.onControlChange(this, mControllerNumber + index, value);
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

	protected void press(float pressure) {
		
	}

	protected void release(float pressure) {
		
	}



	protected IOnControlChangeListener listener;

    public void setOnControlChangeListener(IOnControlChangeListener l) {
    	listener = l;
    }

}
