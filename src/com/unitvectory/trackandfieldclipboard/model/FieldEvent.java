/*
 * Track and Field Clipboard
 * Copyright 2011 Jared Hatfield.  All rights reserved.
 */
package com.unitvectory.trackandfieldclipboard.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * The field event that contains all of the information related to the
 * competition.
 * 
 * @author Jared Hatfield
 * 
 */
@Root
public class FieldEvent implements Serializable {

    /**
     * The serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The lock used for IO.
     */
    public static final Object[] WRITELOCK = new Object[0];

    /**
     * The event name.
     */
    @Element(name = "eventName", required = false)
    protected String eventName;

    /**
     * The type of the event.
     */
    @Element(name = "type")
    protected EventType type;

    /**
     * The gender.
     */
    @Element(name = "gender")
    protected Gender gender;

    /**
     * The date.
     */
    @Element(name = "date", required = false)
    protected String date;

    /**
     * The number of scores to count in the qualifying round.
     */
    @Element(name = "qualifyingScores")
    protected int qualifyingScores;

    /**
     * The number of scores to count in the finals.
     */
    @Element(name = "finalScores")
    protected int finalScores;

    /**
     * The number of participants that advance to finals.
     */
    @Element(name = "finalParticipants")
    protected int finalParticipants;

    /**
     * The number of flights.
     */
    @Element(name = "flights")
    protected int flights;

    /**
     * The flag that indicates that metric should be used.
     */
    @Element(name = "metric")
    protected boolean metric;

    /**
     * The list of participants.
     */
    @ElementList(name = "participants")
    protected ArrayList<Participant> participants;

    /**
     * Initializes a new instance of the FieldEvent class.
     */
    protected FieldEvent() {
        this.participants = new ArrayList<Participant>();
    }

    /**
     * Initializes a new instance of the FieldEvent class.
     * 
     * @param eventName
     *            The name of the event.
     * @param type
     *            The type of the event.
     * @param gender
     *            The gender of the event.
     * @param date
     *            The date of the event.
     * @param qualifyingScores
     *            The number of scores to count in the qualifying round.
     * @param finalScores
     *            The number of scores to count in the final round.
     * @param finalParticipants
     *            The number of participants to move on to the final round.
     * @param flights
     *            The number of flights in the competition.
     * @param metric
     *            The flag indicating that the metric system should be used.
     */
    public FieldEvent(String eventName, EventType type, Gender gender,
            String date, int qualifyingScores, int finalScores,
            int finalParticipants, int flights, boolean metric) {
        this.eventName = eventName;
        this.type = type;
        this.gender = gender;
        this.date = date;
        this.qualifyingScores = qualifyingScores;
        this.finalScores = finalScores;
        this.finalParticipants = finalParticipants;
        this.flights = flights;
        this.metric = metric;
        this.participants = new ArrayList<Participant>();
    }

    /**
     * @return the eventName
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * @param eventName
     *            the eventName to set
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * @return the type
     */
    public EventType getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(EventType type) {
        this.type = type;
    }

    /**
     * @return the gender
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * @param gender
     *            the gender to set
     */
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date
     *            the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return the qualifyingScores
     */
    public int getQualifyingScores() {
        return qualifyingScores;
    }

    /**
     * @param qualifyingScores
     *            the qualifyingScores to set
     */
    public void setQualifyingScores(int qualifyingScores) {
        this.qualifyingScores = qualifyingScores;
    }

    /**
     * @return the finalScores
     */
    public int getFinalScores() {
        return finalScores;
    }

    /**
     * @param finalScores
     *            the finalScores to set
     */
    public void setFinalScores(int finalScores) {
        this.finalScores = finalScores;
    }

    /**
     * @return the finalParticipants
     */
    public int getFinalParticipants() {
        return finalParticipants;
    }

    /**
     * @return the flights
     */
    public int getFlights() {
        return flights;
    }

    /**
     * @param flights
     *            the flights to set
     */
    public void setFlights(int flights) {
        this.flights = flights;
    }

    /**
     * @return the metric
     */
    public boolean isMetric() {
        return metric;
    }

    /**
     * @param metric
     *            the metric to set
     */
    public void setMetric(boolean metric) {
        this.metric = metric;
    }

    /**
     * @param finalParticipants
     *            the finalParticipants to set
     */
    public void setFinalParticipants(int finalParticipants) {
        this.finalParticipants = finalParticipants;
    }

    /**
     * @return the participants
     */
    public ArrayList<Participant> getParticipants() {
        return participants;
    }

    /**
     * Calculates the array of athletes that qualified for finals.
     * 
     * @return The array of participants.
     */
    public List<Participant> calculateFinals() {
        @SuppressWarnings("unchecked")
        List<Participant> athletes =
                (List<Participant>) this.participants.clone();
        QualifyingComparator comparator =
                new QualifyingComparator(this.qualifyingScores);
        Collections.sort(athletes, comparator);
        Collections.reverse(athletes);
        while (athletes.size() > this.finalParticipants) {
            athletes.remove(athletes.size() - 1);
        }

        Collections.reverse(athletes);
        return athletes;
    }

    /**
     * Computes the participants place.
     * 
     * @param participant
     *            The participant.
     * @return The place.
     */
    public int participantPlace(Participant participant) {
        int best = participant.bestMark();

        // If you have no marks, you are definitely in last place.
        if (best == 0) {
            return this.participants.size();
        }

        ResultsComparator comparator = new ResultsComparator();
        int place = 1;
        for (int i = 0; i < this.participants.size(); i++) {
            Participant other = this.participants.get(i);
            if (comparator.compare(other, participant) > 0) {
                place++;
            }
        }

        return place;

    }

    /**
     * Creates a new file name.
     * 
     * @return The string to use as a file name.
     */
    public String newFileName() {
        StringBuilder sb = new StringBuilder();

        if (this.eventName != null && this.eventName.length() > 0) {
            sb.append(this.eventName.replaceAll("\\W+", ""));
            sb.append(" ");
        }

        if (!this.gender.equals(Gender.NA)) {
            sb.append(this.gender.toString());
            sb.append(" ");
        }

        if (!this.type.equals(EventType.NA)) {
            sb.append(this.type.toString().replace("_", " "));
            sb.append(" ");
        }

        SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd hh-mm-ss aa");
        Calendar cal = Calendar.getInstance();
        sb.append(dateFormat.format(cal.getTime()));

        return sb.toString();
    }

    /**
     * Gets the highest flight number that has been used so far.
     * 
     * @return The flight number.
     */
    public int nextParticipantFlight() {
        int flight = 1;
        for (int i = 0; i < this.participants.size(); i++) {
            Participant p = this.participants.get(i);
            if (p.getFlight() > flight) {
                flight = p.getFlight();
            }
        }

        return flight;
    }

    /**
     * Gets the next participant number for the specified flight.
     * 
     * @param flight
     *            The flight number.
     * @return The participant index.
     */
    public int nextParticipantPosition(int flight) {
        int position = 1;
        for (int i = 0; i < this.participants.size(); i++) {
            Participant p = this.participants.get(i);
            if (p.getFlight() == flight && p.getPosition() >= position) {
                position = p.getPosition() + 1;
            }
        }

        return position;
    }

    /**
     * Creates a string representation of the results.
     * 
     * @return The string representation of the results.
     */
    @Override
    public String toString() {
        // TODO: Display the results
        Collections.sort(this.participants, new ResultsComparator());
        Collections.reverse(this.participants);

        StringBuilder sb = new StringBuilder();
        String eName = this.getEventName();
        if (eName == null) {
            eName = "";
        }

        sb.append(eName);
        sb.append("\n");
        sb.append("==========================================================================\n");
        sb.append("    Name                    Year School                  Finals           \n");
        sb.append("==========================================================================\n");
        for (int i = 0; i < this.participants.size(); i++) {
            Participant p = this.participants.get(i);
            int best = p.bestMark();
            String measurement = "";
            if (best == 0) {
                measurement = "-";
            } else {
                measurement =
                        p.getMeasurement(best).translateMeasurement("FOUL");
            }

            String pName = p.getName();
            if (pName == null || pName.length() == 0) {
                pName = "Athlete";
            }

            String pYear = p.getYear();
            if (pYear == null) {
                pYear = "";
            }

            String pSchool = p.getSchool();
            if (pSchool == null || pSchool.length() == 0) {
                pSchool = "Unaffiliated";
            }

            sb.append(String.format("%3s %-24s%4s %-20s %9s",
                    this.participantPlace(p), pName, pYear, pSchool,
                    measurement));
            sb.append("\n");
            sb.append("      ");
            int num = this.qualifyingScores + this.finalScores;
            for (int j = 1; j <= num; j++) {
                Measurement m = p.getMeasurement(j);
                if (m == null) {
                    sb.append("NONE");
                } else {
                    sb.append(m.translateMeasurement("FOUL"));
                }

                sb.append("  ");
            }

            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Gets the gender from a user provided string.
     * 
     * @param gender
     *            The string representation of the gender.
     * @return The gender enum.
     */
    public static Gender parseGender(String gender) {
        if (gender.equalsIgnoreCase(Gender.MALE.toString())) {
            return Gender.MALE;
        } else if (gender.equalsIgnoreCase(Gender.FEMALE.toString())) {
            return Gender.FEMALE;
        }

        return Gender.NA;
    }

    /**
     * Gets the event type from a provided string.
     * 
     * @param type
     *            The event type in string form.
     * @return The event type enum.
     */
    public static EventType parseEventType(String type) {
        if (type.equalsIgnoreCase("discus")) {
            return EventType.DISCUS;
        } else if (type.equalsIgnoreCase("shot put")) {
            return EventType.SHOT_PUT;
        } else if (type.equalsIgnoreCase("javelin")) {
            return EventType.JAVALINE;
        } else if (type.equalsIgnoreCase("long jump")) {
            return EventType.LONG_JUMP;
        } else if (type.equalsIgnoreCase("triple jump")) {
            return EventType.TRIPLE_JUMP;
        }

        return EventType.NA;
    }
}
