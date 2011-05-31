package com.unitvectory.trackandfieldclipboard.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.unitvectory.trackandfieldclipboard.R;
import com.unitvectory.trackandfieldclipboard.model.Event;
import com.unitvectory.trackandfieldclipboard.model.EventType;
import com.unitvectory.trackandfieldclipboard.model.Gender;

public class NewEventActivity extends Activity {

    private EditText fieldName;

    private Spinner fieldType;

    private Spinner fieldGender;

    private EditText fieldDate;

    private Spinner fieldQualifying;

    private Spinner fieldFinals;

    private EditText fieldFinalParticipants;

    private EditText fieldFlights;

    private Spinner fieldUnits;

    /** Called when the activity is first created. */
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
        this.fieldFinals = this.wireUpSpinner(R.id.spinner_event_final_marks,
                R.array.marks);
        this.fieldFinalParticipants = (EditText) this
                .findViewById(R.id.editText_event_final_participants);
        this.fieldFlights = (EditText) this
                .findViewById(R.id.editText_event_flights);
        this.fieldUnits = this.wireUpSpinner(R.id.spinner_event_units,
                R.array.units);
        this.setResult(RESULT_CANCELED);
    }

    public void onAddClick(View v) {
        String name = this.fieldName.getText().toString();
        EventType type = Event.parseEventType((String) this.fieldType
                .getSelectedItem());
        Gender gender = Event.parseGender((String) this.fieldGender
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

        // Return the Event to the activity that called this intent.
        Event event = new Event(name, type, gender, date, qualifying, finals,
                finalParticipants, flights, metric);
        Intent in = new Intent();
        in.putExtra("event", event);
        this.setResult(RESULT_OK, in);
        this.finish();
    }

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

    private Spinner wireUpSpinner(int spinnerId, int listId) {
        Spinner spinner = (Spinner) this.findViewById(spinnerId);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, listId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        return spinner;
    }
}
