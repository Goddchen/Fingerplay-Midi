package com.flat20.fingerplay.socket.commands.misc;

import com.flat20.fingerplay.socket.commands.SocketCommand;
import com.flat20.fingerplay.socket.commands.SocketStringCommand;

public class DeviceList extends SocketStringCommand {

	public DeviceList() {
		super(SocketCommand.COMMAND_MIDI_DEVICE_LIST);
	}

	public DeviceList(String deviceList) {
		super(SocketCommand.COMMAND_MIDI_DEVICE_LIST, deviceList);
	}
}
