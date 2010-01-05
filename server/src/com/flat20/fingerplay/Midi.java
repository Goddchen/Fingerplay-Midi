package com.flat20.fingerplay;

import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;

public class Midi {

	MidiDevice outputDevice = null;
	Receiver receiver = null; //An output's receiver

	public Midi() {
		//open(deviceName);
	}

	public MidiDevice open(String deviceName, boolean bForOutput) {
		MidiDevice.Info	info = getMidiDeviceInfo(deviceName, bForOutput);
		if (info != null) {
			// Found device, try to open it.
			try {
				outputDevice = MidiSystem.getMidiDevice(info);
				System.out.println(info.getName() + " selected.");
				outputDevice.open();
				if (bForOutput)
					receiver = outputDevice.getReceiver();
				//Transmitter t = outputDevice.getTransmitter();
				//t.
				return outputDevice;
			} catch (MidiUnavailableException e) {
				System.out.println(e);
				return null;
			}
		}
		return null;
		/*
		else {
			// Didn't find the device so let's try default.
			System.out.println(deviceName + " not found. Using default device.");
			try {
				receiver = MidiSystem.getReceiver();
			} catch (MidiUnavailableException e) {
				System.out.println(e);
				return;
			}
			
			System.out.println(receiver.getClass().getName() + " opened");
		}*/
	}
	
	public void close() {
		if (receiver != null)
			receiver.close();

		if (outputDevice != null)
			outputDevice.close();
		
		receiver = null;
		outputDevice = null;
	}


	public void playnote(int duration) {

		if (receiver == null) {
			System.out.println("No receiver");
			return;
		}

		int	nChannel = 0;
		int nKey = 88;
		nKey = Math.min(127, Math.max(0, nKey));
		int nVelocity = 60;
		nVelocity = Math.min(127, Math.max(0, nVelocity));
		int nDuration = duration;
		nDuration = Math.max(0, nDuration);

		// sysex
		//receiver.send(sysexMessage, -1);
		
		// Note
		
		ShortMessage	onMessage = null;
		ShortMessage	offMessage = null;
		try
		{
			onMessage = new ShortMessage();
			offMessage = new ShortMessage();
			onMessage.setMessage(ShortMessage.NOTE_ON, nChannel, nKey, nVelocity);
			offMessage.setMessage(ShortMessage.NOTE_OFF, nChannel, nKey, 0);

//		    out("On Msg: " + onMessage.getStatus() + " " + onMessage.getData1() + " " + onMessage.getData2());
//		    out("Off Msg: " + offMessage.getStatus() + " " + offMessage.getData1() + " " + offMessage.getData2());
		}
		catch (InvalidMidiDataException e)
		{
			System.out.println(e);
		}

		receiver.send(onMessage, -1);

		try
		{
			Thread.sleep(nDuration);
		}
		catch (InterruptedException e)
		{
			System.out.println(e);
		}

		receiver.send(offMessage, -1);

	}

	final private ShortMessage mShortMessage = new ShortMessage();
	public void sendShortMessage(int command, int channel, int data1, int data2) {
		ShortMessage shortMessage = mShortMessage;
		try {
			shortMessage.setMessage(command, channel, data1, data2);
			sendShortMessage(shortMessage);
		} catch (InvalidMidiDataException e) {
			System.out.println(e);
		}
	}

	private void sendShortMessage(ShortMessage shortMessage) {
		if (receiver != null)
			receiver.send(shortMessage, -1);//System.nanoTime());
	}

	public void sendsysex(String message) {

		int	nLengthInBytes = message.length() / 2;
		byte[] abMessage = new byte[nLengthInBytes];
		for (int i = 0; i < nLengthInBytes; i++) {
			abMessage[i] = (byte) Integer.parseInt(message.substring(i * 2, i * 2 + 2), 16);
		}
		sendsysex(abMessage);
	}

	public void sendsysex(byte[] abMessage) {
		if (receiver == null) {
			System.out.println("No receiver");
			return;
		}

		SysexMessage sysexMessage = new SysexMessage();
		try {
			sysexMessage.setMessage(SysexMessage.SYSTEM_EXCLUSIVE, abMessage, abMessage.length);
		} catch (InvalidMidiDataException e) {
			System.out.println(e);
		}

		// sysex
		receiver.send(sysexMessage, -1);
	}

	private MidiDevice.Info getMidiDeviceInfo(String strDeviceName, boolean bForOutput)
	{
		MidiDevice.Info[]	aInfos = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < aInfos.length; i++)
		{
			if (aInfos[i].getName().equals(strDeviceName))
			{
				try
				{
					MidiDevice device = MidiSystem.getMidiDevice(aInfos[i]);
					boolean	bAllowsInput = (device.getMaxTransmitters() != 0);
					boolean	bAllowsOutput = (device.getMaxReceivers() != 0);
					if ((bAllowsOutput && bForOutput) || (bAllowsInput && !bForOutput))
					{
						return aInfos[i];
					}
				}
				catch (MidiUnavailableException e)
				{
					// TODO:
				}
			}
		}
		return null;
	}

	public static void listDevices(boolean bForInput,
			boolean bForOutput,
			boolean bVerbose)
	{
		if (bForInput && !bForOutput)
		{
			System.out.println("Available MIDI IN Devices:");
		}
		else if (!bForInput && bForOutput)
		{
			System.out.println("Available MIDI OUT Devices:");
		}
		else
		{
			System.out.println("Available MIDI Devices:");
		}

		MidiDevice.Info[]	aInfos = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < aInfos.length; i++)
		{
			try
			{
				MidiDevice	device = MidiSystem.getMidiDevice(aInfos[i]);
				boolean		bAllowsInput = (device.getMaxTransmitters() != 0);
				boolean		bAllowsOutput = (device.getMaxReceivers() != 0);
				if ((bAllowsInput && bForInput) ||
						(bAllowsOutput && bForOutput))
				{
					if (bVerbose)
					{
						System.out.println("" + i + "  "
								+ (bAllowsInput?"IN ":"   ")
								+ (bAllowsOutput?"OUT ":"    ")
								+ aInfos[i].getName() + ", "
								+ aInfos[i].getVendor() + ", "
								+ aInfos[i].getVersion() + ", "
								+ aInfos[i].getDescription());
					}
					else
					{
						System.out.println("" + i + " = " + aInfos[i].getName());
					}
				}
			}
			catch (MidiUnavailableException e)
			{
				// device is obviously not available...
				// out(e);
			}
		}
		if (aInfos.length == 0)
		{
			System.out.println("[No devices available]");
		}
		//System.exit(0);
	}

	public static String[] getDeviceNames(boolean bForInput, boolean bForOutput) {

		MidiDevice.Info[]	aInfos = MidiSystem.getMidiDeviceInfo();

		// Compiling for old java runtime without ArrayList
		//String[] tempDeviceNames = new String[1000];
		//ArrayList<String> a;
		//ArrayList<String> deviceNames = new ArrayList<String>();
		ArrayList deviceNames = new ArrayList();


		int deviceNameIndex = 0;
		for (int i = 0; i < aInfos.length; i++) {
			try {
				MidiDevice	device = MidiSystem.getMidiDevice(aInfos[i]);
				boolean		bAllowsInput = (device.getMaxTransmitters() != 0);
				boolean		bAllowsOutput = (device.getMaxReceivers() != 0);
				if ((bAllowsInput && bForInput) || (bAllowsOutput && bForOutput)) {
					//tempDeviceNames[deviceNameIndex++] = aInfos[i].getName();
					deviceNames.add(aInfos[i].getName());
				}
			}
			catch (MidiUnavailableException e)
			{
				// device is obviously not available...
				// out(e);
			}
		}

		if (aInfos.length == 0) {
			deviceNames.add("[No devices available]");
			//tempDeviceNames[0] = "[No devices available]";
		}
		
		/*
		String[] deviceNames = new String[deviceNameIndex];
		for (int i=0; i<deviceNameIndex; i++) {
			deviceNames[i] = tempDeviceNames[i];
		}
		return deviceNames;
		*/
		return (String[]) deviceNames.toArray(new String[0]);
		//return (String[]) deviceNames.toArray( new String[0] );
		
		//System.exit(0);
	}


}
