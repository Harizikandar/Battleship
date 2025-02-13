import java.util.ArrayList;
import java.util.List;

public abstract class Ship {
    private String name;
    private int size;
    private List<String> positions;
    private List<String> hitPositions;
    private boolean isSunk;

    public Ship(String name, int size) {
        this.name = name;
        this.size = size;
        this.positions = new ArrayList<>();
        this.hitPositions = new ArrayList<>();
        this.isSunk = false;
    }

    public String getName() { return name; }
    public int getSize() { return size; }
    public boolean isSunk() { return isSunk; }

    public static boolean isValidPlacement(List<String> positions, int shipSize) {
        if (positions.size() != shipSize) return false;
        
        // Check if positions are in a straight line.
        boolean isHorizontal = true;
        boolean isVertical = true;
        
        char firstCol = positions.get(0).charAt(0);
        int firstRow = Integer.parseInt(positions.get(0).substring(1));
        
        for (int i = 1; i < positions.size(); i++) {
            char currentCol = positions.get(i).charAt(0);
            int currentRow = Integer.parseInt(positions.get(i).substring(1));
            
            if (currentCol != firstCol) isVertical = false;
            if (currentRow != firstRow) isHorizontal = false;
        }
        
        return isHorizontal || isVertical;
    }

    public void placeShip(List<String> positions) {
        if (isValidPlacement(positions, size)) {
            this.positions = new ArrayList<>(positions);
        } else {
            throw new IllegalArgumentException("Invalid ship placement");
        }
    }

    public String registerHit(String position) {
        if (positions.contains(position) && !hitPositions.contains(position)) {
            hitPositions.add(position);
            if (hitPositions.size() == size) {
                isSunk = true;
                return "You sank " + name + "!";
            }
            return "Hit!";
        }
        return "Miss!";
    }

    public boolean containsPosition(String position) {
        return positions.contains(position);
    }

    public List<String> getPositions() {
        return positions;
    }
    
}
