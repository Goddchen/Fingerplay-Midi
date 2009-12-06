package com.flat20.fingerplay.socket.commands;

public class MidiNoteOffSocketCommand extends MidiSocketCommand {
	public MidiNoteOffSocketCommand(int channel, int key, int velocity) {
		super((byte)(0x80), (byte) (channel & 0xFF), (byte) (key & 0xFF), (byte) (velocity & 0xFF));
	}
}
