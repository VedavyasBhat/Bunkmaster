package com.example.bunkmasterfull.options;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.bunkmasterfull.R;

public class Settings extends PreferenceFragment
{
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{        
        super.onCreate(savedInstanceState);        
        addPreferencesFromResource(R.xml.preferences);
    }
}
