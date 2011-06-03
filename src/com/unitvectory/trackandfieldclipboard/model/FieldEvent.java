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
    protected List<Participant> participants;

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
    public List<Participant> getParticipants() {
        return participants;
    }

    /**
     * Calculates the array of athletes that qualifed for finals.
     * 
     * @return The array of participants.
     */
    public List<Participant> calculateFinals() {
        List<Participant> athletes = new ArrayList<Participant>();

        for (int i = 0; i < this.participants.size(); i++) {
            athletes.add(this.participants.get(i));
        }

        QualifyingComparator comparator = new QualifyingComparator(
                this.qualifyingScores);
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
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH-mm-ss");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime()) + " " + this.gender + " "
                + this.type;
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
        } else if (type.equalsIgnoreCase("javaline")) {
            return EventType.JAVALINE;
        } else if (type.equalsIgnoreCase("long jump")) {
            return EventType.LONG_JUMP;
        } else if (type.equalsIgnoreCase("triple jump")) {
            return EventType.TRIPLE_JUMP;
        }

        return EventType.NA;
    }

    /**
     * Gets a sample event object.
     * 
     * @return The sample event object.
     */
    public static FieldEvent example() {
        FieldEvent event = new FieldEvent("Example", EventType.SHOT_PUT,
                Gender.MALE, "Today", 3, 3, 10, 2, false);

        Participant p = new Participant("Sam Smith", "432", "10", "High", "31",
                1, 2);
        p.getMarks().add(new Measurement(1, 20, 1.25));
        p.getMarks().add(new Measurement(2, 20, 11));
        p.getMarks().add(new Measurement(3));
        p.getMarks().add(new Measurement(4, 120, 11.25));
        p.getMarks().add(new Measurement(5, 30, 0.25));
        p.getMarks().add(new Measurement(6));
        event.getParticipants().add(p);

        event.getParticipants().add(
                new Participant("John Doe", "123", "10", "High", "30", 1, 1));

        Collections.sort(event.getParticipants());
        return event;
    }
}
