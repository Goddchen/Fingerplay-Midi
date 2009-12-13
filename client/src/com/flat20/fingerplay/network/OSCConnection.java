package com.flat20.fingerplay.network;


import java.net.ConnectException;
import java.net.InetAddress;

import com.flat20.fingerplay.socket.commands.midi.MidiSocketCommand;
import com.flat20.fingerplay.socket.commands.SocketCommand;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;
import com.illposed.osc.OSCPortOut;

import android.util.Log;

/**
 * TODO Replace illposed OSC library and make one which doesn't
 * allocate memory for each message.
 *  
 * @author andreas
 *
 */
public class OSCConnection extends Connection {

	String server = null;
	int port = 8000;

	private OSCPortOut sender = null;

	public OSCConnection() {
	}

	@Override
	public void connect(String serverAddress) throws ConnectException {
		
        String[] split = serverAddress.split(":");
        if (split != null && split.length == 2) {
        	server = split[0];
        	port = Integer.valueOf(split[1]);
        } else {
        	server = serverAddress;
        	port = 8000;
        }
        try {
        	sender = new OSCPortOut(InetAddress.getByName(server), port);
        } catch (Exception e) {
        	Log.i("osc", e.toString());
        	throw new ConnectException(e.toString());
        }
	}

	public boolean isConnected() {
		return (sender != null);
	}

	@Override
	public void send(SocketCommand sm) {
		if (sm.command == SocketCommand.COMMAND_MIDI_SHORT_MESSAGE) {
			MidiSocketCommand msc = (MidiSocketCommand) sm;
			Object args[] = new Object[1];
			args[0] = new Integer(msc.data2);
			OSCMessage msg = new OSCMessage("/fingerplay/control/" + msc.data1, args);
	    	try {
	    		sender.send((OSCPacket)msg);
	    	} catch (Exception e) {
	    		Log.i("osc", "Couldn't send");
	    	}
		}
	}

	/*
	Object args[] = new Object[4];
	args[0] = new Integer(data[1]); //command
	args[1] = new Integer(data[2]); //channel
	args[2] = new Integer(data[3]); //data1
	args[3] = new Integer(data[4]); //data2
	OSCMessage msg = new OSCMessage("/fingerplay", args);
	*/
/*
	@Override
	protected void write(byte[] data) {
		if (data[0] == SocketCommand.COMMAND_MIDI_SHORT_MESSAGE) {
			Object args[] = new Object[1];
			args[0] = new Integer(data[4]);
			OSCMessage msg = new OSCMessage("/fingerplay/control/" + data[3], args);
	    	try {
	    		sender.send((OSCPacket)msg);
	    	} catch (Exception e) {
	    		Log.i("osc", "Couldn't send");
	    	}
		} else {
			//Log.i("OSCConnection", "Can't send this to osc?");
		}
	}
*/
	public void disconnect() {
		sender.close();
		sender = null;
		//c.dispose();
	}
/*
    private OnUpdateListener listener;
    public void setOnUpdateListener(OnUpdateListener l) {
        listener = l;
    }

    public interface OnUpdateListener {
        void onConnectionLost();
        void onRead(byte[] data, int length);
    }
*/
}
