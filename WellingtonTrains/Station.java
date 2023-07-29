// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2021T2, Assignment 2
 * Name: Ella Wipatene
 * Username: wipateella
 * ID: 30558005
 */

import ecs100.*;
import java.awt.Color;
import java.util.*;
import java.io.*;

/**
 * Station
 * Information about an individual station:
 * - The name
 * - The fare zone it is in (1 - 14)
 * - The coordinates of the station on the map
 * - The set of TrainLines that go through that station.
 * The constructor just takes the name, zone and coordinates;
 * TrainLines must then be added to the station, one by one.
 */

public class Station implements Comparable<Station> {

    private String name;
    private int zone;   // fare zone
    private double x;   // coordinate x
    private double y;   // coordinate y
    private Set<TrainLine> trainLines = new HashSet<TrainLine>();

    /**
     * Constructor requires name, zone, and coordinates
     */
    public Station(String name, int zone, double x, double y){
        this.name = name;
        this.zone = zone;
        this.x = x;
        this.y = y;
    }

    /** get the name field */
    public String getName(){
        return this.name;
    }

    /** get the zone field */
    public int getZone(){
        return this.zone;
    }

    /** get the x coord */
    public double getXCoord(){
        return this.x;
    }

    /** get the y coord */
    public double getYCoord(){
        return this.y;
    }

    /** compareTo by names */
    public int compareTo(Station other){
        return name.compareTo(other.name);
    }

    /**
     * Add a TrainLine to the station
     */
    public void addTrainLine(TrainLine line){
        trainLines.add(line);
    }

    /**
     * Return an unmodifiable version of the set of train lines.
     */
    public Set<TrainLine> getTrainLines(){
        return Collections.unmodifiableSet(trainLines); 
    }

    /**
     * toString is the station name plus zone, plus number of train lines
     */
    public String toString(){
        return name+" (zone "+zone+", "+trainLines.size()+" lines)";
    }

}
