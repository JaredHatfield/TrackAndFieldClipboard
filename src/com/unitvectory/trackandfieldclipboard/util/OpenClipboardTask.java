/*
 * Track and Field Clipboard
 * Copyright 2011 Jared Hatfield.  All rights reserved.
 */
package com.unitvectory.trackandfieldclipboard.util;

import java.io.FileInputStream;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.unitvectory.trackandfieldclipboard.R;
import com.unitvectory.trackandfieldclipboard.model.FieldEvent;
import com.unitvectory.trackandfieldclipboard.ui.DistanceClipboardActivity;

/**
 * Task for opening and deserializing a file that is used to launch a task.
 * 
 * @author Jared Hatfield
 * 
 */
public class OpenClipboardTask extends AsyncTask<String, Integer, FieldEvent> {

    /**
     * The tag used for logging.
     */
    private static final String TAG = "TrackAndFieldClipboard";

    /**
     * The context that will be used.
     */
    private Activity activity;

    /**
     * The name of the file.
     */
    private String filename;

    /**
     * Initializes a new instance of the OpenClipboardTask.
     * 
     * @param activity
     *            The activity.
     */
    public OpenClipboardTask(Activity activity) {
        this.activity = activity;
    }

    /**
     * Performs the action.
     * 
     * @param args
     *            The arguments.
     * @return The loaded event.
     */
    @Override
    protected FieldEvent doInBackground(String... args) {
        try {
            this.filename = args[0];
            FileInputStream input = activity.openFileInput(this.filename);
            Serializer serializer = new Persister();
            FieldEvent event = serializer.read(FieldEvent.class, input);
            return event;
        } catch (Exception e) {
            Log.e(OpenClipboardTask.TAG, e.getMessage());
            return null;
        }
    }

    /**
     * Processes the result.
     * 
     * @param event
     *            The loaded event or null on failure.
     */
    @Override
    protected void onPostExecute(FieldEvent event) {
        if (event == null) {
            Toast toast = Toast.makeText(activity, R.string.failed_open,
                    Toast.LENGTH_LONG);
            toast.show();
        } else {
            Intent intent = new Intent(activity,
                    DistanceClipboardActivity.class);
            intent.putExtra("event", event);
            intent.putExtra("filename", this.filename);
            activity.startActivity(intent);
        }
    }
}
