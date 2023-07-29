// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2021T2, Assignment 2
 * Name: Ella Wipatene
 * Username: wipateella
 * ID: 300558005
 */


import ecs100.*;
import java.util.*;
import java.util.Map.Entry;
import java.io.*;
import java.nio.file.*;
import java.lang.*;

/**
 * WellingtonTrains
 * A program to answer queries about Wellington train lines and timetables for
 *  the train services on those train lines.
 *
 * See the assignment page for a description of the program and what you have to do.
 */

public class WellingtonTrains{
    //Fields to store the collections of Stations and Lines
    /*# YOUR CODE HERE */
    private Map <String,Station> allStations = new HashMap<String,Station>(); //maybe change to a map, indexed by the name of the stations
    private Map <String,TrainLine> allTrainLines = new HashMap<String,TrainLine>(); // change to a map 
    
    // Fields for the suggested GUI.
    private String stationName;        // station to get info about, or to start journey from
    private String lineName;           // train line to get info about.
    private String destinationName;
    private int startTime = 0;         // time for enquiring about

    /**
     * main method:  load the data and set up the user interface
     */
    public static void main(String[] args){
        WellingtonTrains wel = new WellingtonTrains();
        wel.loadData();   // load all the data
        wel.setupGUI();   // set up the interface
    }

    /**
     * Load data files
     */
    public void loadData(){
        loadStationData();
        UI.println("Loaded Stations");
        loadTrainLineData();
        UI.println("Loaded Train Lines");
        // The following is only needed for the Completion and Challenge
        loadTrainServicesData();
        UI.println("Loaded Train Services");
    }

    /**
     * User interface has buttons for the queries and text fields to enter stations and train line
     * You will need to implement the methods here.
     */
    public void setupGUI(){
        UI.addButton("All Stations",        this::listAllStations);
        UI.addButton("Stations by name",    this::listStationsByName);
        UI.addButton("All Lines",           this::listAllTrainLines);
        UI.addTextField("Station",          (String name) -> {this.stationName=name;});
        UI.addTextField("Train Line",       (String name) -> {this.lineName=name;});
        UI.addTextField("Destination",      (String name) -> {this.destinationName=name;});
        UI.addTextField("Time (24hr)",      (String time) ->
            {try{this.startTime=Integer.parseInt(time);}catch(Exception e){UI.println("Enter four digits");}});
        UI.addButton("Lines of Station",    () -> {listLinesOfStation(this.stationName);});
        UI.addButton("Stations on Line",    () -> {listStationsOnLine(this.lineName);});
        UI.addButton("Stations connected?", () -> {checkConnected(this.stationName, this.destinationName);});
        UI.addButton("Next Services",       () -> {findNextServices(this.stationName, this.startTime);});
        UI.addButton("Find Trip",           () -> {findTrip(this.stationName, this.destinationName, this.startTime);});

        UI.addButton("Quit", UI::quit);
        UI.setMouseListener(this::doMouse);

        UI.setWindowSize(900, 400);
        UI.setDivider(0.2);
        // this is just to remind you to start the program using main!
        if (allStations.isEmpty()){
            UI.setFontSize(36);
            UI.drawString("Start the program from main", 2, 36);
            UI.drawString("in order to load the data", 2, 80);
            UI.sleep(2000);
            UI.quit();
        }
        else {
            UI.drawImage("data/geographic-map.png", 0, 0);
            UI.drawString("Click to list closest stations", 2, 12);
        }
    }
    
    /**
     * Returns the 10 closest stations to the position where the mouse was clicked. 
     */
    public void doMouse(String action, double x, double y){
        if (action.equals("released")){
            /*# YOUR CODE HERE 
               NOTE: I am assuming that each pixel = one km 
               */
            
            Map<Double, String> station_distances = new HashMap<Double, String>(); // double = distance, string = station name
            for(String station : allStations.keySet()){ // get the distances of all of the stations
                double dist = getDistance(x, y, allStations.get(station).getXCoord(), allStations.get(station).getYCoord()); 
                station_distances.put(dist, station); 
            }
            
            Set<Double> distances = station_distances.keySet(); // gets all of the distances  
            List<Double> sorted_distances = new ArrayList<Double>(distances);
            Collections.sort(sorted_distances); // sorts the distances from smallest to largest
            
            for(int i = 0; i < 10; i++){ // prints 10 closest stations
                UI.printf("%.2f km: %s \n", sorted_distances.get(i), station_distances.get(sorted_distances.get(i)) ); 
            }
        }
    }

    // Methods for loading data and answering queries

    /*# YOUR CODE HERE */
    /**
     * Returns the distance between two points on the map. 
     */
    public double getDistance(double x1, double y1, double x2, double y2){
        double dx = x1 - x2;
        double dy = y1 - y2; 
        return Math.hypot(dx,dy); 
    }
    
    /**
     * Loads all of the information from stations.data into the allStations collection. 
     */
    public void loadStationData(){
        try{ 
            List<String> allLines = Files.readAllLines(Path.of("data/stations.data"));
            for (String line : allLines ) {
                Scanner sc = new Scanner(line);
                String station_name = sc.next();
                int fare_zone = sc.nextInt();
                double x_cord = sc.nextDouble();
                double y_cord = sc.nextDouble(); 
                allStations.put(station_name, new Station(station_name, fare_zone, x_cord, y_cord)); // adds to station map and creates new station object
            }
        }catch(IOException e){UI.println("File reading failed: "+e);}
    }
    
    /**
     * Loads all of the Train Line Data from train-lines.data into the allTrainLines collection. 
     */
    public void loadTrainLineData(){
        try{ 
            List<String> allLines = Files . readAllLines(Path.of("data/train-lines.data"));
            for (String line : allLines ) {
                Scanner sc = new Scanner(line);
                String trainline_name = sc.next();
                allTrainLines.put(trainline_name, new TrainLine(trainline_name)); // adds to train line map and creates new train line object 
                
                List<String> allLines2 = Files.readAllLines(Path.of("data/" + trainline_name + "-stations.data")); // the file witn the trainLines stations 
                for (String line2 : allLines2){
                    Scanner scan = new Scanner(line2);
                    while (scan.hasNext()){ // adds stations to the train line object & the train line to the station object 
                        String station_name = scan.next();  
                        allTrainLines.get(trainline_name).addStation(allStations.get(station_name)); 
                        allStations.get(station_name).addTrainLine(allTrainLines.get(trainline_name)); 
                    }
                }
            }
        }catch(IOException e){UI.println("File reading failed: "+e);}
    }
    
    /**
     * Loads the service data for every train line into the TrainService class. 
     */
    public void loadTrainServicesData(){
        try{
            for(String tl : allTrainLines.keySet()){
                List<String> allLines = Files . readAllLines(Path.of("data/" + tl + "-services.data")); 
                for(String line: allLines){
                    Scanner scan = new Scanner(line);
                    TrainService temp = new TrainService(allTrainLines.get(tl)); 
                    while(scan.hasNextInt()){
                        temp.addTime(scan.nextInt()); 
                    }
                    allTrainLines.get(tl).addTrainService(temp); 
                }
            }
        }catch(IOException e){UI.println("File reading failed: "+e);}
    }
    
    /**
     * Lists all the stations in the region. 
     */
    public void listAllStations(){
        for(String st: allStations.keySet()){
            UI.println(allStations.get(st).toString()); 
        }
    }
    
    /**
     * List all the stations in alphabetic order (by name).
     */
    public void listStationsByName(){
        Set<String> station_names = allStations.keySet(); 
        List<String> sorted_stations = new ArrayList<String>(station_names);  
        Collections.sort(sorted_stations);
        for(String s: sorted_stations){
            UI.println(allStations.get(s).toString()); 
        }
    }
    
    /**
     * List all the train lines in the region. 
     */
    public void listAllTrainLines(){
        for(String trainLine: allTrainLines.keySet()){
            UI.println(allTrainLines.get(trainLine).toString()); 
        }
    }
    
    /**
     * List the train lines that go through a given station. 
     */
    public void listLinesOfStation(String station_name){
        Set<TrainLine> trainLines = allStations.get(station_name).getTrainLines(); 
        for(TrainLine tl : trainLines){
            UI.println(tl.toString()); 
        }
    }
    
    /**
     * List the stations along a given train line
     */
    public void listStationsOnLine(String line_name){
        List<Station> stations = allTrainLines.get(line_name).getStations(); 
        for(Station s: stations){
            UI.println(s.toString()); 
        }
    }
    
    /**
     * Check if two of the stations are connected. 
     */
    public void checkConnected(String station_name, String destination_name){
        if (allStations.containsKey(station_name) && allStations.containsKey(destination_name)){
            Set<TrainLine> trainLines = allStations.get(station_name).getTrainLines(); // all of the train lines from the station
            int zone_amount = Math.abs(allStations.get(station_name).getZone() - allStations.get(destination_name).getZone()); // calcs the zone dif
            String train_line = ""; 
            
            boolean connected = false; 
            for (TrainLine tl : trainLines){ // for each train line from the station
                List<Station> destination_stations = tl.getStations(); 
                for (Station s : destination_stations){ // check if it is the same as the destination station
                    if(s.getName().equals(destination_name) && destination_stations.indexOf(allStations.get(station_name)) < destination_stations.indexOf(allStations.get(destination_name))){
                        train_line = tl.getName(); 
                        connected = true; 
                        break;  
                    }
                }
            }
            
            if(connected == false){
                UI.println("No train line found from " + station_name + " to " + destination_name + "."); 
            }else{
                UI.println("The " + train_line + " line goes from " + station_name + " to " + destination_name + ".");
                UI.println("The trip goes through " + zone_amount + " zones."); 
            }
        }else{
            UI.println("Invalid Stations"); 
        }
    }
    
    /**
     * Find the next train service for each line at a station after the specified time. 
     */
    public void findNextServices(String station_name, int start_time){
        if (allStations.containsKey(station_name)){
            Set<TrainLine> trainLines = allStations.get(station_name).getTrainLines(); 
            for(TrainLine tl: trainLines){
                List<Station> stations = tl.getStations(); 
                int station_index = stations.indexOf(allStations.get(station_name)); //  index of station to match up with the service time
                
                List<TrainService> services = tl.getTrainServices(); 
                for(TrainService ts: services){
                    List<Integer> times = ts.getTimes(); 
                    if (times.get(station_index) >= start_time){
                        UI.println("Next service on " + tl.getName() + " from " + station_name + " is at " + times.get(station_index)); 
                        break; 
                    }                
                }
            }
        }else{
            UI.println("Please enter valid stations"); 
        }
    }
    
    /**
     * Find a trip between two stations, after the specified time.
     */
    public void findTrip(String station_name, String destination_name, int start_time){
        if (allStations.containsKey(station_name) && allStations.containsKey(destination_name)){
            int zone_amount = Math.abs(allStations.get(station_name).getZone() - allStations.get(destination_name).getZone()); // how many zones they will travel
            Set<TrainLine> trainLines = allStations.get(station_name).getTrainLines(); 
            boolean connected = false; 
            
            for (TrainLine tl : trainLines){ // for each train line from that station 
                List<Station> destination_stations = tl.getStations(); 
                for (Station s : destination_stations){
                    // check if the station is the destination and in the correct dirrection
                    if(s.getName().equals(destination_name) && destination_stations.indexOf(allStations.get(station_name)) < destination_stations.indexOf(allStations.get(destination_name))){
                        List<Station> stations = tl.getStations(); 
                        //gets the indexes in order to find the times of the services 
                        int station_index = stations.indexOf(allStations.get(station_name)); 
                        int destination_index = stations.indexOf(allStations.get(destination_name)); 
                        
                        List<TrainService> services = tl.getTrainServices(); 
                        for(TrainService ts: services){
                            List<Integer> times = ts.getTimes(); 
                            if(times.get(station_index) >= start_time && times.get(destination_index) != -1){ // makes sure it departs after the start time and stops at the destination station
                                UI.println(ts.getTrainID() + " leaves " + station_name + " at " + times.get(station_index) + ", arrives at " + destination_name + " at " + times.get(destination_index)); 
                                UI.println("The trip travels through " + zone_amount + " zones."); 
                                connected = true; // if they are connected it will not do the challenge code
                                break; 
                            }                        
                        }
                    }
                }
            }
            if(connected == false){ // if they are not dirrectly connected 
                Set<TrainLine> trainLinesDest = allStations.get(destination_name).getTrainLines();
                int min_zone_amount = 100000000; // to make sure that it is the shortest trip possible
                Station connector = null; 
                TrainLine to_line = null;
                TrainLine from_line = null; 
                
                for(TrainLine tl: trainLines){ // for all train lines from the departure station
                    List<Station> fromStation = tl.getStations();  // get the stations they stop at
                    for(TrainLine tl2: trainLinesDest){ // for all train lines tp the destination station
                        List<Station> toStation = tl2.getStations();  // get the stations they stop at
                        for(Station s: toStation){ 
                            if(fromStation.contains(s)){ // finds the connector station (both train lines stop at it) 
                                if(fromStation.indexOf(allStations.get(station_name)) < fromStation.indexOf(s)){ // so that the from train line is moving in the correct dir
                                    if (toStation.indexOf(s) < toStation.indexOf(allStations.get(destination_name))){ // so that the to train line is in the right dir
                                        zone_amount = Math.abs(allStations.get(station_name).getZone() - s.getZone()) + Math.abs(allStations.get(destination_name).getZone() - s.getZone());
                                        if (zone_amount < min_zone_amount){ // if it is a shorter trip than the previous one
                                            connector = s;
                                            to_line = tl;
                                            from_line = tl2; 
                                            min_zone_amount = zone_amount; 
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                
                List<Station> to_stations = to_line.getStations(); 
                List<Station> from_stations = from_line.getStations();
                
                //gets the indexes in order to find the times of the services 
                int station_index = to_stations.indexOf(allStations.get(station_name)); 
                int mid_index = to_stations.indexOf(connector); 
                int from_mid_index = from_stations.indexOf(connector); 
                int destination_index = from_stations.indexOf(allStations.get(destination_name)); 
                
                int mid_time = 0; // the time when the first train arrives at the connecting station
                
                List<TrainService> services = to_line.getTrainServices();  
                for(TrainService ts: services){ // gets first trip times
                    List<Integer> times = ts.getTimes(); 
                    if(times.get(station_index) >= start_time && times.get(mid_index) != -1){
                        UI.println(ts.getTrainID() + " leaves " + station_name + " at " + times.get(station_index) + ", arrives at " + connector.getName() + " at " + times.get(mid_index)); 
                        mid_time = times.get(mid_index);  
                        break; 
                    }  
                }
                 
                List<TrainService> from_services = from_line.getTrainServices(); 
                for(TrainService ts: from_services){ // gets second trip times
                    List<Integer> times = ts.getTimes(); 
                    if(times.get(from_mid_index) > mid_time && times.get(destination_index) != -1){
                        UI.println(ts.getTrainID() + " leaves " + connector.getName() + " at " + times.get(from_mid_index) + ", arrives at " + destination_name + " at " + times.get(destination_index)); 
                        break; 
                    }                        
                }
        
                UI.println("This trip will cover " + min_zone_amount + " zones."); 
            }
        }else{
            UI.println("Please enter valid stations"); 
        }
    }
} happy birthday el x
