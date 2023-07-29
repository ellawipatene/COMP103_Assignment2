// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2021T2, Assignment 2
 * Name: Ella Wipatene
 * Username: Wipateella
 * ID: 300558005
 */



import ecs100.*;
import java.util.*;
import java.awt.Color;
import javax.swing.JColorChooser; 

/** Pencil   */
public class Pencil{
    private double lastX;
    private double lastY;
    
    private Color colour = Color.black; 
    private double line_width = 5; 
    
    private Stack<Stroke> stroke_record = new Stack<Stroke>();
    private Stack<Stroke> redo_stroke = new Stack<Stroke>();

    /**
     * Setup the GUI
     */
    public void setupGUI(){
        UI.setMouseMotionListener(this::doMouse);
        UI.addSlider("Stroke Size:", 1, 20, this::setLineWidth);
        UI.addButton("Line Colour:", this::setColour); 
        UI.addButton("Undo", this::undo);
        UI.addButton("Redo", this::redo); 
        UI.addButton("Quit", UI::quit);
        UI.setLineWidth(3);
        UI.setDivider(0.0);
    }

    /**
     * Respond to mouse events
     */
    public void doMouse(String action, double x, double y) {
        if (action.equals("pressed")){
            UI.setColor(this.colour); 
            UI.setLineWidth(this.line_width); 
            redo_stroke.clear(); 
            stroke_record.push(new Stroke(colour, line_width)); 
            lastX = x;
            lastY = y; 
        }
        else if (action.equals("dragged")){
            UI.drawLine(lastX, lastY, x, y);
            stroke_record.peek().addCoords(x,y); 
            lastX = x;
            lastY = y;
        }
        else if (action.equals("released")){
            UI.drawLine(lastX, lastY, x, y);
        }
    }
    
    /**
     * Undo the last stroke that you have drawn. 
     */
    public void undo(){
        if(!stroke_record.isEmpty()){
            Stroke stroke_undo = stroke_record.pop(); 
            redo_stroke.push(stroke_undo); 
            
            if(!stroke_record.isEmpty()){
                line_width = stroke_record.peek().getLineWidth(); 
                colour = stroke_record.peek().getColour(); 
                UI.setLineWidth(line_width);
                UI.setColor(colour);
            }
            draw(); 
        }
    }
    
    /**
     * Redoes the undone actions
     */
    public void redo(){
        if(!redo_stroke.isEmpty()){
            Stroke stroke_redo = redo_stroke.pop(); 
            stroke_record.push(stroke_redo); 
            
            line_width = stroke_redo.getLineWidth();
            colour = stroke_redo.getColour();
            UI.setLineWidth(line_width);
            UI.setColor(colour);
    
            draw(); 
        }
    
    }
    
    /**
     *  Will clear the screen and then redraw all of the lines in stack_record
     */
    public void draw(){
        UI.clearGraphics(); 
        if(!stroke_record.isEmpty()){
            for (Stroke s: stroke_record){
                s.draw();
            }
        }
    }

    /**
     * Sets the users line width input to the programs line width
     */
    public void setLineWidth(double width){
        this.line_width = width; 
    }
    
    /**
     * Sets the users colour input to the variable colour
     */
    public void setColour(){
        colour = JColorChooser.showDialog(null, "FillColor", Color.white); 
        UI.setColor(colour); 
    }
    
    public static void main(String[] arguments){
        new Pencil().setupGUI();
    }

}
