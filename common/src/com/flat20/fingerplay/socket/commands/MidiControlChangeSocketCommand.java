package com.flat20.fingerplay.socket.commands;

public class MidiControlChangeSocketCommand extends MidiSocketCommand {


	public MidiControlChangeSocketCommand(int control1, int channel, int control2, int value) {
		super((byte)((0xB0 + control1) & 0xFF), (byte) (channel & 0xFF), (byte) (control2 & 0xFF), (byte) (value & 0xFF));
	}
 
}
