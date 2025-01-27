import java.util.ArrayList;
import java.util.List;

public abstract class Ship{
    private String name;    // Ship name
    private int size;   // Ship size
    private List<String> positions; // Number of list position
    private List<String> hitPositions; // Number of hits
    private boolean isSunk; // Status of the ship

    // Constructor
    public Ship(String name, int size){
        this.name = name;
        this.size = size;
        this.positions = new ArrayList<>();
        this.hitPositions = new ArrayList<>();
        this.isSunk = false;
    }

    // Getters
    public String getName(){
        return name;
    }

    public int getSize(){
        return size;
    }

    public boolean isSunk(){
        return isSunk;
    }

    // Place a ship by assigning positions
    public void placeShip(List<String> positions) {
        if(positions.size() == size) {
            this.positions = new ArrayList<>(positions);
        }else{
            throw new IllegalArgumentException("Position count mismatch.");
        }
    }

    // Register Hit
    public void registerHit(String position){
        if(positions.contains(position) && !hitPositions.contains(position)){
            hitPositions.add(position);
            if(hitPositions.size() == size){
                isSunk = true;
            }
        }
    }

    // Check if a specific position is part of the ship
    public boolean containsPosition(String position){
        return positions.contains(position);
    }
}