package com.flat20.fingerplay.socket.commands;

public class SocketCommand {

	// Standard MIDI 4 byte message.
	public final static byte COMMAND_MIDI_SHORT_MESSAGE = 0x01;

	// Client tells the server which channel he'll be using.
	public final static byte COMMAND_SET_CONTROL_CHANGE_CHANNEL = 0x02;

	// Client asks for a list of MIDI devices.
	public final static byte COMMAND_REQUEST_MIDI_DEVICE_LIST = 0x03; 

	// Server sends the MIDI device list to the client.
	public final static byte COMMAND_MIDI_DEVICE_LIST = 0x04; 

	// Client sets the MIDI device used by the server.
	public final static byte COMMAND_SET_MIDI_DEVICE = 0x05; 

	// Client tells server if a control needs smoothing.
	public final static byte COMMAND_SET_SMOOTHING = 0x06; 

	// Client tells server if a control needs smoothing.
	public final static byte COMMAND_VERSION = 0x07; 

	public byte command;
	//public byte[] data; //First byte contains the command;

	public SocketCommand() {
		
	}

	public SocketCommand(byte command) {
		this.command = command;
	}
/*
	// Create a command from incoming data.
	public SocketCommand(byte[] data) {
		this.command = data[0];
		this.data = data;
	}

	// Create a command to send.
	public SocketCommand(byte command, byte[] data) {
		this.command = command;
		this.data = data;
		this.data[0] = command;
	}

	public SocketCommand(byte command) {
		this.command = command;
		this.data = new byte[1];
		this.data[0] = this.command;
	}
*/
	/*
	public void encode() {
	}*/
/*
	public byte[] copyParameters() {
		byte[] parameters = new byte[data.length-1];
		System.arraycopy(data, 1, parameters, 0, data.length-1);
		return parameters;
	}

	public byte[] copyParameters(int length) {
		byte[] parameters = new byte[length-1];
		System.arraycopy(data, 1, parameters, 0, length-1);
		return parameters;
	}

	public String getParametersAsString(int dataLength) {
		byte[] parameters = copyParameters(dataLength);
		return new String(parameters);
	}
*/
}
