/*
 * Track and Field Clipboard
 * Copyright 2011 Jared Hatfield.  All rights reserved.
 */
package com.unitvectory.trackandfieldclipboard.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses field event objects out of a text file produced by HY-TEK.
 * 
 * @author Jared Hatfield
 * 
 */
public class HyTekParser {
    /**
     * The divider used to assist with file parsing.
     */
    private static final String DIVIDER = "==========================";

    /**
     * The list of events parsed from the file.
     */
    private List<FieldEvent> events;

    /**
     * Initializes a new instance of the HyTekParser class.
     * 
     * @param lines
     *            The lines of the text file to parse.
     */
    public HyTekParser(String[] lines) {
        this.events = new ArrayList<FieldEvent>();
        int start = 0;
        while (start > -1) {
            start = findStart(lines, start);

            if (start > 0) {
                String eventName = lines[start].trim();
                EventType type = this.parseEventType(eventName);
                Gender gender = this.parseGender(eventName);
                String eventDate = lines[start + 2].trim();

                start += 5;
                int flightNumber = 0;
                List<Participant> athletes = new ArrayList<Participant>();
                while (start < lines.length && lines[start].trim().length() > 0) {
                    if (lines[start].startsWith("Flight")) {
                        flightNumber++;
                    } else if (flightNumber > 0) {
                        Participant p = this.parseAthlete(lines[start],
                                flightNumber);
                        if (p != null) {
                            athletes.add(p);
                        }
                    }

                    start++;
                }

                FieldEvent event = new FieldEvent(eventName, type, gender,
                        eventDate, 3, 3, 10, flightNumber, false);
                for (int i = 0; i < athletes.size(); i++) {
                    event.getParticipants().add(athletes.get(i));
                }

                if (athletes.size() > 0) {
                    this.events.add(event);
                }
            }
        }
    }

    /**
     * @return the events
     */
    public List<FieldEvent> getEvents() {
        return events;
    }

    /**
     * Parses the gender from the provided string.
     * 
     * @param line
     *            The string.
     * @return The gender.
     */
    private Gender parseGender(String line) {
        if (line.contains("Girls")) {
            return Gender.FEMALE;
        } else if (line.contains("Boys")) {
            return Gender.MALE;
        } else {
            return Gender.NA;
        }
    }

    /**
     * Parses the event type from the provided string.
     * 
     * @param line
     *            The string.
     * @return The event type.
     */
    private EventType parseEventType(String line) {
        if (line.contains("Discus")) {
            return EventType.DISCUS;
        } else if (line.contains("Javaline")) {
            return EventType.JAVALINE;
        } else if (line.contains("Shot Put")) {
            return EventType.SHOT_PUT;
        } else if (line.contains("Long Jump")) {
            return EventType.LONG_JUMP;
        } else if (line.contains("Triple Jump")) {
            return EventType.TRIPLE_JUMP;
        } else {
            return EventType.NA;
        }
    }

    /**
     * Finds the start of an event using the dividers as markers.
     * 
     * @param lines
     *            The array of strings.
     * @param start
     *            The starting index.
     * @return The starting position of the next event or -1 if no more events
     *         could be found.
     */
    private int findStart(String[] lines, int start) {
        for (int i = start; i < lines.length - 3; i++) {
            String line1 = lines[i];
            String line2 = lines[i + 3];
            if (line1.startsWith(HyTekParser.DIVIDER)
                    && line2.startsWith(HyTekParser.DIVIDER)) {
                return i - 1;
            }
        }

        return -1;
    }

    /**
     * Parses an athlete's information from a line of the file.
     * 
     * @param line
     *            The string to parse.
     * @param flight
     *            The flight the athlete belongs to.
     * @return The participant object or null if it was not able to be parsed.
     */
    private Participant parseAthlete(String line, int flight) {
        try {
            String position = line.substring(0, 4).trim();
            int positionInt = 1;
            try {
                positionInt = Integer.parseInt(position);
            } catch (Exception e) {
            }

            String number = line.substring(3, 11).trim();
            String name = line.substring(10, 39).trim();
            String year = line.substring(38, 44).trim();
            String school = line.substring(43, 62).trim();
            String seed = line.substring(61).trim();
            return new Participant(name, number, year, school, seed, flight,
                    positionInt);
        } catch (Exception e) {
            return null;
        }
    }
}