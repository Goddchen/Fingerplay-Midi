package com.flat20.fingerplay.settings;

import android.preference.Preference;

public interface IController {
	public void onPreferenceChange(Preference preference, Object newValue);
}

