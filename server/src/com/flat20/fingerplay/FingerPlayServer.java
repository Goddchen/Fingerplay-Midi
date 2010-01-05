package com.flat20.fingerplay;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.flat20.fingerplay.Midi;
import com.flat20.fingerplay.socket.ClientSocketThread;
import com.flat20.fingerplay.socket.MulticastServer;
import com.flat20.fingerplay.socket.commands.SocketCommand;

public class FingerPlayServer implements Runnable{

	public static final String VERSION = "0.8.0";
	public static final int SERVERPORT = 4444;

	public static final String MULTICAST_SERVERIP = "230.0.0.1";
	public static final int MULTICAST_SERVERPORT = 9013;

	private Midi midi;
	
	private String mLocalIP = null;
	private static int mPort = SERVERPORT;

	public void run() {
		try {
			System.out.println("\nFingerPlayServer v" + VERSION + "\n");

			// Check if there's a new version of the server online
			// Also tries to grab the localIP from the socket.

			boolean result = Updater.update(VERSION);
			/*
			if (result) {
				System.out.println("FingerPlayServer updated. Please restart.");
				System.exit(0);
			}*/

			// 
			//SocketStringCommand sm = new SetMidiDeviceCommand("apa");
			SocketCommand s = new SocketCommand();


 			// Open MIDI Device.

			midi = new Midi();
			//midi.open("SB Audigy Synth B [CCC0]");
			//midi.open("LoopBe Internal MIDI", true); // true = bForOutput
			//midi.playnote(1000);

			//Midi midiOut = new Midi();
			//midiOut.open("LoopBe Internal MIDI", false);

			//midi.listDevices(true, false, false);

			// If update function didn't get the local IP we'll try
			// a cheaper alternative here. Might give us 127.0.0.1 though.

			if (mLocalIP == null) {
				InetAddress localAddress = InetAddress.getLocalHost();
				mLocalIP = localAddress.getHostAddress();
			}

			// Start multicast server

			String multicastOutputMessage = mLocalIP + ":" + mPort;

			Thread multicastServerThread = new Thread( new MulticastServer(MULTICAST_SERVERIP, MULTICAST_SERVERPORT, multicastOutputMessage) );
			multicastServerThread.start();


			System.out.println("Listening on " + multicastOutputMessage);

			// Wait for client connection
				
			ServerSocket serverSocket = new ServerSocket(mPort);
			System.out.println("Waiting for connection from phone..");
			while (true) {

				Socket client = serverSocket.accept();
				ClientSocketThread st = new ClientSocketThread(client, midi);
				Thread thread = new Thread( st );
				thread.start();

				System.out.println("Phone connected.");
			}

		} catch (Exception e) {
			System.out.println(e.toString());
		}

	}


	public static void main (String[] args) {
		if (args.length > 0) {
			try {
				int port = Integer.parseInt(args[0]);
				mPort = port;
			} catch (NumberFormatException e) {
				System.out.println("Couldn't set server port to " + args[0]);
			}
		}
		Thread desktopServerThread = new Thread(new FingerPlayServer());
		desktopServerThread.start();
	}
} 
