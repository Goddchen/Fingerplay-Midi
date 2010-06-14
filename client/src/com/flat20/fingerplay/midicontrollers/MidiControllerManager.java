package com.flat20.fingerplay.midicontrollers;


import java.util.LinkedHashMap;

import java.util.Set;

import android.util.Log;

import com.flat20.fingerplay.network.ConnectionManager;
import com.flat20.fingerplay.socket.commands.midi.MidiControlChange;
import com.flat20.fingerplay.socket.commands.midi.MidiNoteOff;
import com.flat20.fingerplay.socket.commands.midi.MidiNoteOn;
import com.flat20.fingerplay.socket.commands.midi.MidiSocketCommand;
import com.flat20.fingerplay.socket.commands.SocketCommand;
import com.flat20.gui.widgets.IWidget;
import com.flat20.gui.widgets.MidiWidget;
import com.flat20.gui.widgets.WidgetContainer;

/**
 * TODO Should parse the same XML data as the LayoutManager and assign controllerNumber
 * in here rather than relying on LayoutManager for that.
 * 
 * @author andreas
 *
 */
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

	// Assigns a separate controller number to each IMidiController if LayoutManager hasn't
	// assigned it already.
    public void addMidiController(IMidiController midiController) {
		midiController.setOnControlChangeListener( onControlChangeListener );

		if (midiController.getControllerNumber() == IMidiController.CONTROLLER_NUMBER_UNASSIGNED)
			midiController.setControllerNumber( mControllerIndex );

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
		IWidget[] widgets = widgetContainer.getWidgets();
        for (int i=0; i<widgets.length; i++) {
        	IWidget w = widgets[i];
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

		// Cached to limit garbage collects. 
		final private MidiControlChange mControlChange = new MidiControlChange();
		final private MidiNoteOn mNoteOn = new MidiNoteOn();
		final private MidiNoteOff mNoteOff = new MidiNoteOff();

		@Override
    	public void onControlChange(IMidiController midiController, int controllerNumber, int value) {
			//int ccIndex = (int) getIndex(midiController);
			mControlChange.set(0xB0, 0, controllerNumber, value);
			mConnectionManager.send( mControlChange );
    	}

    	@Override
    	public void onNoteOn(IMidiController midiController, int key, int velocity) {
    		//if (mConnectionManager.isConnected()) {
				int controllerIndex = (int) getIndex(midiController);
				// midi channel, key, velocity
				mNoteOn.set(0, controllerIndex, velocity);
				//socketCommand = new MidiNoteOn(0, controllerIndex, velocity);
				mConnectionManager.send( mNoteOn );
    		//}
    	}

    	@Override
    	public void onNoteOff(IMidiController midiController, int key, int velocity) {
    		//if (mConnectionManager.isConnected()) {
				int controllerIndex = (int) getIndex(midiController);
				//socketCommand = new MidiNoteOff(0, controllerIndex, velocity);
				mNoteOff.set(0, controllerIndex, velocity);
				mConnectionManager.send(mNoteOff);
    		//}
    	}

    };

    // Handler receiving MIDI commands from the server.
    // TODO Implement 
    private ConnectionManager.IConnectionListener mConnectionListener = new ConnectionManager.IConnectionListener() {

    	public void onConnect() {
    	}

    	public void onDisconnect() {
    	}

    	public void onError(String errorMessage) {
    	}

    	public void onSocketCommand(SocketCommand sm) {
			if (sm.command == SocketCommand.COMMAND_MIDI_SHORT_MESSAGE) {
				Log.i("mcm", "server sent cc message");
				MidiSocketCommand msc = (MidiSocketCommand) sm;
				int ccIndex = msc.data1;
				Log.i("mcm", " msc = " + msc + ", ccIndex: " + ccIndex);
				Log.i("mcm", "channel: " + msc.channel + ", " + msc.command + ", " + msc.data1 + ", " + msc.data2);
			}
    	}
    };


}