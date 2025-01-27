import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        GameBoard board = new GameBoard(10, 10);
        Scanner scanner = new Scanner(System.in);

        Ship battleShip = new Battleship();
        Ship destroyer = new Destroyer();
        Ship submarine = new Submarine();

        // Place Battleship
        boolean placed = false;
        while(!placed){
            System.out.println("Current Board:");
            board.displayBoard(true);
            System.out.println("Placing Battleship (4 cells). Enter positions (e.g., A1 A2 A3 A4): ");
            List<String> battleshipPositions = Arrays.asList(scanner.nextLine().trim().split("\\s+"));
            if(board.placeShip(battleShip, battleshipPositions)){
                System.out.println("Battleship placed successfully!");
                placed = true;
            }else{
                System.out.println("Failed to place Battleship.");
            }
        }
        board.displayBoard(false);

        // Place Destroyer
        placed = false;
        while(!placed){
            System.out.println("Placing Destroyer (3 cells). Enter positions (e.g., B1 B2 B3):");
            List<String> destroyerPositions = Arrays.asList(scanner.nextLine().trim().split("\\s+"));
            if(board.placeShip(destroyer, destroyerPositions)){
                System.out.println("Destroyer placed successfully!");
                placed = true;
            }else{
                System.out.println("Failed to place Destroyer.");
            }
        }
        board.displayBoard(false);

        // Place Submarine
        placed = false;
        while(!placed){
            System.out.println("Placing Submarine (2 cells). Enter positions (e.g., C1 C2):");
            List<String> submarinePositions = Arrays.asList(scanner.nextLine().trim().split("\\s+"));
            if(board.placeShip(submarine, submarinePositions)){
                System.out.println("Submarine placed successfully!");
                placed = true;
            }else{
                System.out.println("Failed to place Submarine.");
            }
        }
        board.displayBoard(false);

        // Close the scanner to avoid resource leak
        scanner.close();
    }
}