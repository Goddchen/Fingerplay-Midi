package com.flat20.fingerplay.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import java.net.SocketException;

import com.flat20.fingerplay.socket.commands.FingerReader;
import com.flat20.fingerplay.socket.commands.FingerWriter;
import com.flat20.fingerplay.socket.commands.SocketCommand;
import com.flat20.fingerplay.socket.commands.FingerReader.IReceiver;
import com.flat20.fingerplay.socket.commands.midi.MidiSocketCommand;
import com.flat20.fingerplay.socket.commands.misc.DeviceList;
import com.flat20.fingerplay.socket.commands.misc.RequestMidiDeviceList;
import com.flat20.fingerplay.socket.commands.misc.SetMidiDevice;
import com.flat20.fingerplay.socket.commands.misc.Version;

import android.util.Log;

/**
 * 
 * TODO Multicast to find server address doesn't work yet.
 * Maybe a phone limitation?
 * 
 * @author andreas
 *
 */
public class FingerServerConnection extends Connection implements IReceiver {

	private final static int READ_TIMEOUT = 1000;
	private final static int CONNECT_TIMEOUT = 10000;

	//private static final String MULTICAST_SERVERIP = "230.0.0.1";
	//private static final int MULTICAST_SERVERPORT = 9013;

	private static final int DEFAULT_PORT = 4444;

	private Socket socket = null;
	private DataOutputStream out = null;
	private DataInputStream in = null;
	private FingerWriter mWriter;
	private FingerReader mReader;
	private ReadThread mReadThread;

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
/*
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
*/
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

				mWriter = new FingerWriter(out);
				mReader = new FingerReader(in, this);

				mReadThread = new ReadThread();
				mReadThread.start();

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
		return (mReadThread != null && mReadThread.isRunning());
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
				mWriter.write(socketCommand);
			} catch (Exception e) {
				Log.e("FingerserverConnection", e.toString());
			}
			/*
			try {
				out.write( FingerEncoder.encode(socketCommand) );
			} catch (Exception e) {
				Log.e("FingerserverConnection", e.toString());
			}*/
		}
	}

	public void disconnect() {

		try {
			if (isConnected()) {
				mReadThread.setRunning(false);
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
    private class ReadThread extends Thread {

    	private boolean mRunning;

    	public ReadThread() {
    		mRunning = true;
    	}
 
    	public boolean isRunning() {
    		return mRunning;
    	}
    	public void setRunning(boolean r) {
    		mRunning = r;
    	}


    	public void run() {

			while (mRunning) {
				try {
					if (in.available() > 0) {
						mReader.readCommand();
					}
					Thread.sleep(20);
				} catch (SocketTimeoutException e) {
					// normal behaviour from socket reads.
					Log.i("FSC", "SocketTimeoutException " + e);
				} catch (Exception e) {
					Log.i("FSC", "read exception " + e);
					disconnect();
					try {
						socket.close();
					} catch (Exception e2) {
						System.out.println("socket.close() failed! " + e2);
					}

					// TODO Run this in disconnect() ??
    				if (listener != null)
    					listener.onDisconnect();
				}

			}

    	}
    }

    @Override
	public void onDeviceList(DeviceList socketCommand) throws Exception {
		if (listener != null)
			listener.onSocketCommand(socketCommand);
	}

	@Override
	public void onMidiSocketCommand(MidiSocketCommand socketCommand) throws Exception {
		if (listener != null)
			listener.onSocketCommand(socketCommand);
	}

	@Override
	public void onVersion(Version socketCommand) throws Exception {
		if (listener != null)
			listener.onSocketCommand(socketCommand);
	}

	// Server side
	@Override
	public void onRequestMidiDeviceList(RequestMidiDeviceList socketCommand) throws Exception {
	}

	@Override
	public void onSetMidiDevice(SetMidiDevice socketCommand) throws Exception {
	}
} 
