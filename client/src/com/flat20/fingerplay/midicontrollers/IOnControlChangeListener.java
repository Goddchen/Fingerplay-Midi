package com.flat20.fingerplay.midicontrollers;

public interface IOnControlChangeListener {
	public void onControlChange(IMidiController midiController, int index, int value);
	public void onNoteOn(IMidiController midiController, int key, int velocity);
	public void onNoteOff(IMidiController midiController, int key, int velocity);
}
