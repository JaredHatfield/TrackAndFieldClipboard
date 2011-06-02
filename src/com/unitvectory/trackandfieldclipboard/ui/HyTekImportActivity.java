/*
 * Track and Field Clipboard
 * Copyright 2011 Jared Hatfield.  All rights reserved.
 */
package com.unitvectory.trackandfieldclipboard.ui;

import android.app.Activity;
import android.os.Bundle;

import com.unitvectory.trackandfieldclipboard.R;

/**
 * The activity that allows the user to import a HY-TEK file containing event
 * information.
 * 
 * @author Jared Hatfield
 * 
 */
public class HyTekImportActivity extends Activity {

    /**
     * Called when the activity is first created.
     * 
     * @param savedInstanceState
     *            The saved settings.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hytek_import);
        // TODO: Read in the content
    }
}
