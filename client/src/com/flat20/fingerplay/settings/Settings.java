package com.flat20.fingerplay.settings;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Set;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import com.flat20.fingerplay.R;
import com.flat20.fingerplay.midicontrollers.IMidiController;
import com.flat20.fingerplay.midicontrollers.Parameter;
import com.flat20.fingerplay.network.ConnectionManager;
import com.flat20.fingerplay.socket.commands.MidiControlChangeSocketCommand;
import com.flat20.fingerplay.socket.commands.SetMidiDeviceCommand;
import com.flat20.fingerplay.socket.commands.SocketCommand;

/**
 * Settings uses a simple MVC pattern. View redraws _everything_ when the model sends
 * the update event, ie calls its updateView() method.
 * 
 * @author andreas
 *
 */
public class Settings extends PreferenceActivity implements IController {  

	protected SettingsModel mModel;
	protected SettingsView mView;

	protected ConnectionManager mConnectionManager = null;


	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.layout.settings);

		mConnectionManager = ConnectionManager.getInstance();
		mConnectionManager.addConnectionListener(mConnectionListener);

		mModel = SettingsModel.getInstance();
		mModel.setState( mConnectionManager.isConnected() ? SettingsModel.STATE_CONNECTING_SUCCESS : SettingsModel.STATE_DISCONNECTED );
		//mModel = new SettingsModel( this, mConnectionManager.isConnected() ? SettingsModel.STATE_CONNECTING_SUCCESS : SettingsModel.STATE_DISCONNECTED );
		mView = new SettingsView(mModel, this);
		mModel.setView(mView);

		// A bit of a hack to get the view to update the preferences properly.
		// TODO: Just call requestMidiDeviceList() and set STATE_CONNECTED like it should be.
		if (mModel.state == SettingsModel.STATE_CONNECTING_SUCCESS) {
			mModel.setState(SettingsModel.STATE_CONNECTED);
			requestMidiDeviceList();
		}

		// Get the layout XML files from the sdcard. 
		updateLayoutsList();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();

		mConnectionManager.removeConnectionListener(mConnectionListener);
	}


	@Override
	public void onPreferenceChange(Preference preference, Object newValue) {
		if (preference == mView.mServerTypes) {
			setConnectionType( Integer.parseInt((String)newValue) );
		} else if (preference == mView.mServerAddressEditText)
			mModel.setServerAddress((String) newValue);
		else if (preference == mView.mDevices)
			setMidiDevice((String) newValue);
		else if (preference == mView.mLayoutFiles) {
			mModel.setLayoutFile((String) newValue);
		}
	}

    @Override
    public boolean onPreferenceTreeClick (PreferenceScreen preferenceScreen, Preference preference) {

    	if (preference == null || preference.getKey() == null)
    		return true;

    	if (preference.getKey().equals( "settings_server_connect" )) {
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

    	if (preferenceScreen.getKey() != null && preferenceScreen.getKey().equals("settings_midi_controllers")) {
    		String key = preference.getKey();
    		String name[] = key.split("_");
    		String mcName = name[0];
    		int index = Integer.parseInt(name[1]);
    		IMidiController mc = mModel.midiControllerManager.getMidiControllerByName(mcName);
    		if (mc != null) {
				int ccIndex = (int) mModel.midiControllerManager.getIndex(mc);
	    		SocketCommand socketCommand = new MidiControlChangeSocketCommand(0, 0, ccIndex + index, 0x7F);
	    		mConnectionManager.write(socketCommand);
    		}
    	
    	}

    	return true;
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
            Toast info = Toast.makeText(Settings.this, errorMessage, Toast.LENGTH_LONG);
            info.show();
    	}

    	public void onSocketCommand(SocketCommand sm) {
    		if (sm.command == SocketCommand.COMMAND_MIDI_DEVICE_LIST) {
    			String[] deviceNames = sm.getParametersAsString(sm.data.length).split("%");
    			mModel.setMidiDevices(deviceNames);
    			if (mModel.midiDevice != null)
    				setMidiDevice(mModel.midiDevice);
    		}
    	}

    };

    protected void setConnectionType(int connectionType) {
        mConnectionManager.setConnection(connectionType);
		mModel.setServerType( connectionType );
    }

    protected void requestMidiDeviceList() {
    	if (mConnectionManager.isConnected()) {
    		SocketCommand sm = new SocketCommand(SocketCommand.COMMAND_REQUEST_MIDI_DEVICE_LIST);
    		mConnectionManager.write(sm);
    	}
    }

    protected void setMidiDevice(String deviceName) {
    	if (mConnectionManager.isConnected()) {
    		SetMidiDeviceCommand setDevice = new SetMidiDeviceCommand(deviceName);
    		mConnectionManager.write(setDevice);
    	}
		mModel.setMidiDevice(deviceName);
	}

    // Layout
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

    
    

	// Model



	// View

	protected class SettingsView implements Preference.OnPreferenceChangeListener { 

		final protected SettingsModel mModel;
		final protected IController mController;

	    final protected ListPreference mServerTypes;
	    final protected CheckBoxPreference mServerConnectCheckBox;
	    final protected EditTextPreference mServerAddressEditText;

	    final protected ListPreference mDevices;
	    final protected PreferenceScreen mMidiSettings;

	    final public ListPreference mLayoutFiles;

	    protected ProgressDialog mConnectingDialog = null;


		public SettingsView(SettingsModel settingsModel, IController controller) {
			mModel = settingsModel;
			mController = controller;

			mServerTypes = (ListPreference) findPreference( "settings_server_type" );
			mServerTypes.setOnPreferenceChangeListener(this);
			mServerConnectCheckBox = (CheckBoxPreference) findPreference( "settings_server_connect" );
			mServerConnectCheckBox.setOnPreferenceChangeListener(this);
			mServerAddressEditText = (EditTextPreference) findPreference( "settings_server_address" );
			mServerAddressEditText.setOnPreferenceChangeListener(this);
			mDevices = (ListPreference) findPreference( "settings_midi_out" );
			mDevices.setOnPreferenceChangeListener(this);
			mMidiSettings = (PreferenceScreen) findPreference( "settings_midi_controllers" );
			mLayoutFiles = (ListPreference) findPreference( "settings_layout_file" );
			mLayoutFiles.setOnPreferenceChangeListener(this);


			// Get all MIDI controllers and create individual settings for them 
			// on the MIDI controllers settings screen.
			Set<IMidiController> midiControllers = mModel.midiControllerManager.getMidiControllers();

			for (IMidiController mc : midiControllers) {
				Parameter parameters[] = mc.getParameters();

				// We do this so Buttons don't show up here but are still accounted
				// for in the MidiControllerManager. TODO Create a MIDIParameter class

				int numParameters = 0;
				if (parameters != null) {
					for (int i=0; i<parameters.length; i++) {
						if (parameters[i].visible)
							numParameters++;
					}
				}

				if (numParameters > 0) {
					PreferenceCategory pc = new PreferenceCategory(Settings.this);
					pc.setTitle("Configure " + mc.getName());
					mMidiSettings.addPreference(pc);

					for (int i=0; i<parameters.length; i++) {
						if (parameters[i].visible) {
							Preference p = new Preference(Settings.this);
							p.setKey(mc.getName() + "_" + parameters[i].id);
							p.setPersistent(false);
							p.setTitle("Send " + parameters[i].name);
							p.setSummary("Sends the " + parameters[i].name + " command to the server.");
							pc.addPreference(p);
						}
					}
				}
			}
		}

		public void update() {

			switch (mModel.state) {

				case SettingsModel.STATE_CONNECTING:
					mServerTypes.setEnabled(false);
					mServerAddressEditText.setEnabled(false);
					mServerConnectCheckBox.setEnabled(false);
					mServerConnectCheckBox.setSummary("Connecting to " + mModel.serverAddress);

			    	mConnectingDialog = ProgressDialog.show(Settings.this, "Please Wait", "Connecting to server..", true, false);

			    	break;

				case SettingsModel.STATE_CONNECTING_SUCCESS:
					mServerTypes.setEnabled(false);
					mServerAddressEditText.setEnabled(false);
					mServerConnectCheckBox.setEnabled(true);
					mServerConnectCheckBox.setChecked( true );
					mServerConnectCheckBox.setSummary("Connected to " + mModel.serverAddress);

					if (mConnectingDialog != null) {
						mConnectingDialog.dismiss();
						mConnectingDialog = null;
					}

			    	break;

				case SettingsModel.STATE_CONNECTING_FAIL:

					mServerTypes.setEnabled(true);
					mServerAddressEditText.setEnabled(true);
					mServerConnectCheckBox.setEnabled(true);
					mServerConnectCheckBox.setChecked(false);

					mServerConnectCheckBox.setSummary("Connection failed");

					if (mConnectingDialog != null) {
						mConnectingDialog.dismiss();
						mConnectingDialog = null;
					}

					break;

				case SettingsModel.STATE_CONNECTED:

					mServerConnectCheckBox.setTitle("Disconnect from Server");

					// Display MIDI Devices
					if (mModel.serverType == ConnectionManager.CONNECTION_TYPE_FINGERSERVER) {
						if (mModel.midiDevices != null) {
							CharSequence[] entries = new CharSequence[mModel.midiDevices.length];
							CharSequence[] entryValues = new CharSequence[mModel.midiDevices.length];
							for (int i=0; i<entries.length; i++) {
								entries[i] = mModel.midiDevices[i];
								entryValues[i] = mModel.midiDevices[i];
							}
	
							mDevices.setEnabled(true);
							mDevices.setEntries(entries);
							mDevices.setEntryValues(entryValues);
						}
	
						if (mModel.midiDevice != null) {
							mMidiSettings.setEnabled(true);
						}
					}

					break;
	
				case SettingsModel.STATE_DISCONNECTED:

					mServerTypes.setEnabled(true);
		    		mServerAddressEditText.setEnabled( (mModel.serverType != -1) );

					// Allow Server connect if we have address set.
		        	mServerConnectCheckBox.setChecked(false);
		        	mServerConnectCheckBox.setEnabled( (mModel.serverAddress != null && mModel.serverType > 0) );
					mServerConnectCheckBox.setTitle("Connect to Server");
		       		mServerConnectCheckBox.setSummary("Disconnected");

		       		mDevices.setEnabled(false);
		       		mMidiSettings.setEnabled(false);
					break;
			}


			// Update server type
    		if (mModel.serverType == ConnectionManager.CONNECTION_TYPE_OSC) {
    			mServerTypes.setSummary( "OSC Server" );
    		} else if (mModel.serverType == ConnectionManager.CONNECTION_TYPE_FINGERSERVER){
    			mServerTypes.setSummary( "FingerServer" );
    		} else {
    			mServerTypes.setSummary( "" );
    		}

    		// Update server address
    		String serverAddress = (mModel.serverAddress != null) ? mModel.serverAddress : "";
       		mServerAddressEditText.setText( serverAddress );
        	mServerAddressEditText.setSummary( serverAddress );

			// We want to update the summary even if we're disconnected
    		if (mModel.midiDevice != null) {
        		mDevices.setValue( mModel.midiDevice );
    	    	mDevices.setSummary( mModel.midiDevice );
    		} else if (mModel.midiDevices != null)
    			mDevices.setSummary( "None selected (" + mModel.midiDevices.length + ")" );
    		else
    			mDevices.setSummary( "None selected" );

    		// Update the layouts
    		if (mModel.layoutFiles != null) {
    			mLayoutFiles.setEnabled(true);
    			mLayoutFiles.setEntries(mModel.layoutFiles);
    			mLayoutFiles.setEntryValues(mModel.layoutFiles);
    			mLayoutFiles.setValue( mModel.layoutFile );
    		} else {
    			mLayoutFiles.setEnabled(false);
    		}

    		if (mModel.layoutFile != null)
    			mLayoutFiles.setSummary( mModel.layoutFile );
    		else if (mModel.layoutFiles != null)
    			mLayoutFiles.setSummary( "Default" );
    		else
    			mLayoutFiles.setSummary( "/FingerPlayMIDI/<xml..>" );

		}

		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			mController.onPreferenceChange(preference, newValue);
			return false;
		}
		
		

	}

}