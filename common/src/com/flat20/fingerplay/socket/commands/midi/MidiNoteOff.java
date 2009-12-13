package com.flat20.fingerplay.socket.commands.midi;

public class MidiNoteOff extends MidiSocketCommand {

	public MidiNoteOff() {
		super();
	}

	public MidiNoteOff(int channel, int key, int velocity) {
		super(0x80, channel, key, velocity);
	}

	public void set(int channel, int key, int velocity) {
		super.set(0x80, channel, key, velocity);
	}

}
