/*
 * Track and Field Clipboard
 * Copyright 2011 Jared Hatfield.  All rights reserved.
 */
package com.unitvectory.trackandfieldclipboard.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.unitvectory.trackandfieldclipboard.R;
import com.unitvectory.trackandfieldclipboard.model.FieldEvent;
import com.unitvectory.trackandfieldclipboard.model.RandomEventGenerator;

/**
 * The main activity for TrackAndFieldClipboard.
 * 
 * @author Jared Hatfield
 * 
 */
public class TrackAndFieldActivity extends Activity {

    /**
     * The constant used for handling a request for a new event.
     */
    static final int NEW_EVENT_REQUEST = 639;

    /**
     * Called when the activity is first created.
     * 
     * @param savedInstanceState
     *            The saved settings.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_and_field);
    }

    /**
     * Creates the action bar.
     * 
     * @param menu
     *            The menu to manipulate.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_track_and_field, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Handle the clicks from the action bar.
     * 
     * @param item
     *            The menu item clicked.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            // Display the about application dialog
            this.showAboutDialog();
            return true;
        case R.id.menu_new_event:
            // Launch the new event activity
            Intent intent = new Intent(this, NewEventActivity.class);
            this.startActivityForResult(intent, NEW_EVENT_REQUEST);
            return true;
        case R.id.menu_sample_results:
            // Generate a sample event
            RandomEventGenerator reg = new RandomEventGenerator();
            FieldEvent event = reg.getEvent();
            Intent sampleIntent = new Intent(this,
                    DistanceClipboardActivity.class);
            sampleIntent.putExtra("event", event);
            sampleIntent.putExtra("filename", event.newFileName());
            this.startActivity(sampleIntent);
            return true;
        case R.id.menu_about:
            // Display the about application dialog
            this.showAboutDialog();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Process the results from a new event.
     * 
     * @param requestCode
     *            The request code.
     * @param resultCode
     *            The result code.
     * @param data
     *            The results.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO: Delete test code
        if (requestCode == NEW_EVENT_REQUEST) {
            if (resultCode == RESULT_OK) {
                FieldEvent event = (FieldEvent) data.getExtras()
                        .getSerializable("event");
                Intent intent = new Intent(this,
                        DistanceClipboardActivity.class);
                intent.putExtra("event", event);
                intent.putExtra("filename", event.newFileName());
                this.startActivity(intent);
            }
        }
    }

    /**
     * Display the about dialog.
     */
    private void showAboutDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_about);
        dialog.setTitle(R.string.about);
        dialog.setCancelable(true);
        dialog.show();
    }
}