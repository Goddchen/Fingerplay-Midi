package com.flat20.fingerplay.socket.commands.misc;

import com.flat20.fingerplay.socket.commands.SocketCommand;

public class RequestMidiDeviceList extends SocketCommand {
	public RequestMidiDeviceList() {
		super(SocketCommand.COMMAND_REQUEST_MIDI_DEVICE_LIST);
	}
}
