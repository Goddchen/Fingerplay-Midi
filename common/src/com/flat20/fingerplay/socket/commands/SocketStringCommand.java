package com.flat20.fingerplay.socket.commands;

public class SocketStringCommand extends SocketCommand {

	// Adds a 0 at the end of string.
	public SocketStringCommand(byte command, String parameter) {
		super(command);
		data = new byte[parameter.length()+2]; // One for command and one for 0 at end
		byte[] deviceListBytes = parameter.getBytes();
		System.arraycopy(deviceListBytes, 0, data, 1, parameter.length());
		data[0] = command;
		data[data.length-1] = 0;
	}
}
