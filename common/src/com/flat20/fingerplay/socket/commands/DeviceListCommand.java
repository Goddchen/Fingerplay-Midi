package com.flat20.fingerplay.socket.commands;

public class DeviceListCommand extends SocketStringCommand {

	public DeviceListCommand(String deviceList) {
		super(SocketCommand.COMMAND_MIDI_DEVICE_LIST, deviceList);
	}
}
