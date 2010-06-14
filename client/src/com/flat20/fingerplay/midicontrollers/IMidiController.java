package com.flat20.fingerplay.midicontrollers;

public interface IMidiController {

	final public static int CONTROLLER_NUMBER_UNASSIGNED = -1;

	//public String[] parameters = null;

	public void setName(String name);
	public String getName();
	// Names (and indices) of all parameters belonging to this controller
	public Parameter[] getParameters();

	// MidiControllerManager assigns this number to keep the cc messages separate.  
	public void setControllerNumber(int number);
	public int getControllerNumber();

	public void sendControlChange(int controllerNumber, int value);
	public void sendNoteOn(int key, int velocity);
	public void sendNoteOff(int key, int velocity);
	public void setOnControlChangeListener(IOnControlChangeListener l);
	public void setHold(boolean hold);
	public boolean isHolding();
}
