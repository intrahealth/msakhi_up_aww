package org.intrahealth.mnewborncare.awc;

import org.intrahealth.mnewborncare.awc.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class EditPreference extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.app_settings);
	}	
}
