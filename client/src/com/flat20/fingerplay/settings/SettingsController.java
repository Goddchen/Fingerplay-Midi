package com.flat20.fingerplay.settings;

import java.io.File;
import java.io.FilenameFilter;

import android.os.Environment;
import android.util.Log;

import com.flat20.fingerplay.midicontrollers.IMidiController;
import com.flat20.fingerplay.network.ConnectionManager;
import com.flat20.fingerplay.socket.commands.SocketCommand;
import com.flat20.fingerplay.socket.commands.SocketStringCommand;
import com.flat20.fingerplay.socket.commands.midi.MidiControlChange;
import com.flat20.fingerplay.socket.commands.misc.RequestMidiDeviceList;
import com.flat20.fingerplay.socket.commands.misc.SetMidiDevice;
import com.flat20.fingerplay.socket.commands.misc.Version;

public class SettingsController {

	SettingsModel mModel;
	SettingsView mView;

	// Separate model
	protected ConnectionManager mConnectionManager = null;

	public SettingsController(SettingsView view) {
		mModel = SettingsModel.getInstance();
		mView = view;

		mConnectionManager = ConnectionManager.getInstance();
		mConnectionManager.addConnectionListener(mConnectionListener);

		mModel.setState( mConnectionManager.isConnected() ? SettingsModel.STATE_CONNECTING_SUCCESS : SettingsModel.STATE_DISCONNECTED );

		// A bit of a hack to get the view to update the preferences properly.
		// TODO: Just call requestMidiDeviceList() and set STATE_CONNECTED like it should be.
		if (mModel.state == SettingsModel.STATE_CONNECTING_SUCCESS) {
			mModel.setState(SettingsModel.STATE_CONNECTED);
			requestMidiDeviceList();
		}

		// Get the layout XML files from the sdcard. 
		updateLayoutsList();
	}

	public void destroy() {
		mConnectionManager.removeConnectionListener(mConnectionListener);
	}

    // Server updates listener

    private ConnectionManager.IConnectionListener mConnectionListener = new ConnectionManager.IConnectionListener() {

    	public void onConnect() {
    		if (mConnectionManager.isConnected()) {
    			mModel.setState(SettingsModel.STATE_CONNECTING_SUCCESS);
    			mModel.setState(SettingsModel.STATE_CONNECTED);
    			if (mModel.serverType == ConnectionManager.CONNECTION_TYPE_FINGERSERVER)
    				requestMidiDeviceList();
    		} else {
    			mModel.setState(SettingsModel.STATE_CONNECTING_FAIL);
    			mModel.setState(SettingsModel.STATE_DISCONNECTED);
    		}

    	}

    	public void onDisconnect() {
    		mModel.setState(SettingsModel.STATE_DISCONNECTED);
    	}

    	public void onError(String errorMessage) {
    		mView.displayError(errorMessage);
    	}

    	public void onSocketCommand(SocketCommand sm) {
    		if (sm.command == SocketCommand.COMMAND_MIDI_DEVICE_LIST) {
    			SocketStringCommand ssm = (SocketStringCommand) sm;
    			String[] deviceNames = ssm.message.split("%");
    			mModel.setMidiDevices(deviceNames);
    			if (mModel.midiDevice != null)
    				setMidiDevice(mModel.midiDevice);
    		} else if (sm.command == SocketCommand.COMMAND_VERSION) {
    			Version version = (Version) sm;
    			Log.i("Settings", "version = " + version.message);
    		}
    	}

    };


    // Controller commands

    protected void setConnectionType(int connectionType) {
        mConnectionManager.setConnection(connectionType);
		mModel.setServerType( connectionType );
    }

    protected void requestMidiDeviceList() {
    	if (mConnectionManager.isConnected()) {
    		RequestMidiDeviceList sm = new RequestMidiDeviceList();
    		mConnectionManager.send(sm);
    	}
    }

    protected void setMidiDevice(String deviceName) {
    	if (mConnectionManager.isConnected()) {
    		SetMidiDevice setDevice = new SetMidiDevice(deviceName);
    		mConnectionManager.send(setDevice);
    	}
		mModel.setMidiDevice(deviceName);
	}

    protected void serverConnect() {
		if ( mModel.serverType != -1 && !mConnectionManager.isConnected() ) {
			mConnectionManager.connect( mModel.serverAddress );
			mModel.setState(SettingsModel.STATE_CONNECTING);
		} else {
			mConnectionManager.disconnect();
			mModel.setState(SettingsModel.STATE_DISCONNECTING);
			//TODO connections should send onDisconnect
			mModel.setState(SettingsModel.STATE_DISCONNECTED);
		}
    }

    protected void sendControlChange(String controllerName, int index) {
		IMidiController mc = mModel.midiControllerManager.getMidiControllerByName(controllerName);
		if (mc != null) {
			int ccIndex = (int) mModel.midiControllerManager.getIndex(mc);
    		MidiControlChange socketCommand = new MidiControlChange(0xB0, 0, ccIndex + index, 0x7F);
    		mConnectionManager.send(socketCommand);
		}

    }

	protected void setLayoutFile(String value) {
		if (!value.equals(mModel.layoutFile)) {
			mView.displayError("Restart FingerPlay MIDI to use the new layout file.");
		}
		mModel.setLayoutFile(value);
	}


	// Layout files listing

    protected void updateLayoutsList() {
        File home = new File(Environment.getExternalStorageDirectory() + "/FingerPlayMIDI/");

        XMLFilter filter = new XMLFilter();
        String[] files = home.list(filter);
        mModel.setLayoutFiles(files);
    }

    class XMLFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return name.endsWith(".xml");
        }
    }

}
