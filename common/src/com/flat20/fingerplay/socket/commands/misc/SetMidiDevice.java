package com.flat20.fingerplay.socket.commands.misc;

import com.flat20.fingerplay.socket.commands.SocketCommand;
import com.flat20.fingerplay.socket.commands.SocketStringCommand;

public class SetMidiDevice extends SocketStringCommand {

	public SetMidiDevice(String device) {
		super(SocketCommand.COMMAND_SET_MIDI_DEVICE, device);
	}
}
