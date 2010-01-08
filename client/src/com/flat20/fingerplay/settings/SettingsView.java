package com.flat20.fingerplay.settings;

import java.util.Set;

import android.app.ProgressDialog;
import android.os.Bundle;
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

public class SettingsView extends PreferenceActivity implements Preference.OnPreferenceChangeListener { 

	private SettingsModel mModel;
	private SettingsController mController;

	
    protected ListPreference mServerTypes;
    protected CheckBoxPreference mServerConnectCheckBox;
    protected EditTextPreference mServerAddressEditText;

    public ListPreference mLayoutFiles;

    protected ListPreference mDevices;
    protected PreferenceScreen mMidiSettings;


    protected ProgressDialog mConnectingDialog = null;

	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);
		//mModel = SettingsModel.getInstance();
		//mViewComponent = viewComponent;

        addPreferencesFromResource(R.layout.settings_view);

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

		// init MVC
        //mView = new SettingsView(this);
		mController = new SettingsController(this);
		setController(mController);
		mModel = SettingsModel.getInstance();
		mModel.setView(this);

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
				PreferenceCategory pc = new PreferenceCategory(this);
				pc.setTitle("Configure " + mc.getName());
				mMidiSettings.addPreference(pc);

				for (int i=0; i<parameters.length; i++) {
					if (parameters[i].visible) {
						Preference p = new Preference(this);
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

	@Override
	protected void onDestroy() {
		super.onDestroy();

		mController.destroy();
	}

	public void setController(SettingsController controller) {
		mController = controller;
	}

	public void displayError(String errorMessage) {
		Toast info = Toast.makeText(this, errorMessage, Toast.LENGTH_LONG);
		info.show();
	}

	public void update() {

		switch (mModel.state) {

			case SettingsModel.STATE_CONNECTING:
				mServerTypes.setEnabled(false);
				mServerAddressEditText.setEnabled(false);
				mServerConnectCheckBox.setEnabled(false);
				mServerConnectCheckBox.setSummary("Connecting to " + mModel.serverAddress);

		    	mConnectingDialog = ProgressDialog.show(this, "Please Wait", "Connecting to server..", true, false);

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
		if (preference == mServerTypes) {
			mController.setConnectionType( Integer.parseInt((String)newValue) );
			return true;
		} else if (preference == mServerAddressEditText) {
			mModel.setServerAddress((String) newValue);
			return true;
		} else if (preference == mDevices) {
			mController.setMidiDevice((String) newValue);
			return true;
		} else if (preference == mLayoutFiles) {
			mModel.setLayoutFile((String) newValue);
			return true;
		}
		return false;
	}

    @Override
    public boolean onPreferenceTreeClick (PreferenceScreen preferenceScreen, Preference preference) {

    	if (preference == null || preference.getKey() == null)
    		return true;

    	if (preference.getKey().equals( "settings_server_connect" )) {
    		mController.serverConnect();

    	} else if (preferenceScreen.getKey() != null && preferenceScreen.getKey().equals("settings_midi_controllers")) {
    		// TODO shouldn't have to split the key name here. create a MidiCCPreference or something
    		String key = preference.getKey();
    		String name[] = key.split("_");
    		String controllerName = name[0];
    		int index = Integer.parseInt(name[1]);
    		mController.sendControlChange(controllerName, index);
    	}

    	return true;
    }

}
