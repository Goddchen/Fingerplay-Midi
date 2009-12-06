package com.flat20.fingerplay.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.flat20.fingerplay.Midi;
import com.flat20.fingerplay.socket.commands.DeviceListCommand;
import com.flat20.fingerplay.socket.commands.SocketCommand;

public class ClientSocketThread implements Runnable {

	private Socket client;
	private Midi midi;
	private byte[] buffer;
	private DataInputStream in;
	private DataOutputStream out;

	public ClientSocketThread(Socket client, Midi midi) {
		this.client = client;
		this.midi = midi;
		buffer = new byte[1024];
	}

	public void run() {
		try {
			in = new DataInputStream(client.getInputStream());
			out = new DataOutputStream(client.getOutputStream());

			while (client.isConnected()) {

				int length = in.read(buffer);
				if (length == -1) {
					client.close();
					break;
				}
				//System.out.println("buffer length = " + length);
				if (length >= 1) {
					int index = 0;
					int next = 0;
					while (index < length) {
						int command = buffer[index] & 0xFF;
						//System.out.println("Got command = " + command);
						switch (command) {
							case SocketCommand.COMMAND_MIDI_SHORT_MESSAGE:
								next = handleMidiShortMessage(buffer, index);
								break;
							case SocketCommand.COMMAND_REQUEST_MIDI_DEVICE_LIST:
								next = handleRequestMidiDeviceListCommand(buffer, index);
								break;
							case SocketCommand.COMMAND_SET_MIDI_DEVICE:
								next = handleSetMidiDeviceCommand(buffer, index);
								break;
							default:
								System.out.println("Weird message. Skipping all." + index + ", " + next + ", " + length + ", " + buffer);
								next = length - index;
								break;
						}
						index += next;
					}
				}
			}

		} catch (SocketException e) {
			try {
				client.close();
			} catch (IOException io) {
				System.out.println(io);
			}
		} catch (EOFException e) {
			try {
				client.close();
			} catch (IOException io) {
				System.out.println(io);
			}
		} catch (SocketTimeoutException e) {
			try {
				client.close();
			} catch (IOException io) {
				System.out.println(io);
			}
		} catch(Exception e) {
			System.out.println("S: Error");
			e.printStackTrace();
		} finally {
			//client.close();
			//
		}
		System.out.println("Phone disconnected.");
		return;
	}

	private int handleMidiShortMessage(byte[] buffer, int index) {
		int midiCommand = buffer[index + 1] & 0xFF; // Make it unsigned.
		int channel = buffer[index + 2] & 0xFF;
		int data1 = buffer[index + 3] & 0xFF;
		int data2 = buffer[index + 4] & 0xFF;
		//TODO check all ranges.
		System.out.println("midiCommand = " + midiCommand + " channel = " + channel + ", data1 = " + data1 + " data2 = " + data2);
		synchronized (midi) {
			midi.sendShortMessage(midiCommand, channel, data1, data2);						
		}
		return 5;
	}

	private int handleRequestMidiDeviceListCommand(byte[] buffer, int index) {
		String[] deviceNames = Midi.getDeviceNames(false, true);

		String allDevices = "";
		for (int i=0; i<deviceNames.length; i++) {
			allDevices += deviceNames[i] + "%";
		}
		if (deviceNames.length > 0)
			allDevices = allDevices.substring(0, allDevices.length()-1);

		DeviceListCommand deviceList = new DeviceListCommand( allDevices );
		try {
			out.write(deviceList.data);
		} catch (IOException e) {
			System.out.println(e);
		}
		return 1;
	}

	private int handleSetMidiDeviceCommand(byte[] buffer, int index) {
		String device = "";
		int i;
		for (i = index+1; i<buffer.length; i++) {
			if (buffer[i] == 0) {
				//System.out.println("we found 0 at " + i);
				break;
			}
			device += (char)buffer[i];
		}
		//System.out.println("Set MIDI Device: " + device);
		synchronized (midi) {
			midi.close();
			midi.open(device, true); // true = bForOutput					
		}
		return i + 1; // An extra 0 perhaps?
	}

}
