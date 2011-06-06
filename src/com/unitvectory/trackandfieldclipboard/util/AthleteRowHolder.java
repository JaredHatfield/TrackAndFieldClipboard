/*
 * Track and Field Clipboard
 * Copyright 2011 Jared Hatfield.  All rights reserved.
 */
package com.unitvectory.trackandfieldclipboard.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.widget.TextView;

import com.unitvectory.trackandfieldclipboard.R;
import com.unitvectory.trackandfieldclipboard.model.Measurement;
import com.unitvectory.trackandfieldclipboard.model.Participant;

/**
 * The holder for the participant.
 * 
 * @author Jared Hatfield
 * 
 */
public class AthleteRowHolder {

    /**
     * The way the participant is being displayed.
     */
    private ParticipantDisplay participantDisplay;

    /**
     * The text view for the athlete's place.
     */
    private TextView textPlace;

    /**
     * The text view for the athlete's name.
     */
    private TextView textName;

    /**
     * The text view for the athlete's team.
     */
    private TextView textTeam;

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
     * @param participantDisplay
     *            The participant display method.
     * @param textPlace
     *            The place TextView
     * @param textName
     *            The name TextView.
     * @param textTeam
     *            The team TextView.
     * @param textFlightPosition
     *            The flight / position TextView.
     */
    public AthleteRowHolder(ParticipantDisplay participantDisplay,
            TextView textPlace, TextView textName, TextView textTeam,
            TextView textFlightPosition) {
        this.participantDisplay = participantDisplay;
        this.textPlace = textPlace;
        this.textName = textName;
        this.textTeam = textTeam;
        this.textName.setTag(R.id.id_holder_object, this);
        this.textName.setTag(R.id.id_holder_index, -1);
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
     * @return the textPlace
     */
    public TextView getTextPlace() {
        return textPlace;
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
        if (participant.getName() != null && participant.getName().length() > 0) {
            this.textName.setText(participant.getName());
        } else {
            this.textName.setText(R.string.athlete);
        }

        if (participant.getSchool() != null
                && participant.getSchool().length() > 0) {
            this.textTeam.setText(participant.getSchool());
        } else {
            this.textTeam.setText(R.string.unaffiliated);
        }

        if (this.participantDisplay.equals(ParticipantDisplay.RESULTS)) {
            int best = this.participant.bestMark();
            if (best == 0) {
                this.textFlightPosition.setText("-");
            } else {
                String m = this.getMeasurement(best, this.textFlightPosition
                        .getContext().getString(R.string.scratch));
                this.textFlightPosition.setText(m);
            }
        } else {
            this.textFlightPosition.setText(participant.getFlight() + "/"
                    + participant.getPosition());
        }

        int best = this.participant.bestMark();
        Iterator<Map.Entry<Integer, TextView>> it = this.marks.entrySet()
                .iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, TextView> pairs = it.next();
            int attemptIndex = pairs.getKey().intValue();
            TextView view = pairs.getValue();
            String m = this.getMeasurement(attemptIndex, view.getContext()
                    .getString(R.string.scratch));
            view.setText(m);

            if (best == attemptIndex) {
                view.setBackgroundResource(R.color.best_mark);
            } else {
                view.setBackgroundResource(android.R.color.transparent);
            }
        }
    }

    /**
     * Marks a measurement as a scratch.
     * 
     * @param attempt
     *            The attempt index.
     */
    public void mark(int attempt) {
        // Marks as a foul
        Measurement m = this.participant.getMeasurement(attempt);
        if (m == null) {
            this.participant.addMeasurement(new Measurement(attempt));
            m = this.participant.getMeasurement(attempt);
        } else {
            m.updateScratch();
        }

        // Update the GUI
        this.displayAthlete(this.participant);
    }

    /**
     * Marks a measurement using metric units.
     * 
     * @param attempt
     *            The attempt index.
     * @param meters
     *            The measurement.
     */
    public void mark(int attempt, double meters) {
        Measurement m = this.participant.getMeasurement(attempt);
        if (m == null) {
            this.participant.addMeasurement(new Measurement(attempt, meters));
            m = this.participant.getMeasurement(attempt);
        } else {
            m.updateMark(meters);
        }

        // Update the GUI
        this.displayAthlete(this.participant);
    }

    /**
     * Marks a measurement using feet and inches.
     * 
     * @param attempt
     *            The attempt index.
     * @param feet
     *            The feet component.
     * @param inches
     *            The inches component.
     */
    public void mark(int attempt, int feet, double inches) {
        Measurement m = this.participant.getMeasurement(attempt);
        if (m == null) {
            this.participant.addMeasurement(new Measurement(attempt, feet,
                    inches));
            m = this.participant.getMeasurement(attempt);
        } else {
            m.updateMark(feet, inches);
        }

        // Update the GUI
        this.displayAthlete(this.participant);
    }

    /**
     * Updates the place for the row.
     * 
     * @param place
     */
    public void updatePlace(int place) {
        String str = place + "";
        this.textPlace.setText(str);
    }

    /**
     * Gets a string representation of a measurement.
     * 
     * If the measurement was a foul null is returned and the calling resource
     * is responsible for loading the scratch string resource itself.
     * 
     * @param attempt
     *            The attempt index.
     * @param foul
     *            The string representation of a foul.
     * @return The string representation of a measurement.
     */
    public String getMeasurement(int attempt, String foul) {
        Measurement m = this.participant.getMeasurement(attempt);
        if (m == null) {
            return "-";
        } else {
            return m.translateMeasurement(foul);
        }
    }
}
