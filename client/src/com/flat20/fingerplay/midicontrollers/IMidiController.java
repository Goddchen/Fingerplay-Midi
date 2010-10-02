package com.flat20.fingerplay.midicontrollers;

import com.flat20.gui.widgets.MidiWidget;

public interface IMidiController {

	final public static int CONTROLLER_NUMBER_UNASSIGNED = -1;

	//public String[] parameters = null;

	public void setName(String name);
	public String getName();

	// Names (and indices) of all parameters belonging to this controller
	public Parameter[] getParameters();
	public void setParameters(Parameter[] parameters);
	//public Parameter getParameterById(int parameterId);

	// MidiControllerManager assigns this number to keep the cc messages separate.  
	//public void setControllerNumber(int number);
	//public int getControllerNumber();

	public void sendParameter(int parameterId, int value);
	/*
	public void sendControlChange(int controllerNumber, int value);
	public void sendNoteOn(int key, int velocity);
	public void sendNoteOff(int key, int velocity);
	*/
	public void setOnControlChangeListener(IOnControlChangeListener l);
	//public void setHold(boolean hold);
	//public boolean isHolding();

	public void setView(MidiWidget widget);
	public MidiWidget getView();
}
