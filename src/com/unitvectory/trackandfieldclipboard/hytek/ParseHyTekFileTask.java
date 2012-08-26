/*
 * Track and Field Clipboard
 * Copyright 2011 Jared Hatfield.  All rights reserved.
 */
package com.unitvectory.trackandfieldclipboard.hytek;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.unitvectory.trackandfieldclipboard.R;
import com.unitvectory.trackandfieldclipboard.model.FieldEvent;
import com.unitvectory.trackandfieldclipboard.ui.FileListFragment;

/**
 * Performs the HY-TEK import processing.
 * 
 * @author Jared Hatfield
 * 
 */
public class ParseHyTekFileTask extends AsyncTask<String, Integer, Integer> {

    /**
     * The directory to save all of the new files in.
     */
    private File directory;

    /**
     * The FileListFragment that contains all of the files.
     */
    private FileListFragment listFragment;

    /**
     * The loading dialog.
     */
    private ProgressDialog dialog;

    /**
     * Initializes a new instance of the ParseHyTekFileTask class.
     * 
     * @param directory
     *            The directory to save all of the new files.
     * @param listFragment
     *            The file list fragment.
     * @param dialog
     *            The dialog that will be dismissed.
     */
    public ParseHyTekFileTask(File directory, FileListFragment listFragment,
            ProgressDialog dialog) {
        this.directory = directory;
        this.listFragment = listFragment;
        this.dialog = dialog;
    }

    /**
     * Loads the external file, parses the content and creates all of the
     * FieldEvents.
     * 
     * @param arg
     *            The name of the file to load.
     * @return The result.
     */
    @Override
    protected Integer doInBackground(String... arg) {
        String filename = arg[0];
        List<String> lines = new ArrayList<String>();
        BufferedReader br = null;
        try {
            File file = new File(filename);
            br = new BufferedReader(new FileReader(file));

            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception e) {
            return -1;
        } finally {
        	if(br != null){
        		try {
					br.close();
				} catch (IOException e) {
				}
        	}
        }

        try {
            String[] array = lines.toArray(new String[lines.size()]);

            // Parse the file that was read into the array into objects
            HyTekParser hytek = new HyTekParser(array);

            // Loop through all of the results
            Serializer serializer = new Persister();
            int totalEvents = hytek.getEvents().size();
            for (int i = 0; i < totalEvents; i++) {
                FieldEvent event = hytek.getEvents().get(i);
                File file = new File(this.directory, event.newFileName());

                // Write the event file
                synchronized (FieldEvent.WRITELOCK) {
                    FileOutputStream output = new FileOutputStream(file);
                    serializer.write(event, output);
                    output.close();
                }

                // Update the progress
                this.publishProgress(totalEvents, i);
            }
        } catch (Exception e) {
            return -2;
        }

        return 1;
    }

    /**
     * Update the progress dialog.
     * 
     * @param progress
     *            The values to set for the progress bar.
     */
    @Override
    protected void onProgressUpdate(Integer... progress) {
        try {
            this.dialog.setMax(progress[0].intValue());
            this.dialog.setProgress(progress[1].intValue());
        } catch (Exception e) {

        }
    }

    /**
     * Closes the progress dialog and updates the list of files.
     * 
     * @param result
     *            The result.
     */
    @Override
    protected void onPostExecute(Integer result) {
        if (result != null && result.intValue() == 1) {
            // Update the list of files if the result was successful
            try {
                this.listFragment.displayFiles();

            } catch (Exception e) {
            }
        } else {
            // Display a message saying it failed
            try {
                Toast toast =
                        Toast.makeText(this.listFragment.getActivity(),
                                R.string.error_hytek, Toast.LENGTH_SHORT);
                toast.show();
            } catch (Exception e) {
            }
        }

        // We always want to remove the dialog since it is sticky.
        try {
            this.dialog.dismiss();
        } catch (Exception e) {
        }
    }
}
