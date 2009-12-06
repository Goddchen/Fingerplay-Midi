package com.flat20.fingerplay.socket.commands;

public class MidiNoteOnSocketCommand extends MidiSocketCommand {
	public MidiNoteOnSocketCommand(int channel, int key, int velocity) {
		super((byte)(0x90), (byte) (channel & 0xFF), (byte) (key & 0xFF), (byte) (velocity & 0xFF));
	}
}
