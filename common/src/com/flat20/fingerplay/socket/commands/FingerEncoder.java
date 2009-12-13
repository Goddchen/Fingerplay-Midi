package com.flat20.fingerplay.socket.commands;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;

import com.flat20.fingerplay.socket.commands.midi.MidiSocketCommand;
import com.flat20.fingerplay.socket.commands.SocketStringCommand;

public class FingerEncoder {

	final private static byte[] sData = new byte[ 0xFFFF ];
	final private static ByteBuffer sDataBuffer = ByteBuffer.wrap(sData);

	public static byte[] encode(SocketCommand socketCommand) throws Exception {
		// try catch ClassCastException
		try {
			return encode((MidiSocketCommand) socketCommand);
		} catch (ClassCastException e) {
			
		}
		try {
			return encode((SocketStringCommand) socketCommand);
		} catch (ClassCastException e) {
			
		}

		if (socketCommand instanceof SocketCommand)
			return new byte[] {socketCommand.command}; // TODO Remove. no command should be SocketCommand
		else
			throw new Exception("Can't encode the SocketCommand " + socketCommand);
	}

	public static byte[] encode(SocketStringCommand socketCommand) {
		final String message = socketCommand.message;
		final int size = message.length() + 1 + 4; // + byte + int

		final ByteBuffer data = ByteBuffer.allocate( size ); // command, length
		data.put( socketCommand.command );
		data.putInt( size-5 ); // -1 without command & -4 without size
		data.put( message.getBytes() );

		return data.array();
	}

	public static byte[] encode(MidiSocketCommand socketCommand) {
		final byte[] data = new byte[5];
		data[0] = socketCommand.command;
		data[1] = (byte)socketCommand.midiCommand;
		data[2] = (byte)socketCommand.channel;
		data[3] = (byte)socketCommand.data1;
		data[4] = (byte)socketCommand.data2;
		return data;
	}


	public static void decode(SocketStringCommand socketCommand, byte command, int length, byte[] message) {
		socketCommand.command = command;
		socketCommand.message = new String(message, 0, length);
	}

	public static void decode(SocketStringCommand socketCommand, byte command, DataInputStream in) throws Exception {
		try {
			// Read until we have an int.
			int textLength = in.readInt(); // wait forever?
			if (textLength == -1)
				throw new SocketException("Disconnected");

			//byte[] data = new byte[textLength];
			int numRead = in.read(sData, 0, textLength); // wait forever?
			if (numRead == -1)
				throw new SocketException("Disconnected");

			if (numRead != textLength)
				throw new Exception("Incorrect length for SocketStringCommand " + socketCommand.command);

			decode(socketCommand, command, textLength, sData);

		} catch (IOException e) {
			throw new Exception("Couldn't parse SocketStringCommand " + socketCommand.command);
		}
	}

	public static void decode(MidiSocketCommand socketCommand, byte command, DataInputStream in) {
		try {
			socketCommand.command = command;
			// if command == control change + 0xB0 ??
			socketCommand.set(in.readByte(), in.readByte(), in.readByte(), in.readByte());
			//socketCommand.midiCommand = (in.readByte() & 0xFF);//buffer[index + 1] & 0xFF; // Make it unsigned.
			//socketCommand.channel = (byte) (in.readByte() & 0xFF);//buffer[index + 2] & 0xFF;
			//socketCommand.data1 = (byte) (in.readByte() & 0xFF);//buffer[index + 3] & 0xFF;
			//socketCommand.data2 = (byte) (in.readByte() & 0xFF);//buffer[index + 4] & 0xFF;
		} catch (IOException e) {
			throw new Error("Couldn't parse MidiSocketCommand " + socketCommand.command);
		}
	}

}
