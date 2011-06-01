/*
 * Track and Field Clipboard
 * Copyright 2011 Jared Hatfield.  All rights reserved.
 */
package com.unitvectory.trackandfieldclipboard.model;

import java.io.Serializable;

/**
 * A distance measurement that can represent a scratch, a metric measurement, or
 * a feet and inches measurement.
 * 
 * @author Jared Hatfield
 * 
 */
public class Measurement implements Serializable {

    /**
     * The serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The measurement component in feet.
     */
    private int feet;

    /**
     * The measurement component in inches.
     */
    private double inches;

    /**
     * The total measurement in meters.
     */
    private double meters;

    /**
     * A flag indicating the measurement is in metric.
     */
    private boolean metric;

    /**
     * The attempt by the athlete.
     */
    private int attempt;

    /**
     * The attempt was a scratch.
     */
    private boolean scratch;

    /**
     * Initializes a new instance of the Measurement class representing a
     * scratch.
     * 
     * @param attempt
     *            The attempt index.
     */
    public Measurement(int attempt) {
        this.metric = false;
        this.attempt = attempt;

        // The mark was a scratch
        this.scratch = true;

        // Zero out all of the values
        this.feet = 0;
        this.inches = 0;
        this.meters = 0;
    }

    /**
     * Initializes a new instance of the Measurement class where the score was
     * recorded in meters.
     * 
     * @param attempt
     *            The attempt index.
     * @param meters
     *            The decimal distance in meters.
     */
    public Measurement(int attempt, double meters) {
        this.metric = true;
        this.attempt = attempt;
        this.scratch = false;
        this.feet = 0;
        this.inches = 0;

        // Save the mark
        this.meters = meters;
    }

    /**
     * Initializes a new instance of the Measurement class where the score was
     * recorded in feet and inches.
     * 
     * @param attempt
     *            The attempt index.
     * @param feet
     *            The foot component of the distance.
     * @param inches
     *            The remaining decimal inches component of the distance.
     */
    public Measurement(int attempt, int feet, double inches) {
        this.metric = false;
        this.attempt = attempt;
        this.scratch = false;
        this.meters = 0;

        // Save the mark
        this.feet = feet;
        this.inches = inches;
    }

    /**
     * Updates the mark to be counted as a scratch.
     */
    public void updateScratch() {
        this.scratch = true;
        this.metric = false;
        this.meters = 0;
        this.feet = 0;
        this.inches = 0;
    }

    /**
     * Updates the mark to the new value in feet and inches.
     * 
     * @param feet
     *            The foot component of the distance.
     * @param inches
     *            The remaining decimal inches component.
     */
    public void updateMark(int feet, double inches) {
        this.scratch = false;
        this.metric = false;
        this.meters = 0;
        this.feet = feet;
        this.inches = inches;
    }

    /**
     * Updates the mark to the new value in meters.
     * 
     * @param meters
     *            The distance in meters.
     */
    public void updateMark(double meters) {
        this.scratch = false;
        this.metric = true;
        this.feet = 0;
        this.inches = 0;
        this.meters = meters;
    }

    /**
     * @return the feet
     */
    public int getFeet() {
        return feet;
    }

    /**
     * @return the inches
     */
    public double getInches() {
        return inches;
    }

    /**
     * @return the meters
     */
    public double getMeters() {
        return meters;
    }

    /**
     * @return the metric
     */
    public boolean isMetric() {
        return metric;
    }

    /**
     * @return the attempt
     */
    public int getAttempt() {
        return attempt;
    }

    /**
     * @return the scratch
     */
    public boolean isScratch() {
        return scratch;
    }
}
