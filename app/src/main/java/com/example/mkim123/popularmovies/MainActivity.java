package com.example.mkim123.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int REQ_CODE_SETTINGS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String sortBy = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_def));
        updateTitle(sortBy);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_SETTINGS) {
            if (resultCode == RESULT_OK) {
                String sortBy = data.getStringExtra("SortBy");
                updateTitle(sortBy);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivityForResult(new Intent(this, SettingsActivity.class), REQ_CODE_SETTINGS);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateTitle(String sortBy) {
        String sortByLabel;
        if (sortBy.equals(getString(R.string.pref_sort_label_popular))) {
            sortByLabel = getString(R.string.pref_sort_option_popular);
        } else {
            sortByLabel = getString(R.string.pref_sort_option_rated);
        }

        this.setTitle(getString(R.string.app_name) + " By: " + sortByLabel);
    }
}
