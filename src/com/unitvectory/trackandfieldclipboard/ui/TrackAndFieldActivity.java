/*
 * Track and Field Clipboard
 * Copyright 2011 Jared Hatfield.  All rights reserved.
 */
package com.unitvectory.trackandfieldclipboard.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.unitvectory.trackandfieldclipboard.R;
import com.unitvectory.trackandfieldclipboard.hytek.ParseHyTekFileTask;
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
    private static final int NEW_EVENT_REQUEST = 639;

    /**
     * The constant used for handling a request to import a HY-TEK file.
     */
    private static final int ACTIVITY_CHOOSE_FILE = 4173;

    /**
     * The fragment list.
     */
    private FileListFragment fragmentList;

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
        this.fragmentList =
                (FileListFragment) getFragmentManager().findFragmentById(
                        R.id.fragment_file_list);
    }

    /**
     * Creates the action bar.
     * 
     * @param menu
     *            The menu to manipulate.
     * @return You must return true for the menu to be displayed; if you return
     *         false it will not be shown.
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
     * @return Return false to allow normal menu processing to proceed, true to
     *         consume it here.
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
                Intent sampleIntent =
                        new Intent(this, DistanceClipboardActivity.class);
                sampleIntent.putExtra("event", event);
                sampleIntent.putExtra("filename", event.newFileName());
                this.startActivity(sampleIntent);
                return true;
            case R.id.menu_import_hy_tek:
                this.importHyTek();
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
    protected void
            onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_EVENT_REQUEST) {
            // Displays the activity for the new exercise
            if (resultCode == RESULT_OK) {
                FieldEvent event =
                        (FieldEvent) data.getExtras().getSerializable("event");
                Intent intent =
                        new Intent(this, DistanceClipboardActivity.class);
                intent.putExtra("event", event);
                intent.putExtra("filename", event.newFileName());
                this.startActivity(intent);
            }
        } else if (requestCode == ACTIVITY_CHOOSE_FILE) {
            // Try and parse the HY-TEK results file
            if (resultCode == RESULT_OK) {
                if (data.hasExtra(FilePickerActivity.EXTRA_FILE_PATH)) {
                    // Get the file path
                    String filePath =
                            data.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH);
                    ProgressDialog progressDialog;
                    progressDialog = new ProgressDialog(this);
                    progressDialog
                            .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.setMessage(this.getString(R.string.loading));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    ParseHyTekFileTask parseTask =
                            new ParseHyTekFileTask(this.getFilesDir(),
                                    this.fragmentList, progressDialog);
                    parseTask.execute(filePath);
                }
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

    /**
     * Launches the get content intent.
     */
    private void importHyTek() {
        // Create a new Intent for the file picker activity
        Intent fileintent = new Intent(this, FilePickerActivity.class);

        // Set the initial directory to be the sdcard
        // fileintent.putExtra(FilePickerActivity.EXTRA_FILE_PATH,
        // Environment.getExternalStorageDirectory());

        // Show hidden files
        // fileintent.putExtra(FilePickerActivity.EXTRA_SHOW_HIDDEN_FILES,
        // true);

        // Only make .xml files visible
        ArrayList<String> extensions = new ArrayList<String>();
        extensions.add(".txt");
        fileintent.putExtra(FilePickerActivity.EXTRA_ACCEPTED_FILE_EXTENSIONS,
                extensions);

        // Start the activity
        startActivityForResult(fileintent, ACTIVITY_CHOOSE_FILE);

    }
}