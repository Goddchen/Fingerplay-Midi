package com.flat20.fingerplay.socket.commands;

import java.io.DataOutputStream;
import java.nio.ByteBuffer;

import com.flat20.fingerplay.socket.commands.midi.MidiSocketCommand;
import com.flat20.fingerplay.socket.commands.SocketStringCommand;

// Doesn't allocate anything but overwrites its internal buffer.
public class FingerWriter {

	final private DataOutputStream mOut;

	final private byte[] mData;
	final private ByteBuffer mDataBuffer;

	public FingerWriter(DataOutputStream out) {
		mOut = out;
		mData = new byte[0xFFFF];
		mDataBuffer = ByteBuffer.wrap(mData); // command, length
	}
/*
	public void write(SocketCommand socketCommand) throws Exception {
		write( encode(socketCommand) );
	}

	public void write(byte[] data) throws Exception {
		mOut.write( data );
	}
*/
	public void write(SocketCommand socketCommand) throws Exception {
		final int size;
		switch (socketCommand.command) {
			case SocketCommand.COMMAND_MIDI_SHORT_MESSAGE:
				size = encode((MidiSocketCommand) socketCommand);
				mOut.write( mData, 0, size );
				return;

			case SocketCommand.COMMAND_REQUEST_MIDI_DEVICE_LIST:
				size = encode((SocketCommand) socketCommand);
				mOut.write( mData, 0, size );
				return;

			case SocketCommand.COMMAND_SET_MIDI_DEVICE:
			case SocketCommand.COMMAND_VERSION:
			case SocketCommand.COMMAND_MIDI_DEVICE_LIST:
				size = encode((SocketStringCommand) socketCommand);
				mOut.write( mData, 0, size );
				return;
			
			default:
				throw new Exception("Can't encode the SocketCommand " + socketCommand);
		}/*
		// try catch ClassCastException
		try {
			return encode((MidiSocketCommand) socketCommand);
		} catch (ClassCastException e) {
			
		}
		try {
			return encode((SocketStringCommand) socketCommand);
		} catch (ClassCastException e) {
			
		}

		mData[0] = socketCommand.command;
		return mData;*/

		//throw new Exception("Can't encode the SocketCommand " + socketCommand);
	}

	private int encode(SocketStringCommand socketCommand) {
		final String message = socketCommand.message;
		final int size = message.length() + 1 + 4; // + byte + int

		//final ByteBuffer data = ByteBuffer.allocate( size ); // command, length
		final ByteBuffer data = mDataBuffer;
		data.rewind();
		data.put( socketCommand.command );
		data.putInt( size-5 ); // -1 without command & -4 without size
		data.put( message.getBytes() );

		return size;
	}

	private int encode(MidiSocketCommand socketCommand) {
		//final byte[] data = new byte[5];
		final int size = 5;
		final byte[] data = mData;
		data[0] = socketCommand.command;
		data[1] = (byte)socketCommand.midiCommand;
		data[2] = (byte)socketCommand.channel;
		data[3] = (byte)socketCommand.data1;
		data[4] = (byte)socketCommand.data2;
		return size;
	}

	private int encode(SocketCommand socketCommand) {
		mData[0] = socketCommand.command;
		return 1;
	}
}
