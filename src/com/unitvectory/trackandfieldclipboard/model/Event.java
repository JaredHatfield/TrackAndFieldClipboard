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
     */
    public Event(String eventName, EventType type, Gender gender, String date,
            int qualifyingScores, int finalScores, int finalParticipants,
            int flights) {
        this.eventName = eventName;
        this.type = type;
        this.gender = gender;
        this.date = date;
        this.qualifyingScores = qualifyingScores;
        this.finalScores = finalScores;
        this.finalParticipants = finalParticipants;
        this.flights = flights;
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
}
