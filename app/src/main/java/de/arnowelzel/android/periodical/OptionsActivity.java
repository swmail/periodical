/*
 * Periodical options activity
 * Copyright (C) 2012-2018 Arno Welzel
 *
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.arnowelzel.android.periodical;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Activity to handle the "Preferences" command
 */
public class OptionsActivity extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    
    /**
     * Called when activity starts
     */
    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final Context context = getApplicationContext();
        assert context != null;
        
        addPreferencesFromResource(R.xml.preferences);
        initSummary(getPreferenceScreen());
        
        // Add validation for period length
        findPreference("period_length").setOnPreferenceChangeListener(
            new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int value;
                    try {
                        value = Integer.parseInt(newValue.toString());
                    } catch (NumberFormatException e) {
                        value = 0;
                    }

                    if (value < 1 || value > 14) {
                        Toast.makeText(context,
                                getResources().getString(R.string.invalid_period_length),
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    
                    return true;
                }
            });

        // Add validation for luteal length
        findPreference("luteal_length").setOnPreferenceChangeListener(
            new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int value;
                    try {
                        value = Integer.parseInt(newValue.toString());
                    } catch (NumberFormatException e) {
                        value = 0;
                    }

                    if (value < 1) {
                        Toast.makeText(context,
                                getResources().getString(R.string.invalid_luteal_length),
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    return true;
                }
            });

        // Add validation for cycle length filter
        findPreference("maximum_cycle_length").setOnPreferenceChangeListener(
            new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int value;
                    try {
                        value = Integer.parseInt(newValue.toString());
                    } catch (NumberFormatException e) {
                        value = 0;
                    }

                    if (value < 1) {
                        Toast.makeText(context,
                                getResources().getString(R.string.invalid_maximum_cycle_length),
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    return true;
                }
            });

        // Activate "back button" in Action Bar if possible
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Called when the activity starts interacting with the user
     */
    @Override
    @SuppressWarnings("deprecation")
    protected void onResume() {
        super.onResume();
        
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * Called when activity pauses
     */
    @Override
    @SuppressWarnings("deprecation")
    protected void onPause() {
        super.onPause();
        
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }    
    
    /**
     * Update summary of changed preferences
     */
    @SuppressWarnings("deprecation")
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);
        
        updatePrefSummary(pref);
    }

    /**
     * Set initial summary texts
     */
    private void initSummary(Preference p) {
        if (p instanceof PreferenceGroup) {
            PreferenceGroup pGrp = (PreferenceGroup) p;
            for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
                initSummary(pGrp.getPreference(i));
            }
        } else {
            updatePrefSummary(p);
        }
    }
    
    /**
     * Update summary text for a preference
     */
    private void updatePrefSummary(Preference p) {
        if (p instanceof ListPreference) {
            ListPreference listPref = (ListPreference) p;
            p.setSummary(listPref.getEntry());
        }
        if (p instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            if (p.getTitle().toString().contains("assword"))
            {
                p.setSummary("******");
            } else {
                p.setSummary(editTextPref.getText());
            }
        }
    }
    
    /**
     * Handler for ICS "home" button
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Home icon in action bar clicked, then close activity
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}