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

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.unitvectory.trackandfieldclipboard.R;
import com.unitvectory.trackandfieldclipboard.model.FieldEvent;
import com.unitvectory.trackandfieldclipboard.model.Measurement;
import com.unitvectory.trackandfieldclipboard.model.Participant;
import com.unitvectory.trackandfieldclipboard.model.ResultsComparator;
import com.unitvectory.trackandfieldclipboard.util.AthleteRowHolder;
import com.unitvectory.trackandfieldclipboard.util.ParticipantDisplay;
import com.unitvectory.trackandfieldclipboard.util.SaveClipboardTask;

/**
 * The clipboard activity used to record measurements for a specific event.
 * 
 * @author Jared Hatfield
 * 
 */
public class DistanceClipboardActivity extends Activity implements
        OnClickListener, OnLongClickListener {

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
     * The list of flight choices.
     */
    private List<String> flightChoices;

    /**
     * The current selection of the flight spinner.
     */
    private int spinnerFlightSelection;

    /**
     * The type of display.
     */
    private ParticipantDisplay participantDisplay;

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
            this.event =
                    (FieldEvent) this.getIntent().getSerializableExtra("event");
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
        TextView textName =
                (TextView) this.findViewById(R.id.textView_event_name);
        TextView textDate =
                (TextView) this.findViewById(R.id.textView_event_date);
        TextView textType =
                (TextView) this.findViewById(R.id.textView_event_type);
        TextView textGender =
                (TextView) this.findViewById(R.id.textView_event_gender);
        TextView textParticipants =
                (TextView) this
                        .findViewById(R.id.textView_event_final_participants);
        TextView textFlights =
                (TextView) this.findViewById(R.id.textView_event_flights);

        // Display the header text
        textName.setText(this.event.getEventName());
        textDate.setText(this.event.getDate());
        textType.setText(this.event.getType().toString().replace("_", " "));
        textGender.setText(this.event.getGender().toString());
        textParticipants.setText(this.event.getFinalParticipants() + "");
        textFlights.setText(this.event.getFlights() + "");

        // Set up the navigation sipnner
        this.spinnerFlightSelection = 0;
        this.participantDisplay = ParticipantDisplay.ALL;
        flightChoices = new ArrayList<String>();
        flightChoices.add(this.getString(R.string.spinner_flight_all));
        for (int i = 0; i < this.event.getFlights(); i++) {
            flightChoices.add(this.getString(R.string.flight) + " " + (i + 1)
                    + "");
        }

        flightChoices.add(this.getString(R.string.spinner_flight_finals));
        flightChoices.add(this.getString(R.string.spinner_results));
        ArrayAdapter<String> mSpinnerAdapter =
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, flightChoices);
        mSpinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(mSpinnerAdapter,
                new ActionBar.OnNavigationListener() {
                    public boolean
                            onNavigationItemSelected(int arg0, long arg1) {
                        spinnerFlightSelection = arg0;
                        drawTable();
                        selectAthleteNone();
                        return true;
                    }
                });

        // Hide the title
        actionBar.setDisplayShowTitleEnabled(false);

        // Find all of the text view for the footer
        this.currentName =
                (TextView) this.findViewById(R.id.textView_current_name);
        this.currentAttempt =
                (TextView) this.findViewById(R.id.textView_current_attempt);

        // Find all of the footer view
        this.buttonMark = (Button) this.findViewById(R.id.button_mark);
        this.buttonScratch = (Button) this.findViewById(R.id.button_scratch);

        // Locate the participants table
        this.participants =
                (TableLayout) this.findViewById(R.id.tableLayout_participants);

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
            FileOutputStream output =
                    this.openFileOutput(this.filename, Context.MODE_PRIVATE);
            SaveClipboardTask saveClipboardTask = new SaveClipboardTask(output);
            saveClipboardTask.execute(this.event);
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

        // Save the information about the current state
        savedInstanceState.putInt("flightSelection",
                this.spinnerFlightSelection);
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

        // Restore the current state
        this.getActionBar().setSelectedNavigationItem(
                savedInstanceState.getInt("flightSelection"));
    }

    /**
     * Creates the action bar.
     * 
     * @param menu
     *            The menu to manipulate.
     * @return True so the menu is displayed.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_distance_clipboard, menu);
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
                // App icon in Action Bar clicked; go home
                Intent intent = new Intent(this, TrackAndFieldActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.menu_add_participant:
                this.addParticipantClick();
                return true;
            case R.id.menu_email_results:
                final Intent emailIntent =
                        new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                        this.event.getEventName());
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                        this.event.toString());
                startActivity(emailIntent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Draws the data table from scratch using the current state of the event
     * object.
     */
    private void drawTable() {
        this.participants.removeAllViews();
        this.rows = new HashMap<Integer, AthleteRowHolder>();
        Resources res = getResources();
        float fontSize = res.getDimensionPixelSize(R.dimen.font_size);
        int cellHeight = res.getDimensionPixelSize(R.dimen.table_cell_height);
        int cellLeftPading =
                res.getDimensionPixelSize(R.dimen.table_cell_left_padding);

        // Determine what participants need to be displayed.
        this.participantDisplay = ParticipantDisplay.FLIGHT;
        List<Participant> athletes = new ArrayList<Participant>();
        String spinnerVal = this.flightChoices.get(this.spinnerFlightSelection);
        if (spinnerVal.equals(this.getString(R.string.spinner_flight_all))) {
            // Display everyone
            athletes = this.event.getParticipants();
            Collections.sort(athletes);
            this.participantDisplay = ParticipantDisplay.ALL;
        } else if (spinnerVal.equals(this
                .getString(R.string.spinner_flight_finals))) {
            // Display the participants in the finals sorted by measurements
            athletes = this.event.calculateFinals();
            this.participantDisplay = ParticipantDisplay.FINALS;
        } else if (spinnerVal.equals(this.getString(R.string.spinner_results))) {
            // Display the results
            athletes = this.event.getParticipants();
            Collections.sort(athletes, new ResultsComparator());
            Collections.reverse(athletes);
            this.participantDisplay = ParticipantDisplay.RESULTS;
        } else {
            // Display only those participants in the selected flight
            String selectedFlight =
                    spinnerVal.substring(this.getString(R.string.flight)
                            .length() + 1);
            int flightInt = Integer.parseInt(selectedFlight);
            for (int i = 0; i < this.event.getParticipants().size(); i++) {
                Participant p = this.event.getParticipants().get(i);
                if (p.getFlight() == flightInt) {
                    athletes.add(p);
                }

                Collections.sort(athletes);
            }
        }

        // Add the header row
        TableRow header = new TableRow(this);
        header.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));

        TextView textHeaderPlace = new TextView(this);
        textHeaderPlace.setText(R.string.place);
        textHeaderPlace.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT, cellHeight, 1));
        textHeaderPlace.setTypeface(Typeface.DEFAULT_BOLD);
        textHeaderPlace.setGravity(Gravity.CENTER);
        textHeaderPlace.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
        textHeaderPlace.setBackgroundResource(R.color.header);
        header.addView(textHeaderPlace);

        TextView textHeaderName = new TextView(this);
        textHeaderName.setText(R.string.name);
        textHeaderName.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, cellHeight, 2));
        textHeaderName.setPadding(cellLeftPading, 0, 0, 0);
        textHeaderName.setTypeface(Typeface.DEFAULT_BOLD);
        textHeaderName.setGravity(Gravity.CENTER_VERTICAL);
        textHeaderName.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
        textHeaderName.setBackgroundResource(R.color.header);
        header.addView(textHeaderName);

        TextView textHeaderTeam = new TextView(this);
        textHeaderTeam.setText(R.string.team);
        textHeaderTeam.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, cellHeight));
        textHeaderTeam.setPadding(cellLeftPading, 0, 0, 0);
        textHeaderTeam.setTypeface(Typeface.DEFAULT_BOLD);
        textHeaderTeam.setGravity(Gravity.CENTER_VERTICAL);
        textHeaderTeam.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
        textHeaderTeam.setBackgroundResource(R.color.header);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            textHeaderTeam.setVisibility(View.GONE);
            textHeaderTeam.setWidth(0);
        }

        header.addView(textHeaderTeam);

        TextView textHeaderPosition = new TextView(this);
        if (this.participantDisplay.equals(ParticipantDisplay.RESULTS)) {
            textHeaderPosition.setText(R.string.best);
        } else {
            textHeaderPosition.setText(R.string.flight_position);
        }

        textHeaderPosition.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, cellHeight, 1));
        textHeaderPosition.setTypeface(Typeface.DEFAULT_BOLD);
        textHeaderPosition.setGravity(Gravity.CENTER);
        textHeaderPosition.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
        textHeaderPosition.setBackgroundResource(R.color.header);
        header.addView(textHeaderPosition);

        for (int i = 0; i < this.event.getQualifyingScores(); i++) {
            int num = i + 1;
            TextView textHeaderQualify = new TextView(this);
            textHeaderQualify.setText("#" + num);
            textHeaderQualify.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT, cellHeight, 1));
            textHeaderQualify.setTypeface(Typeface.DEFAULT_BOLD);
            textHeaderQualify.setGravity(Gravity.CENTER);
            textHeaderQualify.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
            textHeaderQualify.setBackgroundResource(R.color.header_qualifying);
            header.addView(textHeaderQualify);
        }

        for (int i = 0; i < this.event.getFinalScores(); i++) {
            int num = i + 1 + this.event.getQualifyingScores();
            TextView textHeaderFinal = new TextView(this);
            textHeaderFinal.setText("#" + num);
            textHeaderFinal.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT, cellHeight, 1));
            textHeaderFinal.setTypeface(Typeface.DEFAULT_BOLD);
            textHeaderFinal.setGravity(Gravity.CENTER);
            textHeaderFinal.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
            textHeaderFinal.setBackgroundResource(R.color.header_finals);
            header.addView(textHeaderFinal);
        }

        this.participants.addView(header, new TableLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        // Add all of the participants
        for (int i = 0; i < athletes.size(); i++) {
            Participant athlete = athletes.get(i);

            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));

            TextView textFlightPlace = new TextView(this);
            textFlightPlace.setLayoutParams(new LayoutParams(
                    LayoutParams.WRAP_CONTENT, cellHeight, 1));
            textFlightPlace.setGravity(Gravity.CENTER);
            textFlightPlace.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
            textFlightPlace.setBackgroundResource(R.color.names);
            tr.addView(textFlightPlace);

            TextView textName = new TextView(this);
            textName.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT, cellHeight, 2));
            textName.setPadding(cellLeftPading, 0, 0, 0);
            textName.setGravity(Gravity.CENTER_VERTICAL);
            textName.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
            textName.setOnClickListener(this);
            textName.setOnLongClickListener(this);
            textName.setBackgroundResource(R.color.names);
            tr.addView(textName);

            TextView textTeam = new TextView(this);
            textTeam.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT, cellHeight));
            textTeam.setPadding(cellLeftPading, 0, 0, 0);
            textTeam.setGravity(Gravity.CENTER_VERTICAL);
            textTeam.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
            textTeam.setBackgroundResource(R.color.names);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                textTeam.setVisibility(View.GONE);
                textTeam.setWidth(0);
            }

            tr.addView(textTeam);

            TextView textFlightPosition = new TextView(this);
            textFlightPosition.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT, cellHeight, 1));
            textFlightPosition.setGravity(Gravity.CENTER);
            textFlightPosition
                    .setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
            textFlightPosition.setBackgroundResource(R.color.names);
            tr.addView(textFlightPosition);

            AthleteRowHolder holder =
                    new AthleteRowHolder(this.participantDisplay,
                            textFlightPlace, textName, textTeam,
                            textFlightPosition);
            this.rows.put(i, holder);

            for (int j = 0; j < this.event.getQualifyingScores(); j++) {
                int num = j + 1;
                TextView textQualify = new TextView(this);
                textQualify.setLayoutParams(new LayoutParams(
                        LayoutParams.MATCH_PARENT, cellHeight, 1));
                textQualify.setGravity(Gravity.CENTER);
                textQualify.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
                textQualify.setOnClickListener(this);

                tr.addView(textQualify);
                holder.addMark(num, textQualify);
            }

            for (int j = 0; j < this.event.getFinalScores(); j++) {
                int num = j + 1 + this.event.getQualifyingScores();
                TextView textFinal = new TextView(this);
                textFinal.setLayoutParams(new LayoutParams(
                        LayoutParams.MATCH_PARENT, cellHeight, 1));
                textFinal.setGravity(Gravity.CENTER);
                textFinal.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
                textFinal.setOnClickListener(this);

                tr.addView(textFinal);
                holder.addMark(num, textFinal);
            }

            holder.displayAthlete(athlete);

            this.participants.addView(tr, new TableLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        }

        this.updateAthletePlace();
    }

    /**
     * Updates the places for all of the athletes.
     */
    public void updateAthletePlace() {
        Iterator<Map.Entry<Integer, AthleteRowHolder>> it =
                this.rows.entrySet().iterator();
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
        AthleteRowHolder holder =
                (AthleteRowHolder) view.getTag(R.id.id_holder_object);
        Integer attempt = (Integer) view.getTag(R.id.id_holder_index);
        if (attempt > 0) {
            if (this.event.isMetric()) {
                this.displayMetricInput(holder, attempt);
            } else {
                this.displayUsInput(holder, attempt);
            }
        }
    }

    /**
     * The event handler for the scratch button.
     * 
     * @param view
     *            The calling view.
     */
    public void onScratchClick(View view) {
        AthleteRowHolder holder =
                (AthleteRowHolder) view.getTag(R.id.id_holder_object);
        Integer attempt = (Integer) view.getTag(R.id.id_holder_index);
        if (attempt > 0) {
            // Display confirmation box if the value is currently set
            if (holder.getParticipant().getMeasurement(attempt.intValue()) == null) {
                holder.mark(attempt.intValue());
                lastClicked.setBackgroundResource(R.color.selected);
            } else {
                this.displayScratchConfirmation(holder, attempt);
            }
        }
    }

    /**
     * Adds a new participant.
     * 
     */
    public void addParticipantClick() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.add_participant);
        View v =
                this.getLayoutInflater()
                        .inflate(R.layout.dialog_add_user, null);
        final EditText inputName =
                (EditText) v.findViewById(R.id.editText_participant_name);
        final EditText inputTeam =
                (EditText) v.findViewById(R.id.editText_participant_team);
        final EditText inputFlight =
                (EditText) v.findViewById(R.id.editText_participant_flight);
        int flight = this.event.nextParticipantFlight();
        inputFlight.setText(flight + "");
        final EditText inputPosition =
                (EditText) v.findViewById(R.id.editText_participant_position);
        inputPosition.setText(this.event.nextParticipantPosition(flight) + "");
        alert.setView(v);
        alert.setPositiveButton(R.string.add,
                new DialogInterface.OnClickListener() {
                    public void
                            onClick(DialogInterface dialog, int whichButton) {
                        String name = inputName.getText().toString();
                        String team = inputTeam.getText().toString();
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
                                new Participant(name, team, Integer
                                        .parseInt(flight), Integer
                                        .parseInt(position)));
                        drawTable();
                    }
                });

        alert.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void
                            onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

        alert.show();
    }

    /**
     * Handles the lock click event that is used to delete a participant.
     * 
     * @param view
     *            The calling view.
     * @return True if the callback consumed the long click, false otherwise.
     */
    public boolean onLongClick(View view) {
        AthleteRowHolder holder =
                (AthleteRowHolder) view.getTag(R.id.id_holder_object);
        if (this.participantDisplay.equals(ParticipantDisplay.ALL)) {
            final Participant participant = holder.getParticipant();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.delete_confirmation)
                    .setCancelable(true)
                    .setPositiveButton(R.string.delete,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int id) {
                                    // Delete the participant
                                    event.getParticipants().remove(participant);
                                    drawTable();
                                }
                            })
                    .setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int id) {
                                    dialog.cancel();
                                }
                            });

            AlertDialog alert = builder.create();
            alert.show();
        } else {
            // Show some toast saying the participant can not be deleted in this
            // view.
            Toast toast =
                    Toast.makeText(this,
                            R.string.warning_no_delete_participant,
                            Toast.LENGTH_SHORT);
            toast.show();
        }

        return true;
    }

    /**
     * The on click handler used to handle the clicks on distance scores for
     * participants that functions to select an change and individual
     * measurement.
     * 
     * @param view
     *            The calling view.
     */
    public void onClick(View view) {
        AthleteRowHolder holder =
                (AthleteRowHolder) view.getTag(R.id.id_holder_object);
        Integer attempt = (Integer) view.getTag(R.id.id_holder_index);
        if (holder.getParticipant() == null) {
            // Something bad happened
            this.selectAthleteNone();
        } else if (attempt > 0) {
            // Change the mark that is selected
            if (this.participantDisplay.equals(ParticipantDisplay.RESULTS)) {
                // Display a message saying the mark can not be edited
                Toast toast =
                        Toast.makeText(this, R.string.warning_no_edit_results,
                                Toast.LENGTH_SHORT);
                toast.show();
            } else if (this.participantDisplay
                    .equals(ParticipantDisplay.FLIGHT)
                    && attempt > this.event.getQualifyingScores()) {
                // Display a message saying the mark can not be edited
                Toast toast =
                        Toast.makeText(this, R.string.warning_no_edit_finals,
                                Toast.LENGTH_SHORT);
                toast.show();
            } else if (this.participantDisplay
                    .equals(ParticipantDisplay.FINALS)
                    && attempt <= this.event.getQualifyingScores()) {
                // Display a message saying the mark can not be edited
                Toast toast =
                        Toast.makeText(this, R.string.warning_no_edit_flight,
                                Toast.LENGTH_SHORT);
                toast.show();
            } else {
                // The mark can be edited.
                if (this.lastClicked != null) {
                    AthleteRowHolder oldHolder =
                            (AthleteRowHolder) this.lastClicked
                                    .getTag(R.id.id_holder_object);
                    oldHolder.displayAthlete(oldHolder.getParticipant());
                }

                view.setBackgroundResource(R.color.selected);
                this.lastClicked = view;

                this.selectAthleteBox(holder, attempt.intValue());
            }
        } else {
            // Launch the dialog to edit a participant.
            if (this.participantDisplay.equals(ParticipantDisplay.ALL)) {
                this.displayEditParticipant(holder);
            } else {
                // Display a message saying the participant can not be edited
                Toast toast =
                        Toast.makeText(this,
                                R.string.warning_no_edit_particiant,
                                Toast.LENGTH_SHORT);
                toast.show();
            }
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
        final EditText input =
                (EditText) v.findViewById(R.id.editText_distance_meters);
        Measurement m = athlete.getMeasurement(attempt);
        if (m != null && !m.isScratch()) {
            input.setText(m.getMeters() + "");
        }

        alert.setView(v);

        alert.setPositiveButton(R.string.mark,
                new DialogInterface.OnClickListener() {
                    public void
                            onClick(DialogInterface dialog, int whichButton) {
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
                    public void
                            onClick(DialogInterface dialog, int whichButton) {
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
    private void
            displayUsInput(final AthleteRowHolder holder, final int attempt) {
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
        final EditText inputFeet =
                (EditText) v.findViewById(R.id.editText_distance_feet);
        final EditText inputInches =
                (EditText) v.findViewById(R.id.editText_distance_inches);
        Measurement m = athlete.getMeasurement(attempt);
        if (m != null && !m.isScratch()) {
            inputFeet.setText(m.getFeet() + "");
            inputInches.setText(m.getInches() + "");
        }

        alert.setView(v);

        alert.setPositiveButton(R.string.mark,
                new DialogInterface.OnClickListener() {
                    public void
                            onClick(DialogInterface dialog, int whichButton) {
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
                    public void
                            onClick(DialogInterface dialog, int whichButton) {
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
        View v =
                this.getLayoutInflater()
                        .inflate(R.layout.dialog_add_user, null);
        final EditText inputName =
                (EditText) v.findViewById(R.id.editText_participant_name);
        inputName.setText(athlete.getName());
        final EditText inputTeam =
                (EditText) v.findViewById(R.id.editText_participant_team);
        inputTeam.setText(athlete.getSchool());
        final EditText inputFlight =
                (EditText) v.findViewById(R.id.editText_participant_flight);
        inputFlight.setText(athlete.getFlight() + "");
        final EditText inputPosition =
                (EditText) v.findViewById(R.id.editText_participant_position);
        inputPosition.setText(athlete.getPosition() + "");

        alert.setView(v);
        alert.setPositiveButton(R.string.save,
                new DialogInterface.OnClickListener() {
                    public void
                            onClick(DialogInterface dialog, int whichButton) {
                        String name = inputName.getText().toString();
                        String team = inputTeam.getText().toString();
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
                        boolean partialUpdate =
                                (flightInt == athlete.getFlight() && positionInt == athlete
                                        .getPosition());
                        athlete.setName(name);
                        athlete.setSchool(team);
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
                    public void
                            onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

        alert.show();
    }
}
