/*
 * Track and Field Clipboard
 * Copyright 2011 Jared Hatfield.  All rights reserved.
 */
package com.unitvectory.trackandfieldclipboard.util;

import java.io.FileOutputStream;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.os.AsyncTask;

import com.unitvectory.trackandfieldclipboard.model.FieldEvent;

/**
 * Task for saving the event file.
 * 
 * @author Jared Hatfield
 * 
 */
public class SaveClipboardTask extends AsyncTask<FieldEvent, Integer, Integer> {

    /**
     * The file to save.
     */
    private FileOutputStream output;

    /**
     * Initializes a new instance of the SaveClipboardTask.
     * 
     * @param output
     *            The output stream.
     */
    public SaveClipboardTask(FileOutputStream output) {
        this.output = output;
    }

    /**
     * Saves the event to a file.
     * 
     * @param arg
     *            The field event.
     * @return A result.
     */
    @Override
    protected Integer doInBackground(FieldEvent... arg) {
        try {
            synchronized (FieldEvent.WRITELOCK) {
                FieldEvent event = arg[0];
                Serializer serializer = new Persister();
                serializer.write(event, this.output);
                output.close();
            }
        } catch (Exception e) {
        }

        return 0;
    }

}
