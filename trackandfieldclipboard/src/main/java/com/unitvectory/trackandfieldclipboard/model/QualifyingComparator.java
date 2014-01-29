/*
 * Track and Field Clipboard
 * Copyright 2011 Jared Hatfield.  All rights reserved.
 */
package com.unitvectory.trackandfieldclipboard.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The comparator used for identifying the participants that move onto the
 * finals.
 * 
 * @author Jared Hatfield
 * 
 */
public class QualifyingComparator implements Comparator<Participant> {

    /**
     * The number of measurements that are to be compared.
     */
    private int qualifying;

    /**
     * Initializes a new instance of the QualifyingComparator class.
     * 
     * @param qualifying
     *            The number of measurements to look at.
     */
    public QualifyingComparator(int qualifying) {
        this.qualifying = qualifying;
    }

    /**
     * Compares two participants.
     * 
     * @param p1
     *            The first participant to be compared.
     * @param p2
     *            The second participant to be compared.
     * @return a negative integer, zero, or a positive integer as the first
     *         argument is less than, equal to, or greater than the second.
     */
    public int compare(Participant p1, Participant p2) {
        List<Measurement> m1 = this.getQualifyingMeasurements(p1);
        List<Measurement> m2 = this.getQualifyingMeasurements(p2);

        while (m1.size() > 0 || m2.size() > 0) {
            if (m1.size() == 0 && m2.size() == 0) {
                return 0;
            } else if (m2.size() == 0) {
                return 1;
            } else if (m1.size() == 0) {
                return -1;
            }

            int result = m1.get(0).compareTo(m2.get(0));
            if (result != 0) {
                return result;
            }

            m1.remove(0);
            m2.remove(0);
        }

        return 0;
    }

    /**
     * Gets the sorted list of measurements.
     * 
     * @param p
     *            The participant.
     * @return The sorted list of measurements.
     */
    private List<Measurement> getQualifyingMeasurements(Participant p) {
        List<Measurement> list = new ArrayList<Measurement>();
        for (int i = 1; i <= this.qualifying; i++) {
            Measurement m = p.getMeasurement(i);
            if (m != null && !m.isScratch()) {
                list.add(m);
            }
        }

        Collections.sort(list);
        Collections.reverse(list);
        return list;
    }
}
