package com.example.mkim123.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Created by mkim123 on 3/17/2016.
 */
public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String stringVal = newValue.toString();

        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringVal);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            preference.setSummary(stringVal);
        }
        setResult(stringVal);
        return true;
    }

    private void bindPreferenceSummaryToVal(Preference pref) {
        pref.setOnPreferenceChangeListener(this);

        onPreferenceChange(pref, PreferenceManager
                .getDefaultSharedPreferences(pref.getContext())
                .getString(pref.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);
        bindPreferenceSummaryToVal(findPreference(getString(R.string.pref_sort_key)));
    }

    private void setResult(String newVal) {
        Intent data = new Intent();
        data.putExtra("SortBy", newVal);
        setResult(RESULT_OK, data);
    }
}
