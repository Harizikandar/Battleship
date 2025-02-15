import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class GameController {
    private GameBoard playerBoard; // Human player's board
    private GameBoard enemyBoard;  // Computer's board
    private Random random;

    public GameController() {
        playerBoard = new GameBoard(10, 10);
        enemyBoard = new GameBoard(10, 10);
        random = new Random();
        autoSetupPhase();
    }

    // Automatically place ships on both boards using fixed positions.
    private void autoSetupPhase() {
        // For player's board:
        List<String> pBattleship = Arrays.asList("A1", "A2", "A3", "A4");
        List<String> pDestroyer = Arrays.asList("C1", "C2", "C3");
        List<String> pSubmarine = Arrays.asList("E1", "E2");
        List<String> pCarrier = Arrays.asList("G1", "G2", "G3", "G4", "G5");
        List<String> pCruiser = Arrays.asList("I1", "I2", "I3");
        playerBoard.placeShip(new Battleship(), pBattleship);
        playerBoard.placeShip(new Destroyer(), pDestroyer);
        playerBoard.placeShip(new Submarine(), pSubmarine);
        playerBoard.placeShip(new Carrier(), pCarrier);
        playerBoard.placeShip(new Cruiser(), pCruiser);
        
        // For enemy (computer) board:
        List<String> eBattleship = Arrays.asList("J7", "J8", "J9", "J10");
        List<String> eDestroyer = Arrays.asList("F5", "F6", "F7");
        List<String> eSubmarine = Arrays.asList("H3", "H4");
        List<String> eCarrier = Arrays.asList("D1", "D2", "D3", "D4", "D5");
        List<String> eCruiser = Arrays.asList("B1", "B2", "B3");
        enemyBoard.placeShip(new Battleship(), eBattleship);
        enemyBoard.placeShip(new Destroyer(), eDestroyer);
        enemyBoard.placeShip(new Submarine(), eSubmarine);
        enemyBoard.placeShip(new Carrier(), eCarrier);
        enemyBoard.placeShip(new Cruiser(), eCruiser);
    }

    // Process the player's attack on the enemy board.
    public String playerAttack(String position) {
        return enemyBoard.attackPosition(position);
    }

    // The computer selects a random available position on the player's board and attacks.
    public String computerAttack() {
        List<String> availablePositions = new ArrayList<>();
        for (int row = 0; row < playerBoard.getRows(); row++) {
            for (int col = 0; col < playerBoard.getCols(); col++) {
                char cell = playerBoard.getBoard()[row][col];
                if (cell != 'X' && cell != 'M') {
                    String pos = "" + (char)('A' + col) + (row + 1);
                    availablePositions.add(pos);
                }
            }
        }
        if (availablePositions.isEmpty()) return "No available moves!";
        String pos = availablePositions.get(random.nextInt(availablePositions.size()));
        String result = playerBoard.attackPosition(pos);
        System.out.println("Computer attacks " + pos + ": " + result);
        return result;
    }

    public GameBoard getPlayerBoard() {
        return playerBoard;
    }

    public GameBoard getEnemyBoard() {
        return enemyBoard;
    }
    
    // Checks whether either side has lost all ships.
    public boolean isGameOver() {
        return playerBoard.allShipsSunk() || enemyBoard.allShipsSunk();
    }
}
