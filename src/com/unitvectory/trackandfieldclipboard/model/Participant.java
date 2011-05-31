package com.unitvectory.trackandfieldclipboard.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Participant implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The participants name.
     */
    private String name;

    /**
     * The participants numbers.
     */
    private String number;

    /**
     * The participants year.
     */
    private String year;

    /**
     * The participants school.
     */
    private String school;

    /**
     * The participants seed.
     */
    private String seed;

    /**
     * The participants flight.
     */
    private int flight;

    /**
     * The participants position in their flight.
     */
    private int position;

    /**
     * The measurements for the athlete.
     */
    private List<Measurement> marks;

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
    public List<Measurement> getMarks() {
        return marks;
    }
}
