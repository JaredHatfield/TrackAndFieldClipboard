package com.unitvectory.trackandfieldclipboard.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Event implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The event name.
     */
    private String eventName;

    /**
     * The type of the event.
     */
    private EventType type;

    /**
     * The gender.
     */
    private Gender gender;

    /**
     * The date.
     */
    private String date;

    /**
     * The number of scores to count in the qualifying round.
     */
    private int qualifyingScores;

    /**
     * The number of scores to count in the finals.
     */
    private int finalScores;

    /**
     * The number of participants that advance to finals.
     */
    private int finalParticipants;

    /**
     * The number of flights.
     */
    private int flights;

    /**
     * The flag that indicates that metric should be used.
     */
    private boolean metric;

    /**
     * The list of participants.
     */
    private List<Participant> participants;

    /**
     * Initializes a new instance of the Event class.
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
    public Event(String eventName, EventType type, Gender gender, String date,
            int qualifyingScores, int finalScores, int finalParticipants,
            int flights, boolean metric) {
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

}
