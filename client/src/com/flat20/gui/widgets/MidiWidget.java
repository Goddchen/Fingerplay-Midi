package com.flat20.gui.widgets;

import com.flat20.fingerplay.midicontrollers.IMidiController;
import com.flat20.fingerplay.midicontrollers.IOnControlChangeListener;

/**
 * IMidiController and AbstractMidiController deals with sending
 * the data now. Plan is to make MidiWidgets only deal mostly with
 * UI stuff.
 * 
 * @author andreas
 *
 */
public abstract class MidiWidget extends Widget { //implements IMidiController {

	final private IMidiController mMidiController;
	
	protected boolean mHold = false;

	public MidiWidget(IMidiController midiController) {
		super();

		mMidiController = midiController;
		mMidiController.setView(this);

		//setName(name);
		//setControllerNumber(controllerNumber);
	}
	
	public IMidiController getMidiController() {
		return mMidiController;
	}


	// Subclasses decide what to do with these.
	protected void press(float pressure) {
		
	}

	protected void release(float pressure) {
		
	}



	protected IOnControlChangeListener listener;

    public void setOnControlChangeListener(IOnControlChangeListener l) {
    	listener = l;
    }

    

}
