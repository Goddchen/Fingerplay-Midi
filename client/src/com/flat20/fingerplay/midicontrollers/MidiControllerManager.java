package com.flat20.fingerplay.midicontrollers;

import java.util.LinkedHashMap;

import java.util.Set;

import android.util.Log;

import com.flat20.fingerplay.network.ConnectionManager;
import com.flat20.fingerplay.socket.commands.MidiControlChangeSocketCommand;
import com.flat20.fingerplay.socket.commands.MidiNoteOffSocketCommand;
import com.flat20.fingerplay.socket.commands.MidiNoteOnSocketCommand;
import com.flat20.fingerplay.socket.commands.SocketCommand;
import com.flat20.gui.widgets.MidiWidget;
import com.flat20.gui.widgets.Widget;
import com.flat20.gui.widgets.WidgetContainer;

public class MidiControllerManager {

    private LinkedHashMap<IMidiController, Integer> mMidiControllers = new LinkedHashMap<IMidiController, Integer>();
	private int mControllerIndex = 0;

	private ConnectionManager mConnectionManager = ConnectionManager.getInstance();


	// Singleton
	private static MidiControllerManager mInstance = null;
	public static MidiControllerManager getInstance() {
		if (mInstance == null)
			mInstance = new MidiControllerManager();
		return mInstance;
	}

	private MidiControllerManager() {
		mConnectionManager.addConnectionListener(mConnectionListener);
	}

    public void addMidiController(IMidiController midiController) {
		midiController.setOnControlChangeListener( onControlChangeListener );
    	mMidiControllers.put(midiController, Integer.valueOf(mControllerIndex));
    	mControllerIndex += midiController.getParameters().length;
    }

    public Set<IMidiController> getMidiControllers() {
    	Set<IMidiController> mcs = (Set<IMidiController>) mMidiControllers.keySet();
    	return mcs;//(IMidiController[]) mMidiControllers.keySet().toArray();
    }

    public IMidiController getMidiControllerByName(String name) {
    	Set<IMidiController> mcs = (Set<IMidiController>) mMidiControllers.keySet();
    	for (IMidiController mc : mcs) {
    		if (mc.getName().equals(name)) {
    			return mc;
    		}
    	}
    	return null;
    }

	public int getIndex(IMidiController midiController) {
		return (int) mMidiControllers.get(midiController);
	}

	// Add all midi controllers inside widgetContainer
	public void addMidiControllersIn(WidgetContainer widgetContainer) {
		Widget[] widgets = widgetContainer.getWidgets();
        for (int i=0; i<widgets.length; i++) {
        	Widget w = widgets[i];
        	if (w instanceof MidiWidget) {
				MidiWidget midiWidget = (MidiWidget) w;
				midiWidget.setOnControlChangeListener( onControlChangeListener );
	        	addMidiController( midiWidget );
        	} else if (w instanceof WidgetContainer) {
				WidgetContainer wc = (WidgetContainer) w;
				addMidiControllersIn(wc);
			}
        }
	}

	public void releaseAllHeld() {
		Set<IMidiController> controllers = getMidiControllers();
    	for (IMidiController mc : controllers) {
    		if (mc.isHolding())
    			mc.setHold(false);
    	}
	}

	private IOnControlChangeListener onControlChangeListener = new IOnControlChangeListener() {

		@Override
    	public void onControlChange(IMidiController midiController, int index, int value) {
			if (mConnectionManager.isConnected()) {
				int ccIndex = (int) getIndex(midiController);
				SocketCommand socketCommand = new MidiControlChangeSocketCommand(0, 0, ccIndex + index, value);
				mConnectionManager.write(socketCommand);
			}
    	}

    	@Override
    	public void onNoteOn(IMidiController midiController, int key, int velocity) {
    		if (mConnectionManager.isConnected()) {
				SocketCommand socketCommand = null;
				int controllerIndex = (int) getIndex(midiController);
				// midi channel, key, velocity
				socketCommand = new MidiNoteOnSocketCommand(0, controllerIndex, velocity);
				mConnectionManager.write(socketCommand);
    		}
    	}

    	@Override
    	public void onNoteOff(IMidiController midiController, int key, int velocity) {
    		if (mConnectionManager.isConnected()) {
				SocketCommand socketCommand = null;
				int controllerIndex = (int) getIndex(midiController);
				socketCommand = new MidiNoteOffSocketCommand(0, controllerIndex, velocity);
				mConnectionManager.write(socketCommand);
    		}
    	}

    };
    
    private ConnectionManager.IConnectionListener mConnectionListener = new ConnectionManager.IConnectionListener() {

    	public void onConnect() {
    	}

    	public void onDisconnect() {
    	}

    	public void onError(String errorMessage) {
    	}

    	public void onSocketCommand(SocketCommand sm) {
			if (sm.command == SocketCommand.COMMAND_MIDI_SHORT_MESSAGE) {
				Log.i("mcm", "server sent");
			}
    	}
    };


}