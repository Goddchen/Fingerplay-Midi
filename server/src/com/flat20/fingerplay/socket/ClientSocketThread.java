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
import com.flat20.fingerplay.socket.commands.FingerReader;
import com.flat20.fingerplay.socket.commands.FingerWriter;
import com.flat20.fingerplay.socket.commands.FingerReader.IReceiver;
import com.flat20.fingerplay.socket.commands.midi.MidiSocketCommand;
import com.flat20.fingerplay.socket.commands.misc.DeviceList;
import com.flat20.fingerplay.socket.commands.misc.RequestMidiDeviceList;
import com.flat20.fingerplay.socket.commands.misc.SetMidiDevice;
import com.flat20.fingerplay.socket.commands.misc.Version;

public class ClientSocketThread implements Runnable, IReceiver {

	final private Socket client;
	final private Midi midi;
	final private DataInputStream in;
	final private DataOutputStream out;
	final private FingerWriter mWriter;

	//final private byte[] mBuffer;

	public ClientSocketThread(Socket client, Midi midi) throws IOException {
		this.client = client;
		this.midi = midi;
		
		in = new DataInputStream(client.getInputStream());
		out = new DataOutputStream(client.getOutputStream());
		mWriter = new FingerWriter(out);

		//mBuffer = new byte[ 0xFFFF ]; // absolute maximum length is 65535
	}

	public void run() {
		try {
			//byte[] buffer = mBuffer;

			//final DataInputStream in = new DataInputStream(client.getInputStream());
			//final DataOutputStream out = new DataOutputStream(client.getOutputStream());
			final FingerReader reader = new FingerReader(in, this);

			while (client.isConnected()) {

				//if (in.available() > 0) {

					try {
						// Reads one command
						reader.readCommand();
					} catch (SocketTimeoutException e) {
						// TODO Remove
						System.out.println("socket read timed out..");
					}
				//}
			}

		} catch (SocketException e) {
			//System.out.println(e);
		} catch (EOFException e) {
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

	public void onVersion(Version clientVersion) throws Exception {//byte command, DataInputStream in, DataOutputStream out) throws Exception {
		System.out.println("Client version: " + clientVersion.message);

		Version version = new Version(FingerPlayServer.VERSION);

		mWriter.write(version);
	}

	public void onMidiSocketCommand(MidiSocketCommand socketCommand) throws Exception {
		System.out.println("midiCommand = " + socketCommand.midiCommand + " channel = " + socketCommand.channel + ", data1 = " + socketCommand.data1 + " data2 = " + socketCommand.data2);
		synchronized (midi) {
			midi.sendShortMessage(socketCommand.midiCommand, socketCommand.channel, socketCommand.data1, socketCommand.data2);						
		}
	}

	public void onRequestMidiDeviceList(RequestMidiDeviceList request) throws Exception {
		String[] deviceNames = Midi.getDeviceNames(false, true);

		String allDevices = "";
		for (int i=0; i<deviceNames.length; i++) {
			allDevices += deviceNames[i] + "%";
		}
		if (deviceNames.length > 0)
			allDevices = allDevices.substring(0, allDevices.length()-1);

		DeviceList deviceList = new DeviceList( allDevices );

		mWriter.write(deviceList);
	}

	public void onSetMidiDevice(SetMidiDevice ssm) throws Exception {

		String device = ssm.message;

		System.out.println("Set MIDI Device: " + device);
		synchronized (midi) {
			midi.close();
			midi.open(device, true); // true = bForOutput					
		}
	}

	public void onDeviceList(DeviceList deviceList) throws Exception {
		
	}

}
