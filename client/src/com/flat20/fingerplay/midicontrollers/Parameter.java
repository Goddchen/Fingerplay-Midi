package com.flat20.fingerplay.midicontrollers;

public class Parameter {

	// Send as MIDI control change or note on. 
	final public static int TYPE_CONTROL_CHANGE = 1;
	final public static int TYPE_NOTE = 2;
	final public static int TYPE_NOTE_ON = 3;
	final public static int TYPE_NOTE_OFF = 4;

	final public int id;
	public String name;
	public int type;
	final public boolean visible;
	public int channel;
	public int controllerNumber; // or key

	public Parameter(int id, int channel, int controllerNumber, String name, int type, boolean visible) {
		this.id = id;
		this.channel = channel;
		this.controllerNumber = controllerNumber;
		this.name = name;
		this.type = type;
		this.visible = visible;
	}

	public Parameter clone() {
		return new Parameter(id, channel, controllerNumber, name, type, visible);
	}

	public String toString() {
		return "Parameter id: " + id + ", channel: " + channel + ", controllerNumber: " + controllerNumber + ", name: " + name + ", type: " + type + ", visible: " + visible;
	}
}
