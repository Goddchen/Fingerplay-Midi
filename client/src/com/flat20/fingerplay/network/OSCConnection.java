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

	public void disconnect() {
		if (sender != null) {
			sender.close();
			sender = null;
		}
	}

}
