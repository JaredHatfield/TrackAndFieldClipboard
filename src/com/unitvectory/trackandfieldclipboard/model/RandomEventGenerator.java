/*
 * Track and Field Clipboard
 * Copyright 2011 Jared Hatfield.  All rights reserved.
 */
package com.unitvectory.trackandfieldclipboard.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates a random event based on set parameters.
 * 
 * @author Jared Hatfield
 * 
 */
public class RandomEventGenerator {

    /**
     * The event name.
     */
    private static final String NAME = "Sample";

    /**
     * The event type.
     */
    private static final EventType TYPE = EventType.DISCUS;

    /**
     * The event gender.
     */
    private static final Gender GENDER = Gender.MALE;

    /**
     * The event date.
     */
    private static final String DATE = "";

    /**
     * The number of qualifying marks.
     */
    private static final int QUALIFYING_MARKS = 3;

    /**
     * the number of final marks.
     */
    private static final int FINAL_MARKS = 3;

    /**
     * The number of final participants.
     */
    private static final int FINAL_PARTICIPANTS = 10;

    /**
     * The number of flights.
     */
    private static final int NUMBER_OF_FLIGHTS = 2;

    /**
     * The metric flag.
     */
    private static final boolean METRIC = false;

    /**
     * The names of the participants to use.
     */
    private static final String[] PARTICIPANT_NAMES =
            new String[] { "Dominick McGlamery", "Victor Bobzien",
                    "Luke Bartley", "Franklyn Kostyla", "Henry Patoine",
                    "Ray Fail", "Tyson Goatley", "Frank Livsey",
                    "Jonah Stonerock", "Donnie McPeck", "Christian Habersham",
                    "Johnny Fieldman", "Charley Poremba", "Glen Schabowski",
                    "Jerald Bihl", "Gavin Luckadoo", "Stephan Deetz",
                    "Weston Prescod", "Antone Prose", "Martin Romano" };

    /**
     * The minimum distance a participant will have.
     */
    private static final int MIN_DISTANCE = 80;

    /**
     * The maximum distance (not including the bump) that a participant will
     * have.
     */
    private static final int MAX_DISTANCE = 160;

    /**
     * The distance added to a participants distance for being in later flights.
     */
    private static final int FLIGHT_BUMP = 5;

    /**
     * The probability that a participant will foul.
     */
    private static final int PROBABILITY_OF_FOUL = 25;

    /**
     * The total probability.
     */
    private static final int PROBABILITY_TOTAL = 100;

    /**
     * The event that was generated.
     */
    private FieldEvent event;

    /**
     * Initializes a new instance of the RandomEventGenerator.
     * 
     * The constructor perform all of the computations to produce a random set
     * of results.
     */
    public RandomEventGenerator() {
        this.event =
                new FieldEvent(RandomEventGenerator.NAME,
                        RandomEventGenerator.TYPE, RandomEventGenerator.GENDER,
                        RandomEventGenerator.DATE,
                        RandomEventGenerator.QUALIFYING_MARKS,
                        RandomEventGenerator.FINAL_MARKS,
                        RandomEventGenerator.FINAL_PARTICIPANTS,
                        RandomEventGenerator.NUMBER_OF_FLIGHTS,
                        RandomEventGenerator.METRIC);

        int perFlight =
                RandomEventGenerator.PARTICIPANT_NAMES.length
                        / RandomEventGenerator.NUMBER_OF_FLIGHTS;

        Map<Participant, Integer> mins = new HashMap<Participant, Integer>();
        Map<Participant, Integer> maxs = new HashMap<Participant, Integer>();

        for (int i = 0; i < RandomEventGenerator.PARTICIPANT_NAMES.length; i++) {
            String pName = RandomEventGenerator.PARTICIPANT_NAMES[i];
            int position = i + 1;
            int flight = 1;
            while (position > perFlight) {
                position -= perFlight;
                flight++;
            }

            Participant p = new Participant(pName, "", flight, position);
            int min =
                    RandomEventGenerator.generateInRange(
                            RandomEventGenerator.MIN_DISTANCE,
                            RandomEventGenerator.MAX_DISTANCE);
            int max =
                    RandomEventGenerator.generateInRange(
                            RandomEventGenerator.MIN_DISTANCE,
                            RandomEventGenerator.MAX_DISTANCE);
            if (min < max) {
                int tmp = max;
                max = min;
                min = tmp;
            }

            mins.put(p, min);
            maxs.put(p, max);

            // Run the preliminaries
            for (int t = 1; t <= RandomEventGenerator.QUALIFYING_MARKS; t++) {
                if (RandomEventGenerator.randomFoul()) {
                    Measurement m = new Measurement(t);
                    p.addMeasurement(m);
                } else {
                    int feet = RandomEventGenerator.generateInRange(min, max);
                    feet += flight * RandomEventGenerator.FLIGHT_BUMP;
                    double inches =
                            RandomEventGenerator.generateInRange(0,
                                    Measurement.INCHES_PER_FOOT);
                    Measurement m = new Measurement(t, feet, inches);
                    p.addMeasurement(m);
                }
            }

            this.event.participants.add(p);
        }

        // Identify who made the finals
        List<Participant> finalParticipants = new ArrayList<Participant>();
        for (int i = 0; i < this.event.participants.size(); i++) {
            Participant p = this.event.participants.get(i);
            if (this.event.participantPlace(p) <= RandomEventGenerator.FINAL_PARTICIPANTS) {
                finalParticipants.add(p);
            }
        }

        // Run the finals
        for (int i = 0; i < finalParticipants.size(); i++) {
            Participant p = finalParticipants.get(i);
            int min = mins.get(p);
            int max = maxs.get(p);
            for (int t = 1; t <= RandomEventGenerator.FINAL_MARKS; t++) {
                int mark = t + RandomEventGenerator.QUALIFYING_MARKS;
                if (RandomEventGenerator.randomFoul()) {
                    Measurement m = new Measurement(mark);
                    p.addMeasurement(m);
                } else {
                    int feet = RandomEventGenerator.generateInRange(min, max);
                    double inches =
                            RandomEventGenerator.generateInRange(0,
                                    Measurement.INCHES_PER_FOOT);
                    Measurement m = new Measurement(mark, feet, inches);
                    p.addMeasurement(m);
                }
            }
        }
    }

    /**
     * @return the event
     */
    public FieldEvent getEvent() {
        return event;
    }

    /**
     * Generates a random number within a range.
     * 
     * @param min
     *            The minimum.
     * @param max
     *            The maximum.
     * @return The random number.
     */
    private static int generateInRange(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    /**
     * Computes the result of the random probability of an athlete having foul a
     * given mark.
     * 
     * @return True if a foul, otherwise a mark.
     */
    private static boolean randomFoul() {
        int odds =
                RandomEventGenerator.generateInRange(0,
                        RandomEventGenerator.PROBABILITY_TOTAL);
        return odds < RandomEventGenerator.PROBABILITY_OF_FOUL;
    }

}
