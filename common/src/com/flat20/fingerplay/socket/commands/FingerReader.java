package com.flat20.fingerplay.socket.commands;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.SocketException;

import com.flat20.fingerplay.socket.commands.midi.MidiSocketCommand;
import com.flat20.fingerplay.socket.commands.misc.DeviceList;
import com.flat20.fingerplay.socket.commands.misc.RequestMidiDeviceList;
import com.flat20.fingerplay.socket.commands.misc.SetMidiDevice;
import com.flat20.fingerplay.socket.commands.misc.Version;

public class FingerReader {

	final private DataInputStream mIn;
	final private IReceiver mReceiver;

	final private static byte[] sData = new byte[ 0xFFFF ];

	final private MidiSocketCommand sMss = new MidiSocketCommand();

	final private DeviceList sDl = new DeviceList();
	final private RequestMidiDeviceList sRmdl = new RequestMidiDeviceList();
	final private SetMidiDevice sMd = new SetMidiDevice();
	final private Version sV = new Version();

	public FingerReader(DataInputStream in, IReceiver receiver) {
		mIn = in;
		mReceiver = receiver;
	}

	public byte readCommand() throws Exception {

		byte command = mIn.readByte();

		// commands[COMMAND_ID] = SocketCommand ?
		switch (command) {
			case SocketCommand.COMMAND_MIDI_SHORT_MESSAGE:
				mReceiver.onMidiSocketCommand( decode(sMss, command) );
				return command;
			
			case SocketCommand.COMMAND_REQUEST_MIDI_DEVICE_LIST:
				mReceiver.onRequestMidiDeviceList( (RequestMidiDeviceList) decode(sRmdl, command) );
				return command;
			
			case SocketCommand.COMMAND_MIDI_DEVICE_LIST:
				mReceiver.onDeviceList( (DeviceList) decode(sDl, command) );
				return command;
			
			case SocketCommand.COMMAND_SET_MIDI_DEVICE:
				mReceiver.onSetMidiDevice( (SetMidiDevice) decode(sMd, command) );
				return command;

			case SocketCommand.COMMAND_VERSION:
				mReceiver.onVersion( (Version) decode(sV, command) );
				return command;
			default:
				System.out.println("Unknown command: " + command);
		}
		return -1;
	}

	private SocketCommand decode(SocketCommand socketCommand, byte command) {
		socketCommand.command = command;
		return socketCommand;
	}

	private void decode(SocketStringCommand socketCommand, byte command, int length, byte[] message) {
		socketCommand.command = command;
		socketCommand.message = new String(message, 0, length);
	}

	private SocketStringCommand decode(SocketStringCommand socketCommand, byte command) throws Exception {
		try {
			final DataInputStream in = mIn;
			// Read until we have an int.
			final int textLength = in.readInt(); // wait forever?
			if (textLength == -1)
				throw new SocketException("Disconnected");

			//byte[] data = new byte[textLength];
			final int numRead = in.read(sData, 0, textLength); // wait forever?
			if (numRead == -1)
				throw new SocketException("Disconnected");

			if (numRead != textLength)
				throw new Exception("Incorrect length for SocketStringCommand " + socketCommand.command);

			decode(socketCommand, command, textLength, sData);

			return socketCommand;

		} catch (IOException e) {
			throw new Exception("Couldn't parse SocketStringCommand " + socketCommand.command);
		}
	}

	private Version decode(Version version, byte command) throws Exception {
		decode((SocketStringCommand) version, command);
		return version;
	}

	private MidiSocketCommand decode(MidiSocketCommand socketCommand, byte command) throws Exception {
		try {
			final DataInputStream in = mIn;
			socketCommand.command = command;
			socketCommand.set(in.readByte(), in.readByte(), in.readByte(), in.readByte());
			return socketCommand;
		} catch (IOException e) {
			throw new Exception("Couldn't parse MidiSocketCommand " + socketCommand.command);
		}
	}


	public interface IReceiver {
		public void onMidiSocketCommand(MidiSocketCommand socketCommand) throws Exception;
		public void onRequestMidiDeviceList(RequestMidiDeviceList socketCommand) throws Exception;
		public void onDeviceList(DeviceList socketCommand) throws Exception;
		public void onSetMidiDevice(SetMidiDevice socketCommand) throws Exception;
		public void onVersion(Version socketCommand) throws Exception;
	}

}
