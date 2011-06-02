/*
 * Track and Field Clipboard
 * Copyright 2011 Jared Hatfield.  All rights reserved.
 */
package com.unitvectory.trackandfieldclipboard.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

        this.fieldName = (EditText) this.findViewById(R.id.editText_event_name);
        this.fieldType = this.wireUpSpinner(R.id.spinner_event_type,
                R.array.event_types);
        this.fieldGender = this.wireUpSpinner(R.id.spinner_event_gender,
                R.array.genders);
        this.fieldDate = (EditText) this.findViewById(R.id.editText_event_date);
        this.fieldQualifying = this.wireUpSpinner(
                R.id.spinner_event_qualifying_marks, R.array.marks);
        this.fieldQualifying.setSelection(3);
        this.fieldFinals = this.wireUpSpinner(R.id.spinner_event_final_marks,
                R.array.marks);
        this.fieldFinals.setSelection(3);
        this.fieldFinalParticipants = (EditText) this
                .findViewById(R.id.editText_event_final_participants);
        this.fieldFlights = (EditText) this
                .findViewById(R.id.editText_event_flights);
        this.fieldUnits = this.wireUpSpinner(R.id.spinner_event_units,
                R.array.units);
        this.setResult(RESULT_CANCELED);
    }

    /**
     * Handles the add event click actions.
     * 
     * @param v
     *            The calling view.
     */
    public void onAddClick(View v) {
        String name = this.fieldName.getText().toString();
        if (name.equals("")) {
            name = "No name.";
        }

        EventType type = FieldEvent.parseEventType((String) this.fieldType
                .getSelectedItem());
        Gender gender = FieldEvent.parseGender((String) this.fieldGender
                .getSelectedItem());
        String date = this.fieldDate.getText().toString();
        if (date.equals("")) {
            date = "No date.";
        }

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
