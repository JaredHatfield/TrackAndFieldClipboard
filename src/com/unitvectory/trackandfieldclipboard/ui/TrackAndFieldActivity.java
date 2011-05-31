package com.unitvectory.trackandfieldclipboard.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.unitvectory.trackandfieldclipboard.R;
import com.unitvectory.trackandfieldclipboard.model.Event;

public class TrackAndFieldActivity extends Activity {

    static final int NEW_EVENT_REQUEST = 639;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_and_field);
    }

    public void onNewEventClick(View v) {
        // Launch the new event activity
        Intent intent = new Intent(this, NewEventActivity.class);
        this.startActivityForResult(intent, NEW_EVENT_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_EVENT_REQUEST) {
            if (resultCode == RESULT_OK) {
                Event event = (Event) data.getExtras().getSerializable("event");
            }
        }
    }
}