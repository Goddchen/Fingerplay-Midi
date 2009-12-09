package com.flat20.fingerplay.network;

import java.net.ConnectException;

import com.flat20.fingerplay.socket.commands.SocketCommand;

/**
 * TODO Remove write(byte[] data)
 * 
 * @author andreas
 *
 */
public abstract class Connection {

	public void connect(String serverAddress) throws ConnectException {
	}

	public void disconnect() {
	}

	public boolean isConnected() {
		return false;
	}

	protected void write(byte[] data) {
		
	}

	// TODO Connections should override send() and not write() like OSCConnection does.
	public void send(SocketCommand command) {
		write(command.data);
	}

	protected OnUpdateListener listener;
    public void setOnUpdateListener(OnUpdateListener l) {
        listener = l;
    }

    public interface OnUpdateListener {
        void onDisconnect();
        void onRead(byte[] data, int length);
    }

}
