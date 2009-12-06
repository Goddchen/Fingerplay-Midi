package com.flat20.fingerplay.midicontrollers;

public interface IMidiController {
	//public String[] parameters = null;

	public void setName(String name);
	public String getName();
	// Names (and indices) of all parameters belonging to this controller
	public Parameter[] getParameters();

//	public int getNumControllers();
	public void sendControlChange(int index, int value);
	public void sendNoteOn(int key, int velocity);
	public void sendNoteOff(int key, int velocity);
	public void setOnControlChangeListener(IOnControlChangeListener l);
	public void setHold(boolean hold);
	public boolean isHolding();
}
