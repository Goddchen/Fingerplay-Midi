package com.flat20.fingerplay.socket.commands;

public class MidiSocketCommand extends SocketCommand {
	/*
	public int command;
	public int channel;
	public int data1;
	public int data2;
	*/


	public MidiSocketCommand(byte command, byte channel, byte data1, byte data2) {
		super(SocketCommand.COMMAND_MIDI_SHORT_MESSAGE, new byte[5]);
		data[1] = command;
		data[2] = channel;
		data[3] = data1;
		data[4] = data2;
		/*
		this.command = command;
		this.channel = channel;
		this.data1 = data1;
		this.data2 = data2;
		*/
	}
}
