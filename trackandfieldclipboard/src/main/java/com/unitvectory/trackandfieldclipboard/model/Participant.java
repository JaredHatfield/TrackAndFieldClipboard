/*
 * Track and Field Clipboard
 * Copyright 2011 Jared Hatfield.  All rights reserved.
 */
package com.unitvectory.trackandfieldclipboard.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * A participant or athlete that is competing in an event which will have
 * measurements recorded for their marks.
 * 
 * @author Jared Hatfield
 * 
 */
@Root
public class Participant implements Serializable, Comparable<Participant> {

    /**
     * The serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The participants name.
     */
    @Element(name = "name", required = false)
    protected String name;

    /**
     * The participants numbers.
     */
    @Element(name = "number", required = false)
    protected String number;

    /**
     * The participants year.
     */
    @Element(name = "year", required = false)
    protected String year;

    /**
     * The participants school.
     */
    @Element(name = "school", required = false)
    protected String school;

    /**
     * The participants seed.
     */
    @Element(name = "seed", required = false)
    protected String seed;

    /**
     * The participants flight.
     */
    @Element(name = "flight")
    protected int flight;

    /**
     * The participants position in their flight.
     */
    @Element(name = "position")
    protected int position;

    /**
     * The measurements for the athlete.
     */
    @ElementList(name = "marks")
    protected ArrayList<Measurement> marks;

    /**
     * Initializes a new instance of the Participant class.
     */
    protected Participant() {
        this.marks = new ArrayList<Measurement>();
    }

    /**
     * Initializes a new instance of the Participant class.
     * 
     * @param name
     *            The participant's name.
     * @param school
     *            The participant's school.
     * @param flight
     *            The participant's flight.
     * @param position
     *            The participant's position.
     */
    public Participant(String name, String school, int flight, int position) {
        this.name = name;
        this.school = school;
        this.flight = flight;
        this.position = position;
        this.marks = new ArrayList<Measurement>();
    }

    /**
     * Initializes a new instance of the Participant class.
     * 
     * @param name
     *            The participant's name.
     * @param number
     *            The participant's number.
     * @param year
     *            The participant's year.
     * @param school
     *            The participant's school
     * @param seed
     *            The participant's seed.
     * @param flight
     *            The participant's flight.
     * @param position
     *            The participant's position.
     */
    public Participant(String name, String number, String year, String school,
            String seed, int flight, int position) {
        this.name = name;
        this.number = number;
        this.year = year;
        this.school = school;
        this.seed = seed;
        this.flight = flight;
        this.position = position;
        this.marks = new ArrayList<Measurement>();
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the number
     */
    public String getNumber() {
        return number;
    }

    /**
     * @param number
     *            the number to set
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * @return the year
     */
    public String getYear() {
        return year;
    }

    /**
     * @param year
     *            the year to set
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * @return the school
     */
    public String getSchool() {
        return school;
    }

    /**
     * @param school
     *            the school to set
     */
    public void setSchool(String school) {
        this.school = school;
    }

    /**
     * @return the seed
     */
    public String getSeed() {
        return seed;
    }

    /**
     * @param seed
     *            the seed to set
     */
    public void setSeed(String seed) {
        this.seed = seed;
    }

    /**
     * @return the flight
     */
    public int getFlight() {
        return flight;
    }

    /**
     * @param flight
     *            the flight to set
     */
    public void setFlight(int flight) {
        this.flight = flight;
    }

    /**
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * @param position
     *            the position to set
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * @return the marks
     */
    public ArrayList<Measurement> getMarks() {
        return marks;
    }

    /**
     * Gets the requested measurement for the participant or null if the
     * measurement has not been taken yet.
     * 
     * @param attempt
     *            The attempt to retrieve.
     * @return The measurement or null.
     */
    public Measurement getMeasurement(int attempt) {
        for (int i = 0; i < this.marks.size(); i++) {
            if (this.marks.get(i).getAttempt() == attempt) {
                return this.marks.get(i);
            }
        }

        return null;
    }

    /**
     * Gets the index of the best mark for the participant.
     * 
     * @return The attempt index or 0 if no mark is found.
     */
    public int bestMark() {
        if (this.marks.size() == 0) {
            return 0;
        }

        // TODO: This should probably be cached to improve speed.
        return Collections.max(this.marks).attempt;
    }

    /**
     * Adds a new measurement to a participant.
     * 
     * @param measurement
     *            The measurement to add.
     */
    public void addMeasurement(Measurement measurement) {
        this.marks.add(measurement);
    }

    /**
     * Compares this object with the specified object for order.
     * 
     * @param p
     *            The Participant to be compared.
     * @return a negative integer, zero, or a positive integer as this object is
     *         less than, equal to, or greater than the specified object.
     */
    public int compareTo(Participant p) {
        if (this.flight < p.flight) {
            return -1;
        } else if (this.flight > p.flight) {
            return 1;
        } else if (this.position < p.position) {
            return -1;
        } else if (this.position > p.position) {
            return 1;
        } else {
            return 0;
        }
    }
}
