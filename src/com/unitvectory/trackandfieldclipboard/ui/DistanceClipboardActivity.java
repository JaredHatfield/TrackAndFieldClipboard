package com.unitvectory.trackandfieldclipboard.ui;

import java.text.DecimalFormat;
import java.util.List;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.unitvectory.trackandfieldclipboard.R;
import com.unitvectory.trackandfieldclipboard.model.Event;
import com.unitvectory.trackandfieldclipboard.model.Measurement;
import com.unitvectory.trackandfieldclipboard.model.Participant;

public class DistanceClipboardActivity extends Activity {

    /**
     * The participants table.
     */
    private TableLayout participants;

    /**
     * The event being manipulated.
     */
    private Event event;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_clipboard);
        this.event = (Event) this.getIntent().getSerializableExtra("event");

        // Something went terribly wrong and we can't continue.
        if (this.event == null) {
            this.finish();
            return;
        }

        // Find all of the text views
        TextView textName = (TextView) this
                .findViewById(R.id.textView_event_name);
        TextView textDate = (TextView) this
                .findViewById(R.id.textView_event_date);
        TextView textType = (TextView) this
                .findViewById(R.id.textView_event_type);
        TextView textGender = (TextView) this
                .findViewById(R.id.textView_event_gender);

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

    private void drawTable() {
        this.participants.removeAllViews();
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
        header.addView(textHeaderName);

        TextView textHeaderPosition = new TextView(this);
        textHeaderPosition.setText(this.getString(R.string.flight_position));
        textHeaderPosition.setLayoutParams(new LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1));
        textHeaderPosition.setTypeface(Typeface.DEFAULT_BOLD);
        textHeaderPosition.setTextSize(res.getDimension(R.dimen.font_size));
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
            header.addView(textHeaderFinal);
        }

        this.participants.addView(header, new TableLayout.LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

        // Add all of the participants
        List<Participant> athletes = this.event.getParticipants();
        for (int i = 0; i < athletes.size(); i++) {
            Participant athlete = athletes.get(i);

            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));

            TextView textName = new TextView(this);
            textName.setText(athlete.getName());
            textName.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT, 2));
            textName.setTextSize(res.getDimension(R.dimen.font_size));
            tr.addView(textName);

            TextView textFlightPosition = new TextView(this);
            textFlightPosition.setText(athlete.getFlight() + "/"
                    + athlete.getPosition());
            textFlightPosition.setLayoutParams(new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1));
            textFlightPosition.setTextSize(res.getDimension(R.dimen.font_size));
            tr.addView(textFlightPosition);

            for (int j = 0; j < this.event.getQualifyingScores(); j++) {
                int num = j + 1;

                TextView textQualify = new TextView(this);
                textQualify.setText(this.translateMeasurement(athlete
                        .getMeasurement(num)));
                textQualify
                        .setLayoutParams(new LayoutParams(
                                LayoutParams.FILL_PARENT,
                                LayoutParams.WRAP_CONTENT, 1));
                textQualify.setGravity(Gravity.CENTER_HORIZONTAL);
                textQualify.setTextSize(res.getDimension(R.dimen.font_size));
                tr.addView(textQualify);
            }

            for (int j = 0; j < this.event.getQualifyingScores(); j++) {
                int num = j + 1 + this.event.getQualifyingScores();
                TextView textFinal = new TextView(this);
                textFinal.setText(this.translateMeasurement(athlete
                        .getMeasurement(num)));
                textFinal
                        .setLayoutParams(new LayoutParams(
                                LayoutParams.FILL_PARENT,
                                LayoutParams.WRAP_CONTENT, 1));
                textFinal.setGravity(Gravity.CENTER_HORIZONTAL);
                textFinal.setTextSize(res.getDimension(R.dimen.font_size));
                tr.addView(textFinal);
            }

            this.participants.addView(tr, new TableLayout.LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        }

    }

    private String translateMeasurement(Measurement measure) {
        if (measure == null) {
            return "-";
        } else if (measure.isScratch()) {
            return this.getString(R.string.scratch);
        }

        else if (measure.isMetric()) {
            return measure.getMeters() + "";
        } else {
            DecimalFormat df = new DecimalFormat("##.##");
            return measure.getFeet() + "-" + df.format(measure.getInches());
        }
    }
}
