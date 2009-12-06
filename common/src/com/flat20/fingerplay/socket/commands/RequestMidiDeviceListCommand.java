package com.flat20.fingerplay.socket.commands;

public class RequestMidiDeviceListCommand extends SocketCommand {
	public RequestMidiDeviceListCommand() {
		super(SocketCommand.COMMAND_REQUEST_MIDI_DEVICE_LIST);
	}
}
