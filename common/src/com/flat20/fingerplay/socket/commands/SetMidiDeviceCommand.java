package com.flat20.fingerplay.socket.commands;

public class SetMidiDeviceCommand extends SocketStringCommand {

	public SetMidiDeviceCommand(String device) {
		super(SocketCommand.COMMAND_SET_MIDI_DEVICE, device);
	}
}
