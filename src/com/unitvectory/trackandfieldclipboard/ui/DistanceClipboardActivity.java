package com.unitvectory.trackandfieldclipboard.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.unitvectory.trackandfieldclipboard.R;
import com.unitvectory.trackandfieldclipboard.model.Event;
import com.unitvectory.trackandfieldclipboard.model.Participant;
import com.unitvectory.trackandfieldclipboard.util.AthleteRowHolder;

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
    private Event event;

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

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_clipboard);

        if (this.event == null) {
            this.event = (Event) this.getIntent().getSerializableExtra("event");
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save the information about the currently selected box
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore the currently selected box
    }

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

    @Override
    public void onClick(View arg0) {
        AthleteRowHolder holder = (AthleteRowHolder) arg0
                .getTag(R.id.id_holder_object);
        Integer attempt = (Integer) arg0.getTag(R.id.id_holder_index);
        Participant athlete = holder.getParticipant();
        if (athlete == null) {
            // Something bad happened
        } else if (attempt > 0) {
            this.currentName.setText(holder.getParticipant().getName());
            this.currentAttempt.setText("#" + attempt.intValue());
            String mark = holder.getMeasurement(attempt.intValue());
            if (mark == null) {
                this.currentMark.setText(R.string.scratch);
            } else {
                this.currentMark.setText(mark);
            }
        } else {
            // Invalid selection
        }
    }
}
