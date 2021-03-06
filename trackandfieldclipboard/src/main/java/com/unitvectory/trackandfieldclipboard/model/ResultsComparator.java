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
 * The comparator that sorts participants based on the best mark.
 * 
 * @author Jared Hatfield
 * 
 */
public class ResultsComparator implements Comparator<Participant> {

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
        // Try something simple first.
        if (p1.marks.size() == 0 && p2.marks.size() == 0) {
            return 0;
        } else if (p2.marks.size() == 0) {
            return 1;
        } else if (p1.marks.size() == 0) {
            return -1;
        }

        int quickResult =
                Collections.max(p1.marks).compareTo(Collections.max(p2.marks));
        if (quickResult != 0) {
            return quickResult;
        }

        // Resort to breaking ties
        List<Measurement> m1 = this.getMeasurements(p1);
        List<Measurement> m2 = this.getMeasurements(p2);

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
    private List<Measurement> getMeasurements(Participant p) {
        List<Measurement> list = new ArrayList<Measurement>();
        for (int i = 0; i < p.getMarks().size(); i++) {
            Measurement m = p.getMarks().get(i);
            list.add(m);
        }

        Collections.sort(list);
        Collections.reverse(list);
        return list;
    }
}
