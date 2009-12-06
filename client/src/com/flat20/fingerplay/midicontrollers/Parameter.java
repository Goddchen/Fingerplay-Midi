package com.flat20.fingerplay.midicontrollers;

public class Parameter {

	// Send as MIDI control change or note on. 
	final public static int TYPE_CONTROL_CHANGE = 1;
	final public static int TYPE_NOTE = 1;

	final public int id;
	final public String name;
	final public int type;
	final public boolean visible;

	public Parameter(int id, String name, int type, boolean visible) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.visible = visible;
	}
}
