package com.unitvectory.trackandfieldclipboard.util;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.widget.TextView;

import com.unitvectory.trackandfieldclipboard.R;
import com.unitvectory.trackandfieldclipboard.model.Measurement;
import com.unitvectory.trackandfieldclipboard.model.Participant;

public class AthleteRowHolder {

    /**
     * The text view for the athlete's name.
     */
    private TextView textName;

    /**
     * The text view for the athlete's flight / position.
     */
    private TextView textFlightPosition;

    /**
     * The participant being displayed.
     */
    private Participant participant;

    /**
     * The collection of text views for the athlete's marks.
     */
    private Map<Integer, TextView> marks;

    /**
     * Initializes a new instance of the AthleteRowHolder class.
     * 
     * @param textName
     *            The name TextView.
     * @param textFlightPosition
     *            The flight / position TextView.
     */
    public AthleteRowHolder(TextView textName, TextView textFlightPosition) {
        this.textName = textName;
        this.textFlightPosition = textFlightPosition;
        this.marks = new HashMap<Integer, TextView>();
    }

    /**
     * @return the textName
     */
    public TextView getTextName() {
        return textName;
    }

    /**
     * @param textName
     *            the textName to set
     */
    public void setTextName(TextView textName) {
        this.textName = textName;
    }

    /**
     * @return the textFlightPosition
     */
    public TextView getTextFlightPosition() {
        return textFlightPosition;
    }

    /**
     * @param textFlightPosition
     *            the textFlightPosition to set
     */
    public void setTextFlightPosition(TextView textFlightPosition) {
        this.textFlightPosition = textFlightPosition;
    }

    /**
     * @return the participant
     */
    public Participant getParticipant() {
        return participant;
    }

    /**
     * Adds a mark to the list.
     * 
     * @param attempt
     *            The attempt.
     * @param view
     *            The TextView.
     */
    public void addMark(int attempt, TextView view) {
        this.marks.put(attempt, view);
        view.setTag(R.id.id_holder_object, this);
        view.setTag(R.id.id_holder_index, attempt);
    }

    /**
     * Displays the participants information.
     * 
     * @param participant
     *            The participant.
     */
    public void displayAthlete(Participant participant) {
        this.participant = participant;
        this.textName.setText(participant.getName());
        this.textFlightPosition.setText(participant.getFlight() + "/"
                + participant.getPosition());

        Iterator<Map.Entry<Integer, TextView>> it = this.marks.entrySet()
                .iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, TextView> pairs = it.next();
            String m = translateMeasurement(participant.getMeasurement(pairs
                    .getKey().intValue()));
            TextView view = pairs.getValue();
            if (m == null) {
                view.setText(R.string.scratch);
            } else {
                view.setText(m);
            }
        }
    }

    public String getMeasurement(int attempt) {
        if (this.participant == null) {
            return null;
        } else {
            return this.translateMeasurement(this.participant
                    .getMeasurement(attempt));
        }
    }

    private String translateMeasurement(Measurement measure) {
        if (measure == null) {
            return "-";
        } else if (measure.isScratch()) {
            return null;
        } else if (measure.isMetric()) {
            return measure.getMeters() + "";
        } else {
            DecimalFormat df = new DecimalFormat("##.##");
            df.setMinimumIntegerDigits(2);
            df.setMinimumFractionDigits(2);
            df.setMaximumFractionDigits(2);
            df.setMaximumFractionDigits(2);
            return measure.getFeet() + "-" + df.format(measure.getInches());
        }
    }
}
