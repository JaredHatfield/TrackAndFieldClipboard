/*
 * Track and Field Clipboard
 * Copyright 2011 Jared Hatfield.  All rights reserved.
 */
package com.unitvectory.trackandfieldclipboard.model;

import java.io.Serializable;
import java.text.DecimalFormat;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * A distance measurement that can represent a scratch, a metric measurement, or
 * a feet and inches measurement.
 * 
 * @author Jared Hatfield
 * 
 */
@Root
public class Measurement implements Serializable, Comparable<Measurement> {

    /**
     * The serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The conversion factor of feet to meters.
     */
    private static final double CONVERSION = 0.3048;

    /**
     * The measurement component in feet.
     */
    @Attribute(name = "feet")
    protected int feet;

    /**
     * The measurement component in inches.
     */
    @Attribute(name = "inches")
    protected double inches;

    /**
     * The total measurement in meters.
     */
    @Attribute(name = "meters")
    protected double meters;

    /**
     * A flag indicating the measurement is in metric.
     */
    @Attribute(name = "metric")
    protected boolean metric;

    /**
     * The attempt by the athlete.
     */
    @Attribute(name = "attempt")
    protected int attempt;

    /**
     * The attempt was a scratch.
     */
    @Attribute(name = "scratch")
    protected boolean scratch;

    /**
     * Initializes a new instance of the Measurement class.
     */
    protected Measurement() {
    }

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
        while (this.inches >= 12) {
            this.feet++;
            this.inches -= 12;
        }

        this.fillMeters();
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
        while (this.inches >= 12) {
            this.feet++;
            this.inches -= 12;
        }

        this.fillMeters();
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

    /**
     * Compares this object with the specified object for order
     * 
     * @param m
     *            The Measurement to be compared.
     * @return a negative integer, zero, or a positive integer as this object is
     *         less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(Measurement m) {
        // We are safe comparing meters because feet and inches are always and
        // automatically converted into meters.
        return Double.compare(this.meters, m.meters);
    }

    /**
     * Computes and fill in the meters measurement with the provided feet and
     * inches measurement.
     */
    private void fillMeters() {
        double us = this.feet + (this.inches / 12.0);
        this.meters = us * Measurement.CONVERSION;
    }

    /**
     * Translates a measurement into a string representation.
     * 
     * @param foul
     *            The string to return when the measurement is a foul.
     * @return The string representation.
     */
    public String translateMeasurement(String foul) {
        if (this.isScratch()) {
            return foul;
        } else if (this.isMetric()) {
            return this.getMeters() + "";
        } else {
            DecimalFormat df = new DecimalFormat("##.##");
            df.setMinimumIntegerDigits(2);
            df.setMinimumFractionDigits(2);
            df.setMaximumFractionDigits(2);
            df.setMaximumFractionDigits(2);
            return this.getFeet() + "-" + df.format(this.getInches());
        }
    }
}
