package com.flat20.fingerplay.network;

/**
 * TODO: Only need one connection at a time.
 */
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;

import android.os.Handler;

import com.flat20.fingerplay.socket.commands.SocketCommand;

public class ConnectionManager {

	public static final int CONNECTION_TYPE_FINGERSERVER = 1;
	public static final int CONNECTION_TYPE_OSC = 2;

	private Connection mConnection = null;

	// List of available connections
	private final HashMap<Integer, Connection> mConnections;

	// Listeners
	private final ArrayList<IConnectionListener> mConnectionListeners;

	// Singleton
	private static ConnectionManager mInstance = null;
	public static ConnectionManager getInstance() {
		if (mInstance == null)
			mInstance = new ConnectionManager();
		return mInstance;
	}

	private ConnectionManager() {
		mConnections = new HashMap<Integer, Connection>(2);
		mConnectionListeners = new ArrayList<IConnectionListener>();

		FingerServerConnection fsConn = new FingerServerConnection();
		OSCConnection oscConn = new OSCConnection();
		mConnections.put(CONNECTION_TYPE_FINGERSERVER, fsConn);
		mConnections.put(CONNECTION_TYPE_OSC, oscConn);
	}

	public void cleanup() {
		for (Connection connection: mConnections.values()) {
			connection.disconnect();
			connection.setOnUpdateListener(null);
		}
	}

	public void setConnection(int connectionType) {
		if (mConnection != null)
			mConnection.setOnUpdateListener(null);
		mConnection = mConnections.get(connectionType);
		mConnection.setOnUpdateListener(mOnUpdateListener);
	}

	public void addConnectionListener(IConnectionListener listener) {
		mConnectionListeners.add(listener);
	}

	public void removeConnectionListener(IConnectionListener listener) {
		mConnectionListeners.remove(listener);
	}

	public void connect(String server) {
		if (mConnection != null) {
			ConnectThread t = new ConnectThread(server);
			t.start();
		}
	}

	public void disconnect() {
		if (mConnection != null)
			mConnection.disconnect();
	}

	public boolean isConnected() {
		if (mConnection != null)
			return mConnection.isConnected();
		return false;
	}

	public void send(SocketCommand sm) {
		if (mConnection != null)
			mConnection.send(sm);
	}/*
	public void write(SocketCommand sm) {
		if (mConnection != null)
			mConnection.write(sm.data);
	}*/

    final Handler mHandler = new Handler();

    final Runnable mOnConnect = new Runnable() {
        public void run() {
    		for (IConnectionListener listener : mConnectionListeners)
    			listener.onConnect();
        }
    };

    class OnError implements Runnable {

    	protected String mErrorMessage;

    	public OnError(String errorMessage) {
    		mErrorMessage = errorMessage;
    	}

    	public void run() {
    		for (IConnectionListener listener : mConnectionListeners)
    			listener.onError(mErrorMessage);
    	}
    };
/*
    final Runnable mOnError = new Runnable() {
        public void run() {
    		for (IConnectionListener listener : mConnectionListeners)
    			listener.onError();
        }
    };
*/
    class OnSocketCommand implements Runnable {
    	public SocketCommand socketCommand = null;
    	public void run() {
    		for (IConnectionListener listener : mConnectionListeners)
    			listener.onSocketCommand(socketCommand);
    	}
    };
    final OnSocketCommand mOnSocketCommand = new OnSocketCommand();

    final Runnable mOnDisconnect = new Runnable() {
    	public void run() {
    		for (IConnectionListener listener : mConnectionListeners)
    			listener.onDisconnect();
    	}
    };

    // So connection is done in the background.
    class ConnectThread extends Thread {
    	public String server;
    	
    	public ConnectThread(String server) {
    		this.server = server;
    	}

    	public void run() {
    		if (server == null)
    			return;
    		try {
    			mConnection.connect(server); // returns a result. we should use it.
    		} catch (ConnectException e) {
        		mHandler.post( new OnError(e.toString()) );
    			// Do a Toast with the error..
    		} finally {
    			mHandler.post(mOnConnect);
    		}
    	}
    }

	// Listener for our connection
    private Connection.OnUpdateListener mOnUpdateListener = new Connection.OnUpdateListener() {

    	public void onDisconnect() {
    		mHandler.post(mOnDisconnect);
    	}

    	public void onSocketCommand(SocketCommand socketCommand) {
    		mOnSocketCommand.socketCommand = socketCommand;
    		mHandler.post(mOnSocketCommand);
    	}

    };

	// Interface used by application
    public interface IConnectionListener {
    	void onConnect();
    	void onDisconnect();
    	void onError(String errorMessage);
    	void onSocketCommand(SocketCommand sm);
    }
}
