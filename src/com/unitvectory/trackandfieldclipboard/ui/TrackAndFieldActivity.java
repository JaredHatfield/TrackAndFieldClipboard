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
     * The file list fragment.
     */
    private FileListFragment fileListFragment;

    /**
     * The dirty flag.
     */
    private int dirtyFlag;

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
        this.fileListFragment = (FileListFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_file_list);
        this.dirtyFlag = 0;
    }

    /**
     * Restore the activity and reload the list of files if necessary.
     */
    @Override
    public void onResume() {
        super.onPause();
        if (this.dirtyFlag > 0) {
            this.fileListFragment.displayFiles();
            this.dirtyFlag--;
        }
    }

    /**
     * Handle the new FieldEvent event.
     * 
     * @param v
     *            The calling view.
     */
    public void onNewEventClick(View v) {
        // Launch the new event activity
        Intent intent = new Intent(this, NewEventActivity.class);
        this.startActivityForResult(intent, NEW_EVENT_REQUEST);
    }

    /**
     * Handle the import data event.
     * 
     * @param v
     *            The calling view.
     */
    public void onImportDataClick(View v) {
        // Launch the import data activity
        Intent intent = new Intent(this, HyTekImportActivity.class);
        this.startActivityForResult(intent, NEW_EVENT_REQUEST);
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
                this.dirtyFlag += 2;
            }
        }
    }
}