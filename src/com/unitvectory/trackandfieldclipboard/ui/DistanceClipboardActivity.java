/*
 * Track and Field Clipboard
 * Copyright 2011 Jared Hatfield.  All rights reserved.
 */
package com.unitvectory.trackandfieldclipboard.ui;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.unitvectory.trackandfieldclipboard.R;
import com.unitvectory.trackandfieldclipboard.model.FieldEvent;
import com.unitvectory.trackandfieldclipboard.model.Measurement;
import com.unitvectory.trackandfieldclipboard.model.Participant;
import com.unitvectory.trackandfieldclipboard.util.AthleteRowHolder;

/**
 * The clipboard activity used to record measurements for a specific event.
 * 
 * @author Jared Hatfield
 * 
 */
public class DistanceClipboardActivity extends Activity implements
        OnClickListener {

    /**
     * The tag used for logging.
     */
    private static final String TAG = "TrackAndFieldClipboard";

    /**
     * The participants table.
     */
    private TableLayout participants;

    /**
     * The map of rows used to update the content.
     */
    private Map<Integer, AthleteRowHolder> rows;

    /**
     * The event being manipulated.
     */
    private FieldEvent event;

    /**
     * The filename that is used for saving.
     */
    private String filename;

    /**
     * The spinner used to select the flight.
     */
    private Spinner spinnerFlight;

    /**
     * The current name.
     */
    private TextView currentName;

    /**
     * The current attempt.
     */
    private TextView currentAttempt;

    /**
     * The button to mark a distance.
     */
    private Button buttonMark;

    /**
     * The button to mark a scratch.
     */
    private Button buttonScratch;

    /**
     * The view that was last clicked that needs to have a highlight removed.
     */
    private View lastClicked;

    /**
     * Called when the activity is first created.
     * 
     * @param savedInstanceState
     *            The application's saved settings.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_clipboard);

        if (this.event == null) {
            this.event = (FieldEvent) this.getIntent().getSerializableExtra(
                    "event");
        }

        if (this.filename == null) {
            this.filename = this.getIntent().getStringExtra("filename");
        }

        // Something went terribly wrong and we can't continue.
        if (this.event == null) {
            this.finish();
            return;
        }

        if (this.filename == null) {
            this.finish();
            return;
        }

        // Find all of the views for the header
        TextView textName = (TextView) this
                .findViewById(R.id.textView_event_name);
        TextView textDate = (TextView) this
                .findViewById(R.id.textView_event_date);
        TextView textType = (TextView) this
                .findViewById(R.id.textView_event_type);
        TextView textGender = (TextView) this
                .findViewById(R.id.textView_event_gender);
        this.spinnerFlight = (Spinner) this.findViewById(R.id.spinner_flight);
        List<String> flightChoices = new ArrayList<String>();
        flightChoices.add(this.getString(R.string.spinner_flight_all));
        for (int i = 0; i < this.event.getFlights(); i++) {
            flightChoices.add((i + 1) + "");
        }

        flightChoices.add(this.getString(R.string.spinner_flight_finals));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, flightChoices);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFlight.setAdapter(adapter);
        spinnerFlight
                .setOnItemSelectedListener(new ListView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> a, View v, int i,
                            long l) {
                        drawTable();
                        selectAthleteNone();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg) {
                        // Nothing here
                    }
                });

        // Find all of the text view for the footer
        this.currentName = (TextView) this
                .findViewById(R.id.textView_current_name);
        this.currentAttempt = (TextView) this
                .findViewById(R.id.textView_current_attempt);

        // Find all of the footer view
        this.buttonMark = (Button) this.findViewById(R.id.button_mark);
        this.buttonScratch = (Button) this.findViewById(R.id.button_scratch);

        // Display the header text
        textName.setText(this.event.getEventName());
        textDate.setText(this.event.getDate());
        textType.setText(this.event.getType().toString().replace("_", " "));
        textGender.setText(this.event.getGender().toString());

        // Locate the participants table
        this.participants = (TableLayout) this
                .findViewById(R.id.tableLayout_participants);

        // Render the table
        this.drawTable();
    }

    /**
     * Saved the state of the exercise before the activity is closed.
     */
    @Override
    public void onPause() {
        super.onPause();
        try {
            FileOutputStream output = this.openFileOutput(this.filename,
                    Context.MODE_PRIVATE);
            Serializer serializer = new Persister();
            serializer.write(this.event, output);
            output.close();
        } catch (Exception e) {
            Log.e(DistanceClipboardActivity.TAG, e.getMessage());
        }
    }

    /**
     * Saves the state of the instance before the activity is destroyed.
     * 
     * @param savedInstanceState
     *            The bundle to use when saving the state.
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save the information about the currently selected box
    }

    /**
     * Restores the instance after the activity is rebuilt.
     * 
     * @param savedInstanceState
     *            The bundle to read the saved state from.
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore the currently selected box
    }

    /**
     * Draws the data table from scratch using the current state of the event
     * object.
     */
    private void drawTable() {
        this.participants.removeAllViews();
        this.rows = new HashMap<Integer, AthleteRowHolder>();
        Resources res = getResources();
        float fontSize = res.getDimension(R.dimen.font_size);
        int cellHeight = res.getDimensionPixelSize(R.dimen.table_cell_height);

        // Add the header row
        TableRow header = new TableRow(this);
        header.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));

        TextView textHeaderName = new TextView(this);
        textHeaderName.setText(this.getString(R.string.name));
        textHeaderName.setLayoutParams(new LayoutParams(
                LayoutParams.FILL_PARENT, cellHeight, 2));
        textHeaderName.setTypeface(Typeface.DEFAULT_BOLD);
        textHeaderName.setGravity(Gravity.CENTER_VERTICAL);
        textHeaderName.setTextSize(fontSize);
        textHeaderName.setBackgroundResource(R.color.header);
        header.addView(textHeaderName);

        TextView textHeaderPosition = new TextView(this);
        textHeaderPosition.setText(this.getString(R.string.flight_position));
        textHeaderPosition.setLayoutParams(new LayoutParams(
                LayoutParams.FILL_PARENT, cellHeight, 1));
        textHeaderPosition.setTypeface(Typeface.DEFAULT_BOLD);
        textHeaderPosition.setGravity(Gravity.CENTER_VERTICAL);
        textHeaderPosition.setTextSize(fontSize);
        textHeaderPosition.setBackgroundResource(R.color.header);
        header.addView(textHeaderPosition);

        for (int i = 0; i < this.event.getQualifyingScores(); i++) {
            int num = i + 1;
            TextView textHeaderQualify = new TextView(this);
            textHeaderQualify.setText("#" + num);
            textHeaderQualify.setLayoutParams(new LayoutParams(
                    LayoutParams.FILL_PARENT, cellHeight, 1));
            textHeaderQualify.setTypeface(Typeface.DEFAULT_BOLD);
            textHeaderQualify.setGravity(Gravity.CENTER);
            textHeaderQualify.setTextSize(fontSize);
            textHeaderQualify.setBackgroundResource(R.color.header);
            header.addView(textHeaderQualify);
        }

        for (int i = 0; i < this.event.getQualifyingScores(); i++) {
            int num = i + 1 + this.event.getQualifyingScores();
            TextView textHeaderFinal = new TextView(this);
            textHeaderFinal.setText("#" + num);
            textHeaderFinal.setLayoutParams(new LayoutParams(
                    LayoutParams.FILL_PARENT, cellHeight, 1));
            textHeaderFinal.setTypeface(Typeface.DEFAULT_BOLD);
            textHeaderFinal.setGravity(Gravity.CENTER);
            textHeaderFinal.setTextSize(fontSize);
            textHeaderFinal.setBackgroundResource(R.color.header);
            header.addView(textHeaderFinal);
        }

        TextView textHeaderPlace = new TextView(this);
        textHeaderPlace.setText(this.getString(R.string.place));
        textHeaderPlace.setLayoutParams(new LayoutParams(
                LayoutParams.FILL_PARENT, cellHeight, 1));
        textHeaderPlace.setTypeface(Typeface.DEFAULT_BOLD);
        textHeaderPlace.setGravity(Gravity.CENTER);
        textHeaderPlace.setTextSize(fontSize);
        textHeaderPlace.setBackgroundResource(R.color.header);
        header.addView(textHeaderPlace);

        this.participants.addView(header, new TableLayout.LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

        // Determine what participants need to be displayed.
        String valAll = this.getString(R.string.spinner_flight_all);
        String valFinal = this.getString(R.string.spinner_flight_finals);
        String spinnerVal = this.spinnerFlight.getSelectedItem().toString();
        List<Participant> athletes = new ArrayList<Participant>();
        if (spinnerVal.equals(valAll)) {
            // Display everyone
            athletes = this.event.getParticipants();
            Collections.sort(athletes);
        } else if (spinnerVal.equals(valFinal)) {
            // Display the participants in the finals sorted by measurements
            athletes = this.event.calculateFinals();
        } else {
            // Display only those participants in the selected flight
            int flightInt = Integer.parseInt(spinnerVal);
            for (int i = 0; i < this.event.getParticipants().size(); i++) {
                Participant p = this.event.getParticipants().get(i);
                if (p.getFlight() == flightInt) {
                    athletes.add(p);
                }

                Collections.sort(athletes);
            }
        }

        // Add all of the participants

        for (int i = 0; i < athletes.size(); i++) {
            Participant athlete = athletes.get(i);

            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));

            TextView textName = new TextView(this);
            textName.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                    cellHeight, 2));
            textName.setGravity(Gravity.CENTER_VERTICAL);
            textName.setTextSize(fontSize);
            textName.setOnClickListener(this);
            textName.setBackgroundResource(R.color.names);
            tr.addView(textName);

            TextView textFlightPosition = new TextView(this);
            textFlightPosition.setLayoutParams(new LayoutParams(
                    LayoutParams.FILL_PARENT, cellHeight, 1));
            textFlightPosition.setGravity(Gravity.CENTER_VERTICAL);
            textFlightPosition.setTextSize(fontSize);
            textFlightPosition.setBackgroundResource(R.color.names);
            tr.addView(textFlightPosition);

            AthleteRowHolder holder = new AthleteRowHolder(textName,
                    textFlightPosition);
            this.rows.put(i, holder);

            for (int j = 0; j < this.event.getQualifyingScores(); j++) {
                int num = j + 1;
                TextView textQualify = new TextView(this);
                textQualify.setLayoutParams(new LayoutParams(
                        LayoutParams.FILL_PARENT, cellHeight, 1));
                textQualify.setGravity(Gravity.CENTER);
                textQualify.setTextSize(fontSize);
                textQualify.setOnClickListener(this);
                tr.addView(textQualify);
                holder.addMark(num, textQualify);
            }

            for (int j = 0; j < this.event.getQualifyingScores(); j++) {
                int num = j + 1 + this.event.getQualifyingScores();
                TextView textFinal = new TextView(this);
                textFinal.setLayoutParams(new LayoutParams(
                        LayoutParams.FILL_PARENT, cellHeight, 1));
                textFinal.setGravity(Gravity.CENTER);
                textFinal.setTextSize(fontSize);
                textFinal.setOnClickListener(this);
                tr.addView(textFinal);
                holder.addMark(num, textFinal);
            }

            holder.displayAthlete(athlete);

            TextView textFlightPlace = new TextView(this);
            textFlightPlace.setLayoutParams(new LayoutParams(
                    LayoutParams.FILL_PARENT, cellHeight, 1));
            textFlightPlace.setGravity(Gravity.CENTER);
            textFlightPlace.setTextSize(fontSize);
            textFlightPlace.setBackgroundResource(R.color.names);
            tr.addView(textFlightPlace);

            holder.setTextPlace(textFlightPlace);

            this.participants.addView(tr, new TableLayout.LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        }

        this.updateAthletePlace();
    }

    /**
     * Updates the places for all of the athletes.
     */
    public void updateAthletePlace() {
        Iterator<Map.Entry<Integer, AthleteRowHolder>> it = this.rows
                .entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, AthleteRowHolder> pairs = it.next();
            AthleteRowHolder holder = pairs.getValue();
            int place = this.event.participantPlace(holder.getParticipant());
            holder.updatePlace(place);
        }
    }

    /**
     * Event handler for the mark button.
     * 
     * @param view
     *            The calling view.
     */
    public void onMarkClick(View view) {
        AthleteRowHolder holder = (AthleteRowHolder) view
                .getTag(R.id.id_holder_object);
        Integer attempt = (Integer) view.getTag(R.id.id_holder_index);
        if (holder.getParticipant() == null) {
            // Something bad happened
        } else if (attempt > 0) {
            if (this.event.isMetric()) {
                this.displayMetricInput(holder, attempt);
            } else {
                this.displayUsInput(holder, attempt);
            }
        } else {
            // Invalid selection
        }

    }

    /**
     * The event handler for the scratch button.
     * 
     * @param view
     *            The calling view.
     */
    public void onScratchClick(View view) {
        AthleteRowHolder holder = (AthleteRowHolder) view
                .getTag(R.id.id_holder_object);
        Integer attempt = (Integer) view.getTag(R.id.id_holder_index);
        if (holder.getParticipant() == null) {
            // Something bad happened
        } else if (attempt > 0) {
            // Display confirmation box if the value is currently set
            if (holder.getParticipant().getMeasurement(attempt.intValue()) == null) {
                holder.mark(attempt.intValue());
                lastClicked.setBackgroundResource(R.color.selected);
            } else {
                this.displayScratchConfirmation(holder, attempt);
            }
        } else {
            // Invalid selection
        }
    }

    /**
     * Adds a new participant.
     * 
     * @param view
     *            The calling view.
     */
    public void onAddParticipantClick(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.add_participant);
        View v = this.getLayoutInflater().inflate(R.layout.dialog_add_user,
                null);
        final EditText inputName = (EditText) v
                .findViewById(R.id.editText_participant_name);
        final EditText inputFlight = (EditText) v
                .findViewById(R.id.editText_participant_flight);
        final EditText inputPosition = (EditText) v
                .findViewById(R.id.editText_participant_position);

        alert.setView(v);
        alert.setPositiveButton(R.string.add,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String name = inputName.getText().toString();
                        String flight = inputFlight.getText().toString();
                        if (flight.length() == 0) {
                            flight = "1";
                        }

                        String position = inputPosition.getText().toString();
                        if (position.length() == 0) {
                            position = "1";
                        }

                        // Add the participant;
                        event.getParticipants().add(
                                new Participant(name, Integer.parseInt(flight),
                                        Integer.parseInt(position)));
                        drawTable();
                    }
                });

        alert.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

        alert.show();
    }

    /**
     * The on click handler used to handle the clicks on distance scores for
     * participants that functions to select an change and individual
     * measurement.
     * 
     * @param view
     *            The calling view.
     */
    @Override
    public void onClick(View view) {
        AthleteRowHolder holder = (AthleteRowHolder) view
                .getTag(R.id.id_holder_object);
        Integer attempt = (Integer) view.getTag(R.id.id_holder_index);
        if (holder.getParticipant() == null) {
            // Something bad happened
            this.selectAthleteNone();
        } else if (attempt > 0) {
            // Change the mark that is selected
            if (this.lastClicked != null) {
                AthleteRowHolder oldHolder = (AthleteRowHolder) this.lastClicked
                        .getTag(R.id.id_holder_object);
                oldHolder.displayAthlete(oldHolder.getParticipant());
            }

            view.setBackgroundResource(R.color.selected);
            this.lastClicked = view;

            this.selectAthleteBox(holder, attempt.intValue());
        } else {
            // Launch the dialog to edit a participant.
            this.displayEditParticipant(holder);
        }
    }

    /**
     * Updates the GUI so that no athlete score is selected.
     */
    private void selectAthleteNone() {
        this.currentName.setText("");
        this.currentAttempt.setText("");
        this.buttonMark.setEnabled(false);
        this.buttonScratch.setEnabled(false);
    }

    /**
     * Updates the gui so that a specific athlete score is selected.
     * 
     * @param holder
     *            The athlete holder.
     * @param attempt
     *            The attempt index.
     */
    private void selectAthleteBox(AthleteRowHolder holder, int attempt) {
        Participant athlete = holder.getParticipant();
        String athleteName = athlete.getName();
        if (athleteName == null) {
            athleteName = this.getString(R.string.athlete);
        } else if (athleteName.length() == 0) {
            athleteName = this.getString(R.string.athlete);
        }

        this.currentName.setText(athleteName);
        this.currentAttempt.setText("#" + attempt);
        this.buttonMark.setTag(R.id.id_holder_object, holder);
        this.buttonMark.setTag(R.id.id_holder_index, attempt);
        this.buttonMark.setEnabled(true);
        this.buttonScratch.setTag(R.id.id_holder_object, holder);
        this.buttonScratch.setTag(R.id.id_holder_index, attempt);
        this.buttonScratch.setEnabled(true);
    }

    /**
     * Displays the confirmation dialog to confirm the user wants to mark the
     * measurement as a scratch.
     * 
     * @param holder
     *            The athlete holder.
     * @param attempt
     *            The attempt index.
     */
    private void displayScratchConfirmation(final AthleteRowHolder holder,
            final int attempt) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.scratch_confirmation)
                .setCancelable(true)
                .setPositiveButton(R.string.scratch,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // Do it!
                                holder.mark(attempt);
                                lastClicked
                                        .setBackgroundResource(R.color.selected);
                                updateAthletePlace();
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Displays the dialog to input a metric measurement.
     * 
     * @param holder
     *            The athlete holder.
     * @param attempt
     *            The attempt index.
     */
    private void displayMetricInput(final AthleteRowHolder holder,
            final int attempt) {
        Participant athlete = holder.getParticipant();

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.record_measurement);
        String athleteName = athlete.getName();
        if (athleteName == null) {
            athleteName = this.getString(R.string.athlete);
        } else if (athleteName.length() == 0) {
            athleteName = this.getString(R.string.athlete);
        }

        alert.setMessage(athleteName + " " + this.getString(R.string.attempt)
                + " #" + attempt);
        View v = this.getLayoutInflater().inflate(R.layout.dialog_metric, null);
        final EditText input = (EditText) v
                .findViewById(R.id.editText_distance_meters);
        Measurement m = athlete.getMeasurement(attempt);
        if (m != null && !m.isScratch()) {
            input.setText(m.getMeters() + "");
        }

        alert.setView(v);

        alert.setPositiveButton(R.string.mark,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();
                        if (value.length() == 0) {
                            value = "0";
                        }

                        holder.mark(attempt, Double.parseDouble(value));
                        lastClicked.setBackgroundResource(R.color.selected);
                        updateAthletePlace();
                    }
                });

        alert.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

        alert.show();
    }

    /**
     * Displays the dialog to input a US measurement.
     * 
     * @param holder
     *            The athlete holder.
     * @param attempt
     *            The attempt index.
     */
    private void displayUsInput(final AthleteRowHolder holder, final int attempt) {
        Participant athlete = holder.getParticipant();

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.record_measurement);
        String athleteName = athlete.getName();
        if (athleteName == null) {
            athleteName = this.getString(R.string.athlete);
        } else if (athleteName.length() == 0) {
            athleteName = this.getString(R.string.athlete);
        }

        alert.setMessage(athleteName + " " + this.getString(R.string.attempt)
                + " #" + attempt);
        View v = this.getLayoutInflater().inflate(R.layout.dialog_us, null);
        final EditText inputFeet = (EditText) v
                .findViewById(R.id.editText_distance_feet);
        final EditText inputInches = (EditText) v
                .findViewById(R.id.editText_distance_inches);
        Measurement m = athlete.getMeasurement(attempt);
        if (m != null && !m.isScratch()) {
            inputFeet.setText(m.getFeet() + "");
            inputInches.setText(m.getInches() + "");
        }

        alert.setView(v);

        alert.setPositiveButton(R.string.mark,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String feet = inputFeet.getText().toString();
                        if (feet.length() == 0) {
                            feet = "0";
                        }

                        String inches = inputInches.getText().toString();
                        if (inches.length() == 0) {
                            inches = "0";
                        }

                        holder.mark(attempt, Integer.parseInt(feet),
                                Double.parseDouble(inches));
                        lastClicked.setBackgroundResource(R.color.selected);
                        updateAthletePlace();
                    }
                });

        alert.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

        alert.show();
    }

    /**
     * Display the interface to edit the participant.
     * 
     * @param holder
     *            The holder of the participant being edited.
     */
    private void displayEditParticipant(final AthleteRowHolder holder) {
        final Participant athlete = holder.getParticipant();

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.edit_participant);
        View v = this.getLayoutInflater().inflate(R.layout.dialog_add_user,
                null);
        final EditText inputName = (EditText) v
                .findViewById(R.id.editText_participant_name);
        inputName.setText(athlete.getName());
        final EditText inputFlight = (EditText) v
                .findViewById(R.id.editText_participant_flight);
        inputFlight.setText(athlete.getFlight() + "");
        final EditText inputPosition = (EditText) v
                .findViewById(R.id.editText_participant_position);
        inputPosition.setText(athlete.getPosition() + "");

        alert.setView(v);
        alert.setPositiveButton(R.string.save,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String name = inputName.getText().toString();
                        String flight = inputFlight.getText().toString();
                        if (flight.length() == 0) {
                            flight = "1";
                        }

                        int flightInt = Integer.parseInt(flight);

                        String position = inputPosition.getText().toString();
                        if (position.length() == 0) {
                            position = "1";
                        }

                        int positionInt = Integer.parseInt(position);

                        // Update the participant
                        boolean partialUpdate = (flightInt == athlete
                                .getFlight() && positionInt == athlete
                                .getPosition());
                        athlete.setName(name);
                        athlete.setFlight(Integer.parseInt(flight));
                        athlete.setPosition(Integer.parseInt(position));
                        if (partialUpdate) {
                            // We can just update the local row.
                            holder.displayAthlete(holder.getParticipant());
                        } else {
                            // We need to redraw the entire table.
                            drawTable();
                        }
                    }
                });

        alert.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

        alert.show();
    }
}
