/*
 * Track and Field Clipboard
 * Copyright 2011 Jared Hatfield.  All rights reserved.
 */
package com.unitvectory.trackandfieldclipboard.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.unitvectory.trackandfieldclipboard.R;
import com.unitvectory.trackandfieldclipboard.model.FieldEvent;

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

    public void onTestEventClick(View v) {
        // TODO: Delete test code
        // Launch the new event activity
        FieldEvent event = FieldEvent.example();
        Intent intent = new Intent(this, DistanceClipboardActivity.class);
        intent.putExtra("event", event);
        this.startActivity(intent);
    }

    public void onNewEventClick(View v) {
        // TODO: Delete test code
        // Launch the new event activity
        Intent intent = new Intent(this, NewEventActivity.class);
        this.startActivityForResult(intent, NEW_EVENT_REQUEST);
    }

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
                this.startActivity(intent);
            }
        }
    }
}