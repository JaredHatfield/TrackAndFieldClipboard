package com.unitvectory.trackandfieldclipboard.ui;

import android.app.Activity;
import android.os.Bundle;

import com.unitvectory.trackandfieldclipboard.R;
import com.unitvectory.trackandfieldclipboard.model.Event;

public class DistanceClipboardActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_clipboard);
        Event event = (Event) savedInstanceState.getSerializable("event");
    }
}
