/*
 * Track and Field Clipboard
 * Copyright 2011 Jared Hatfield.  All rights reserved.
 */
package com.unitvectory.trackandfieldclipboard.model;

import java.io.Serializable;
import java.util.ArrayList;
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
    @Element(name = "eventName")
    private String eventName;

    /**
     * The type of the event.
     */
    @Element(name = "type")
    private EventType type;

    /**
     * The gender.
     */
    @Element(name = "gender")
    private Gender gender;

    /**
     * The date.
     */
    @Element(name = "date")
    private String date;

    /**
     * The number of scores to count in the qualifying round.
     */
    @Element(name = "qualifyingScores")
    private int qualifyingScores;

    /**
     * The number of scores to count in the finals.
     */
    @Element(name = "finalScores")
    private int finalScores;

    /**
     * The number of participants that advance to finals.
     */
    @Element(name = "finalParticipants")
    private int finalParticipants;

    /**
     * The number of flights.
     */
    @Element(name = "flights")
    private int flights;

    /**
     * The flag that indicates that metric should be used.
     */
    @Element(name = "metric")
    private boolean metric;

    /**
     * The list of participants.
     */
    @ElementList(name = "participants")
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
     * @param participants
     *            The list of participants.
     */
    public FieldEvent(@Element(name = "eventName") String eventName,
            @Element(name = "type") EventType type,
            @Element(name = "gender") Gender gender,
            @Element(name = "date") String date,
            @Element(name = "qualifyingScores") int qualifyingScores,
            @Element(name = "finalScores") int finalScores,
            @Element(name = "finalParticipants") int finalParticipants,
            @Element(name = "flights") int flights,
            @Element(name = "metric") boolean metric,
            @ElementList(name = "participants") List<Participant> participants) {
        this.eventName = eventName;
        this.type = type;
        this.gender = gender;
        this.date = date;
        this.qualifyingScores = qualifyingScores;
        this.finalScores = finalScores;
        this.finalParticipants = finalParticipants;
        this.flights = flights;
        this.metric = metric;
        this.participants = participants;
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
