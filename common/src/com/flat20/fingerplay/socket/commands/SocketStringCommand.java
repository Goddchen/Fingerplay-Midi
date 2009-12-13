package com.flat20.fingerplay.socket.commands;

import com.flat20.fingerplay.socket.commands.SocketCommand;

public class SocketStringCommand extends SocketCommand {
/*
	// Adds a 0 at the end of string.
	public SocketStringCommand(byte command, String parameter) {
		super(command);
		data = new byte[parameter.length()+2]; // One for command and one for 0 at end
		byte[] deviceListBytes = parameter.getBytes();
		System.arraycopy(deviceListBytes, 0, data, 1, parameter.length());
		data[0] = command;
		data[data.length-1] = 0;
	}
*/
	public String message = null;

	public SocketStringCommand() {
		super();
	}

	public SocketStringCommand(byte command) {
		super(command);
	}
/*
	public static SocketStringCommand createRequestDeviceListCommand() {
		// only keep SocketStringCommand
		// and MidiControlchangeSocketcommand?
		// code will look weird?
	}
*/
	public SocketStringCommand(byte command, String message) {
		super(command);
		this.message = message;
	}
/*
	public SocketStringCommand(byte command, int length, ByteBuffer input) {
		decode(command, length, input);
	}
*/
	/*
	public void encode() {
		int size = message.length() + 1 + 4; // + byte + int
		ByteBuffer buffer = ByteBuffer.allocate( size ); // command, length
		buffer.put( command );
		buffer.putInt( size-5 ); // -1 without command & -4 without size
		buffer.put( message.getBytes() );

		this.data = buffer.array();
	}*/
/*
	public void decode(byte command, int length, ByteBuffer input) {
		this.command = command;
		byte[] data = new byte[input.remaining()];
		input.get(data);
		message = new String(data);
	}
*/
	/*
	private void decode(byte command, int length, byte[] message) {
		this.command = command;
		//byte[] data = new byte[input.remaining()];
		//input.get(data);
		this.message = new String(message, 0, length);
	}*/

/*
	public static ByteBuffer encode(byte command, String text) {
		int size = text.length() + 1 + 4;
		ByteBuffer buffer = ByteBuffer.allocate( size ); // command, length
		buffer.put( command );
		buffer.putInt( size-5 ); // -1 without command & -4 without size
		buffer.put( text.getBytes() );
		//buffer.put( (byte)0 );
		System.out.println( text.getBytes()[0] + ", " + buffer.position() + ", " + buffer.limit() + ", " + buffer.remaining());
		return buffer;
	}

	public static void decode(byte command, int length, ByteBuffer input) {
		System.out.println(command + ", " + length + ", " + input.remaining());
		byte[] data = new byte[input.remaining()];
		input.get(data);
		//System.out.println(input.get());
		String text = new String(data);
		System.out.println(text);
	}
*/
}
