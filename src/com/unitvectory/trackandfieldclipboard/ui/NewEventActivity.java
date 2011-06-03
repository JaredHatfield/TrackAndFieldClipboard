/*
 * Track and Field Clipboard
 * Copyright 2011 Jared Hatfield.  All rights reserved.
 */
package com.unitvectory.trackandfieldclipboard.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.unitvectory.trackandfieldclipboard.R;
import com.unitvectory.trackandfieldclipboard.model.EventType;
import com.unitvectory.trackandfieldclipboard.model.FieldEvent;
import com.unitvectory.trackandfieldclipboard.model.Gender;

/**
 * The activity used to create a new event.
 * 
 * @author Jared Hatfield
 * 
 */
public class NewEventActivity extends Activity {

    /**
     * The name of the shared preferences file.
     */
    public static final String PREFS_NAME = "TrackAndFieldClipboardPrefs";

    /**
     * The event name.
     */
    private EditText fieldName;

    /**
     * The event type.
     */
    private Spinner fieldType;

    /**
     * The event gender.
     */
    private Spinner fieldGender;

    /**
     * The event date.
     */
    private EditText fieldDate;

    /**
     * The number of qualifying marks.
     */
    private Spinner fieldQualifying;

    /**
     * The number of final marks.
     */
    private Spinner fieldFinals;

    /**
     * The number of participants that move on to the final round.
     */
    private EditText fieldFinalParticipants;

    /**
     * The number of flights.
     */
    private EditText fieldFlights;

    /**
     * The type of units to use.
     */
    private Spinner fieldUnits;

    /**
     * Called when the activity is first created.
     * 
     * @param savedInstanceState
     *            The saved settings.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        // Change the name in the ActionBar
        this.getActionBar().setTitle(R.string.new_event);

        // Get the shared preferences for the application
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        // The event name
        this.fieldName = (EditText) this.findViewById(R.id.editText_event_name);

        // The event type
        this.fieldType = this.wireUpSpinner(R.id.spinner_event_type,
                R.array.event_types);

        // The event gender
        this.fieldGender = this.wireUpSpinner(R.id.spinner_event_gender,
                R.array.genders);

        // The event date
        this.fieldDate = (EditText) this.findViewById(R.id.editText_event_date);

        // The qualifying marks (user saved default)
        this.fieldQualifying = this.wireUpSpinner(
                R.id.spinner_event_qualifying_marks, R.array.marks);
        this.fieldQualifying.setSelection(settings.getInt("newQualifying", 3));

        // The finals marks (user saved default)
        this.fieldFinals = this.wireUpSpinner(R.id.spinner_event_final_marks,
                R.array.marks);
        this.fieldFinals.setSelection(settings.getInt("newFinals", 3));

        // The final participants (user saved default)
        this.fieldFinalParticipants = (EditText) this
                .findViewById(R.id.editText_event_final_participants);
        this.fieldFinalParticipants.setText(settings.getString(
                "newFinalParticipants", "10"));

        // The number of flights (user saved default)
        this.fieldFlights = (EditText) this
                .findViewById(R.id.editText_event_flights);
        this.fieldFlights.setText(settings.getString("newFlights", "1"));

        // The units (user saved default)
        this.fieldUnits = this.wireUpSpinner(R.id.spinner_event_units,
                R.array.units);
        this.fieldUnits.setSelection(settings.getInt("newUnits", 0));

        // Set the default result to be canceled
        this.setResult(RESULT_CANCELED);
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
            // App icon in Action Bar clicked; go home
            Intent intent = new Intent(this, TrackAndFieldActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Handles the add event click actions.
     * 
     * @param v
     *            The calling view.
     */
    public void onAddClick(View v) {
        String name = this.fieldName.getText().toString();
        EventType type = FieldEvent.parseEventType((String) this.fieldType
                .getSelectedItem());
        Gender gender = FieldEvent.parseGender((String) this.fieldGender
                .getSelectedItem());
        String date = this.fieldDate.getText().toString();
        int qualifying = Integer.parseInt((String) this.fieldQualifying
                .getSelectedItem());
        int finals = Integer.parseInt((String) this.fieldFinals
                .getSelectedItem());
        int finalParticipants = this
                .getIntFromEditText(this.fieldFinalParticipants);
        int flights = this.getIntFromEditText(this.fieldFlights);
        String units = (String) this.fieldUnits.getSelectedItem();
        boolean metric = true;
        if (units.equalsIgnoreCase("US")) {
            metric = false;
        }

        // Save all of the new values in the shared preferences so they will be
        // the same the next time the user creates an event
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("newQualifying",
                this.fieldQualifying.getSelectedItemPosition());
        editor.putInt("newFinals", this.fieldFinals.getSelectedItemPosition());
        editor.putString("newFinalParticipants", finalParticipants + "");
        editor.putString("newFlights", flights + "");
        editor.putInt("newUnits", this.fieldUnits.getSelectedItemPosition());
        editor.commit();

        // Return the Event to the activity that called this intent.
        FieldEvent event = new FieldEvent(name, type, gender, date, qualifying,
                finals, finalParticipants, flights, metric);
        Intent in = new Intent();
        in.putExtra("event", event);
        this.setResult(RESULT_OK, in);
        this.finish();
    }

    /**
     * Gets an integer values for a text field.
     * 
     * This is designed to be used on an EditText field that has the input type
     * set to be a number to avoid possible errors.
     * 
     * @param field
     *            The edit text.
     * @return The integer values.
     */
    private int getIntFromEditText(EditText field) {
        String val = field.getText().toString();
        if (val.length() == 0) {
            return 0;
        }

        try {
            return Integer.parseInt(val);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Loads the options for the specified spinner given the specified array.
     * 
     * @param spinnerId
     *            The spinner resource.
     * @param listId
     *            The string array resource.
     * @return The spinner object.
     */
    private Spinner wireUpSpinner(int spinnerId, int listId) {
        Spinner spinner = (Spinner) this.findViewById(spinnerId);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, listId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        return spinner;
    }
}
