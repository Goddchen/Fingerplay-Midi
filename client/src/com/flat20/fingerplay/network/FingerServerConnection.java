package com.flat20.fingerplay.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import java.net.SocketException;

import com.flat20.fingerplay.socket.commands.FingerEncoder;
import com.flat20.fingerplay.socket.commands.SocketCommand;
import com.flat20.fingerplay.socket.commands.SocketStringCommand;
import com.flat20.fingerplay.socket.commands.midi.MidiSocketCommand;

import android.util.Log;

/**
 * 
 * TODO If we only get half a message from the server the app freaks out.
 * We need to send a header with each message specifying the length of the
 * incoming message so the read code can loop until it received everything.
 * 
 * TODO Multicast to find server address doesn't work yet.
 * Maybe a phone limitation?
 * 
 * @author andreas
 *
 */
public class FingerServerConnection extends Connection {

	private final static int READ_TIMEOUT = 1000;
	private final static int CONNECT_TIMEOUT = 10000;

	private static final String MULTICAST_SERVERIP = "230.0.0.1";
	private static final int MULTICAST_SERVERPORT = 9013;

	private static final int DEFAULT_PORT = 4444;

	private Socket socket = null;
	private DataOutputStream out = null;
	private DataInputStream in = null;
	private ReadThread readThread;

	String server = null;
	int port = DEFAULT_PORT;

	//final private byte[] mBuffer;
	//final MidiSocketCommand mMidiCommand;
	//final SocketStringCommand mStringCommand;

	public FingerServerConnection() {
		//mBuffer = new byte[ 0xFFFF ];
		//mMidiCommand = new MidiSocketCommand();
		//mStringCommand = new SocketStringCommand();
		//System.out.println("REMOVE!");
		//getServerAddressMulticast();
	}

	// Not in use, but should be.
    private String getServerAddressMulticast() {
    	try {

    		//WifiManager wifiManager = Context.getSystemService(Context.WIFI_SERVICE);
    		//wifiManager.isWifiEnabled();
			MulticastSocket socket = new MulticastSocket(MULTICAST_SERVERPORT);
			socket.setSoTimeout(8000);
			InetAddress address = InetAddress.getByName(MULTICAST_SERVERIP);
			socket.joinGroup(address);

			DatagramPacket packet;
			byte message[] = new byte[23]; //22 should be max for ip

			packet = new DatagramPacket(message, message.length);
			socket.receive(packet);
			//data[packet.getLength()-1] = 0;
			String serverAddress = new String( packet.getData(),0,packet.getLength() );
			socket.leaveGroup(address);
			socket.close();
			return serverAddress;
    	} catch (IOException e) {
    		Log.i("FSC", e.toString());
    		return null;
    	}
    }

    /**
     * In the form of hostname:port or just hostname but default port of 4444 will be used.
     * @param serverAddress
     * @return
     */
    @Override
	public void connect(String serverAddress) throws ConnectException {
		if (isConnected())
			disconnect();

        String[] split = serverAddress.split(":");
        if (split != null && split.length == 2) {
        	server = split[0];
        	port = Integer.valueOf(split[1]);
        } else {
        	server = serverAddress;
        	port = DEFAULT_PORT;
        } 

        try {
			socket = new Socket();
			socket.setSoTimeout(READ_TIMEOUT); //read intervals
			socket.setTcpNoDelay(true);
			InetSocketAddress remoteAddr = new InetSocketAddress(server, port);

			socket.connect(remoteAddr, CONNECT_TIMEOUT); // was 2000
			try {
				out = new DataOutputStream( socket.getOutputStream() );
				in = new DataInputStream( socket.getInputStream() );

				readThread = new ReadThread();
				readThread.start();

			} catch(Exception e) {
				socket = null;
				Log.e("TCP", "Socket", e);
				throw new ConnectException(e.toString());
			} finally {
				//socket.close();
			}
		} catch (SocketException e) {
			socket = null;
			Log.e("TCP", "Socket" + e);
			throw new ConnectException(e.toString());
		} catch (Exception e) {
			socket = null;
			//Log.e("TCP", "C", e);
			throw new ConnectException(e.toString());
		}
	}

	// Socket is disconnected when we can't read anything from it.
	public boolean isConnected() {
		return (readThread != null && readThread.isRunning());
	}
/*
	@Override
	protected void write(byte[] data) {
		if (out == null)
			return;
		try {
			out.write(data);
		} catch (IOException e) {
			//Log.i("asd", e.toString());
		}
	}
*/
	@Override
	public void send(SocketCommand socketCommand) {
		if (out != null) {
			try {
				out.write( FingerEncoder.encode(socketCommand) );
			} catch (Exception e) {
				Log.e("FingerserverConnection", e.toString());
			}
		}
	}

	public void disconnect() {

		try {
			if (isConnected()) {
				readThread.setRunning(false);
				socket.close();
			}
		} catch (IOException e) {
			
		} finally {
			//socket = null;
		}

	}


    // Create runnable for posting results from connect()
    final Runnable mDispatchSocketReadEvent = new Runnable() {
        public void run() {
        	//if (listener != null)
        		//listener.onRead(buffer, numRead);
        	//Log.i("sc", "ServerConnection read something!");
        }
    };

    final Runnable mSocketDisconnected = new Runnable() {
        public void run() {
        	if (listener != null)
				listener.onDisconnect();
        }
    };

    // So reading is done in the background.
    class ReadThread extends Thread {

    	boolean running;

    	public ReadThread() {
    		running = true;
    	}
 
    	public boolean isRunning() {
    		return running;
    	}
    	public void setRunning(boolean r) {
    		running = r;
    	}


    	public void run() {

    		final MidiSocketCommand msm = new MidiSocketCommand();
			final SocketStringCommand ssm = new SocketStringCommand();

			while (running) {
				try {

					if (in.available() > 0) {
						byte command = in.readByte();
	
						switch (command) {
							case SocketCommand.COMMAND_MIDI_SHORT_MESSAGE:
								FingerEncoder.decode(msm, command, in);
								if (listener != null)
				    				listener.onSocketCommand(msm);
								break;
							case SocketCommand.COMMAND_MIDI_DEVICE_LIST:
								FingerEncoder.decode(ssm, command, in);
								if (listener != null)
				    				listener.onSocketCommand(ssm);
								break;
							case SocketCommand.COMMAND_VERSION:
								FingerEncoder.decode(ssm, command, in);
								if (listener != null)
				    				listener.onSocketCommand(ssm);
								break;
							default:
								System.out.println("Unknown command: " + command);
								break;
						}
					}
				} catch (SocketTimeoutException e) {
					// normal behaviour from socket reads.
					Log.i("FSC", "SocketTimeoutException " + e);
				} catch (Exception e) {
					Log.i("FSC", "read exception " + e);
					disconnect();
					Log.i("FSC", "onDiksconnect should be called");
					try {
						socket.close();
					} catch (Exception e2) {
						System.out.println("desperate close!" + e2);
					}
					//setRunning(false);
					// TODO Run this in disconnect() ??
    				if (listener != null)
    					listener.onDisconnect();
				}

			}

/*    		
    		while (running) {
	    		try {
	    			int numRead = in.read(buffer);
	    			if (numRead == -1) {
	    				//disconnect();
	    				setRunning(false);
	    				if (listener != null)
	    					listener.onDisconnect();
	    				//mHandler.post(mSocketDisconnected);
	    			} else if (listener != null) {
	    				//Log.i("fsc", new String(buffer));
	    				listener.onRead(buffer, numRead);
	    			}
	    				//mHandler.post(mDispatchSocketReadEvent);
	    		} catch (SocketTimeoutException e) {
	    			
	    		} catch (IOException e) {
	    			Log.i("sc.rt", e.toString());
	    		}
    		}
*/
    	}
    }

    
/*
	private MidiSocketCommand handleMidiShortMessage(byte command, DataInputStream in) throws IOException {
		byte midiCommand = in.readByte();//buffer[index + 1] & 0xFF; // Make it unsigned.
		byte channel = in.readByte();//buffer[index + 2] & 0xFF;
		byte data1 = in.readByte();//buffer[index + 3] & 0xFF;
		byte data2 = in.readByte();//buffer[index + 4] & 0xFF;

		MidiSocketCommand msc = new MidiSocketCommand(command, channel, data1, data2);

		//TODO check all ranges.
		System.out.println("midiCommand = " + midiCommand + " channel = " + channel + ", data1 = " + data1 + " data2 = " + data2);
		return msc;
		//synchronized (midi) {
			//midi.sendShortMessage(midiCommand, channel, data1, data2);						
		//}
	}

	private SocketStringCommand handleMidiDeviceListCommand(byte command, DataInputStream in) throws IOException {

		SocketStringCommand ssm = parseStringCommand(command, in);
		String deviceList = ssm.message;

		System.out.println("Device list: " + deviceList);

		return ssm;
	}

	private SocketStringCommand parseStringCommand(byte command, DataInputStream in) throws SocketException, IOException {
		byte[] buffer = mBuffer;
		SocketStringCommand stringCommand = mStringCommand;

		// Read until we have an int.
		int textLength = in.readInt(); // wait forever?
		if (textLength == -1)
			throw new SocketException("Disconnected");

		//byte[] data = new byte[textLength];
		int numRead = in.read(buffer, 0, textLength); // wait forever?
		if (numRead == -1)
			throw new SocketException("Disconnected");

		FingerEncoder.decode(stringCommand, command, textLength, buffer);

		System.out.println(stringCommand.command + ": " + stringCommand.message);
		return stringCommand;
	}
*/
} 
