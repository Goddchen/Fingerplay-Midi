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

	private static final String MULTICAST_SERVERIP = "230.0.0.1";
	private static final int MULTICAST_SERVERPORT = 9013;

	private static final int DEFAULT_PORT = 4444;

	private Socket socket = null;
	private DataOutputStream out = null;
	private DataInputStream in = null;
	private ReadThread readThread;

	String server = null;
	int port = DEFAULT_PORT;

	public FingerServerConnection() {
		
	}

	// Not in use, but should be.
    public String getServerAddressMulticast() {
    	try {

    		//WifiManager wifiManager = Context.getSystemService(Context.WIFI_SERVICE);
    		//wifiManager.isWifiEnabled();
			MulticastSocket socket = new MulticastSocket(MULTICAST_SERVERPORT);
			socket.setSoTimeout(3000);
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
			//socket = socketFactory.createSocket("192.168.0.97", 4444);
			socket = new Socket();
			//socket = socketFactory.createSocket(server, 4444);
			socket.setSoTimeout(READ_TIMEOUT); //read intervals
			//socket = socketFactory.createSocket("192.168.1.3", 4444);
			socket.setTcpNoDelay(true);
			InetSocketAddress remoteAddr = new InetSocketAddress(server, port);
			socket.connect(remoteAddr, 8000); // was 2000
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


	public void disconnect() {
		
		try {
			if (isConnected()) {
				readThread.setRunning(false);
				socket.close();
			}
		} catch (IOException e) {
			
		} finally {

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

    // Need handler for callbacks to ServerService thread(?)
    //final Handler mHandler = new Handler();

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
    		byte[] buffer = new byte[4096];
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
    	}
    }

} 
