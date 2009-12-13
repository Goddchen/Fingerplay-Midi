package com.flat20.fingerplay.socket.commands.midi;

public class MidiNoteOn extends MidiSocketCommand {

	public MidiNoteOn() {
		super();
	}

	public MidiNoteOn(int channel, int key, int velocity) {
		//super((byte)(0x90), (byte) (channel & 0xFF), (byte) (key & 0xFF), (byte) (velocity & 0xFF));
		super(0x90, channel, key, velocity);
	}

	public void set(int channel, int key, int velocity) {
		super.set(0x90, channel, key, velocity);
		/*
		this.midiCommand = (byte)(0x90);
		this.channel = (byte) (channel & 0xFF);
		this.data1 = (byte) (key & 0xFF);
		this.data2 = (byte) (velocity & 0xFF);
		*/
	}

}
