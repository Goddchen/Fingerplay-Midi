package com.flat20.fingerplay.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.flat20.fingerplay.FingerPlayServer;
import com.flat20.fingerplay.Midi;
import com.flat20.fingerplay.socket.commands.FingerEncoder;
import com.flat20.fingerplay.socket.commands.SocketCommand;
import com.flat20.fingerplay.socket.commands.midi.MidiSocketCommand;
import com.flat20.fingerplay.socket.commands.SocketStringCommand;
import com.flat20.fingerplay.socket.commands.misc.DeviceList;
import com.flat20.fingerplay.socket.commands.misc.Version;

public class ClientSocketThread implements Runnable {

	final private Socket client;
	final private Midi midi;
	//private DataInputStream in;
	//private DataOutputStream out;

	//final private byte[] mBuffer;

	public ClientSocketThread(Socket client, Midi midi) {
		this.client = client;
		this.midi = midi;

		//mBuffer = new byte[ 0xFFFF ]; // absolute maximum length is 65535
	}

	public void run() {
		try {
			//byte[] buffer = mBuffer;

			final DataInputStream in = new DataInputStream(client.getInputStream());
			final DataOutputStream out = new DataOutputStream(client.getOutputStream());

			while (client.isConnected()) {

				//if (in.available() > 0) {
					
					try {
						// Read command
						byte command = in.readByte();
						//System.out.println("command = " + command + ", avail: " + in.available());

						switch (command) {
							case SocketCommand.COMMAND_MIDI_SHORT_MESSAGE:
								handleMidiShortMessage(command, in);
								break;
							case SocketCommand.COMMAND_REQUEST_MIDI_DEVICE_LIST:
								handleRequestMidiDeviceListCommand(command, in, out);
								break;
							case SocketCommand.COMMAND_SET_MIDI_DEVICE:
								handleSetMidiDeviceCommand(command, in);
								break;
							case SocketCommand.COMMAND_VERSION:
								handleVersion(command, in, out);
								break;
							default:
								System.out.println("Unknown command: " + command);
								break;
						}
					} catch (SocketTimeoutException e) {
						// TODO Remove
						System.out.println("socket read timed out..");
					}
				//}
			}

		} catch (SocketException e) {
			System.out.println(e);
		} catch (EOFException e) {
			System.out.println(e);
		//} catch (SocketTimeoutException e) {
			//System.out.println(e);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Client message too long for buffer.");
			System.out.println(e);
		} catch(Exception e) {
			System.out.println("S: Error");
			e.printStackTrace();
		} finally {
			try {
				client.close();
			} catch (IOException io) {
			}
		}
		System.out.println("Phone disconnected.");
		return;
	}

	private void handleVersion(byte command, DataInputStream in, DataOutputStream out) throws Exception {
		SocketStringCommand ssm = new SocketStringCommand();

		FingerEncoder.decode(ssm, command, in);

		System.out.println("Client version: " + ssm.message);

		Version version = new Version(FingerPlayServer.VERSION);

		try {
			out.write( FingerEncoder.encode(version) );
		} catch (IOException e) {
			System.out.println(e);
		}

	}

	private void handleMidiShortMessage(byte command, DataInputStream in) throws IOException {
		final MidiSocketCommand socketCommand = new MidiSocketCommand();
		FingerEncoder.decode(socketCommand, command, in);
		//TODO check all ranges.
		System.out.println("midiCommand = " + socketCommand.midiCommand + " channel = " + socketCommand.channel + ", data1 = " + socketCommand.data1 + " data2 = " + socketCommand.data2);
		synchronized (midi) {
			midi.sendShortMessage(socketCommand.midiCommand, socketCommand.channel, socketCommand.data1, socketCommand.data2);						
		}
	}

	private void handleRequestMidiDeviceListCommand(byte command, DataInputStream in, DataOutputStream out) {
		String[] deviceNames = Midi.getDeviceNames(false, true);

		String allDevices = "";
		for (int i=0; i<deviceNames.length; i++) {
			allDevices += deviceNames[i] + "%";
		}
		if (deviceNames.length > 0)
			allDevices = allDevices.substring(0, allDevices.length()-1);

		DeviceList deviceList = new DeviceList( allDevices );

		try {
			out.write( FingerEncoder.encode(deviceList) );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleSetMidiDeviceCommand(byte command, DataInputStream in) throws Exception {
		SocketStringCommand ssm = new SocketStringCommand();

		FingerEncoder.decode(ssm, command, in);

		String device = ssm.message;

		System.out.println("Set MIDI Device: " + device);
		synchronized (midi) {
			midi.close();
			midi.open(device, true); // true = bForOutput					
		}
	}

}
