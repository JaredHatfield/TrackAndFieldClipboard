/*
 * Track and Field Clipboard
 * Copyright 2011 Jared Hatfield.  All rights reserved.
 */
package com.unitvectory.trackandfieldclipboard.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
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
     * The current name.
     */
    private TextView currentName;

    /**
     * The current attempt.
     */
    private TextView currentAttempt;

    /**
     * The current mark.
     */
    private TextView currentMark;

    /**
     * The button to mark a distance.
     */
    private Button buttonMark;

    /**
     * The button to mark a scratch.
     */
    private Button buttonScratch;

    /**
     * The button to move to the next measurement.
     */
    private Button buttonNext;

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

        // Something went terribly wrong and we can't continue.
        if (this.event == null) {
            this.finish();
            return;
        }

        // Find all of the text views for the header
        TextView textName = (TextView) this
                .findViewById(R.id.textView_event_name);
        TextView textDate = (TextView) this
                .findViewById(R.id.textView_event_date);
        TextView textType = (TextView) this
                .findViewById(R.id.textView_event_type);
        TextView textGender = (TextView) this
                .findViewById(R.id.textView_event_gender);

        // Find all of the text view for the footer
        this.currentName = (TextView) this
                .findViewById(R.id.textView_current_name);
        this.currentAttempt = (TextView) this
                .findViewById(R.id.textView_current_attempt);
        this.currentMark = (TextView) this
                .findViewById(R.id.textView_current_mark);

        // Find all of the buttons
        this.buttonMark = (Button) this.findViewById(R.id.button_mark);
        this.buttonScratch = (Button) this.findViewById(R.id.button_scratch);
        this.buttonNext = (Button) this.findViewById(R.id.button_next);

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

        // Add the header row
        TableRow header = new TableRow(this);
        header.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));

        TextView textHeaderName = new TextView(this);
        textHeaderName.setText(this.getString(R.string.name));
        textHeaderName.setLayoutParams(new LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 2));
        textHeaderName.setTypeface(Typeface.DEFAULT_BOLD);
        textHeaderName.setTextSize(res.getDimension(R.dimen.font_size));
        textHeaderName.setBackgroundResource(R.color.header);
        header.addView(textHeaderName);

        TextView textHeaderPosition = new TextView(this);
        textHeaderPosition.setText(this.getString(R.string.flight_position));
        textHeaderPosition.setLayoutParams(new LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1));
        textHeaderPosition.setTypeface(Typeface.DEFAULT_BOLD);
        textHeaderPosition.setTextSize(res.getDimension(R.dimen.font_size));
        textHeaderPosition.setBackgroundResource(R.color.header);
        header.addView(textHeaderPosition);

        for (int i = 0; i < this.event.getQualifyingScores(); i++) {
            int num = i + 1;
            TextView textHeaderQualify = new TextView(this);
            textHeaderQualify.setText("#" + num);
            textHeaderQualify.setLayoutParams(new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1));
            textHeaderQualify.setTypeface(Typeface.DEFAULT_BOLD);
            textHeaderQualify.setGravity(Gravity.CENTER_HORIZONTAL);
            textHeaderQualify.setTextSize(res.getDimension(R.dimen.font_size));
            textHeaderQualify.setBackgroundResource(R.color.header);
            header.addView(textHeaderQualify);
        }

        for (int i = 0; i < this.event.getQualifyingScores(); i++) {
            int num = i + 1 + this.event.getQualifyingScores();
            TextView textHeaderFinal = new TextView(this);
            textHeaderFinal.setText("#" + num);
            textHeaderFinal.setLayoutParams(new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1));
            textHeaderFinal.setTypeface(Typeface.DEFAULT_BOLD);
            textHeaderFinal.setGravity(Gravity.CENTER_HORIZONTAL);
            textHeaderFinal.setTextSize(res.getDimension(R.dimen.font_size));
            textHeaderFinal.setBackgroundResource(R.color.header);
            header.addView(textHeaderFinal);
        }

        this.participants.addView(header, new TableLayout.LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

        // Add all of the participants
        List<Participant> athletes = this.event.getParticipants();
        int padding = res
                .getDimensionPixelSize(R.dimen.table_participant_row_padding);
        for (int i = 0; i < athletes.size(); i++) {
            Participant athlete = athletes.get(i);

            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));
            tr.setPadding(0, padding, 0, padding);

            TextView textName = new TextView(this);
            textName.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT, 2));
            textName.setTextSize(res.getDimension(R.dimen.font_size));
            textName.setBackgroundResource(R.color.names);
            tr.addView(textName);

            TextView textFlightPosition = new TextView(this);
            textFlightPosition.setLayoutParams(new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1));
            textFlightPosition.setTextSize(res.getDimension(R.dimen.font_size));
            textFlightPosition.setBackgroundResource(R.color.names);
            tr.addView(textFlightPosition);

            AthleteRowHolder holder = new AthleteRowHolder(textName,
                    textFlightPosition);

            for (int j = 0; j < this.event.getQualifyingScores(); j++) {
                int num = j + 1;
                TextView textQualify = new TextView(this);
                textQualify
                        .setLayoutParams(new LayoutParams(
                                LayoutParams.FILL_PARENT,
                                LayoutParams.WRAP_CONTENT, 1));
                textQualify.setGravity(Gravity.CENTER_HORIZONTAL);
                textQualify.setTextSize(res.getDimension(R.dimen.font_size));
                textQualify.setOnClickListener(this);
                tr.addView(textQualify);
                holder.addMark(num, textQualify);
            }

            for (int j = 0; j < this.event.getQualifyingScores(); j++) {
                int num = j + 1 + this.event.getQualifyingScores();
                TextView textFinal = new TextView(this);
                textFinal
                        .setLayoutParams(new LayoutParams(
                                LayoutParams.FILL_PARENT,
                                LayoutParams.WRAP_CONTENT, 1));
                textFinal.setGravity(Gravity.CENTER_HORIZONTAL);
                textFinal.setTextSize(res.getDimension(R.dimen.font_size));
                textFinal.setOnClickListener(this);
                tr.addView(textFinal);
                holder.addMark(num, textFinal);
            }

            holder.displayAthlete(athlete);

            this.participants.addView(tr, new TableLayout.LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
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
            } else {
                this.displayScratchConfirmation(holder, attempt);
            }
        } else {
            // Invalid selection
        }
    }

    /**
     * The event handler for the next button.
     * 
     * @param view
     *            The calling view.
     */
    public void onNextClick(View view) {
        // TODO: Implement next button.
        Context context = getApplicationContext();
        CharSequence text = "Next Clicked";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
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
        if (this.lastClicked != null) {
            this.lastClicked.setBackgroundResource(android.R.color.transparent);
        }

        view.setBackgroundResource(R.color.selected);
        this.lastClicked = view;

        AthleteRowHolder holder = (AthleteRowHolder) view
                .getTag(R.id.id_holder_object);
        Integer attempt = (Integer) view.getTag(R.id.id_holder_index);
        if (holder.getParticipant() == null) {
            // Something bad happened
            this.selectAthleteNone();
        } else if (attempt > 0) {
            this.selectAthleteBox(holder, attempt.intValue());
        } else {
            // Invalid selection
            this.selectAthleteNone();
        }
    }

    /**
     * Updates the GUI so that no athlete score is selected.
     */
    private void selectAthleteNone() {
        this.currentName.setText("");
        this.currentMark.setText("");
        this.currentAttempt.setText("");
        this.buttonMark.setEnabled(false);
        this.buttonScratch.setEnabled(false);
        this.buttonNext.setEnabled(false);
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
        this.currentName.setText(athlete.getName());
        this.currentAttempt.setText("#" + attempt);
        String mark = holder.getMeasurement(attempt);
        if (mark == null) {
            this.currentMark.setText(R.string.scratch);
        } else {
            this.currentMark.setText(mark);
        }

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
        alert.setMessage(athlete.getName() + " "
                + this.getString(R.string.attempt) + " #" + attempt);
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
        alert.setMessage(athlete.getName() + " "
                + this.getString(R.string.attempt) + " #" + attempt);
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
