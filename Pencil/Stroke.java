/* Code for COMP103 - 2021T2, Assignment 2
 * Name: Ella Wipatene
 * Username: Wipateella
 * ID: 300558005
 */

import ecs100.*;
import java.util.*;
import java.awt.Color;

/**
 * Stroke class
 */
public class Stroke
{
    // instance variables - replace the example below with your own
    private Color colour;
    private double line_width;
    private List<Double> x_coords = new ArrayList<Double>(); 
    private List<Double> y_coords = new ArrayList<Double>();

    /**
     * Constructor for objects of class Stroke
     */
    public Stroke(Color c, double lw){ 
        this.colour = c;
        this.line_width = lw;
    }
    
    public void addCoords(double x, double y){
        x_coords.add(x); 
        y_coords.add(y); 
    }
    
    public double getLineWidth(){
        return this.line_width; 
    }

    public Color getColour(){
        return this.colour; 
    }
    
    public List<Double> getXCoords(){
        return x_coords;
    }
    
    public List<Double> getYCoords(){
        return x_coords;
    }
    
    public void draw(){
        UI.setLineWidth(this.line_width);
        UI.setColor(this.colour); 
        for(int i = 0; i < x_coords.size()-1; i++){
            UI.drawLine(x_coords.get(i), y_coords.get(i), x_coords.get(i+1), y_coords.get(i+1)); 
        }
    }
}
