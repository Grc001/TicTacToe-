import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;


public class TicTacToe {

    private int N;
    private char[][] board;
    private final char[] players = {'X', 'O'};
    private int currentPlayerIndex = 0;

    private int moves = 0;

    private int[][] winningLine = null;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TicTacToe game = new TicTacToe();
        if (!game.loadGame()) {
            System.out.println("Entrez le nombre de dimensions du tableau N (>= 3):");
            int N = scanner.nextInt();
            while (N < 3) {
                System.out.println("invalide, veuillez choisir un autre nombre (>= 3):");
                N = scanner.nextInt();
            }
            game.initializeBoard(N);
        }
        game.play();
    }

    public void initializeBoard(int dimension) {
        this.N = dimension;
        this.board = new char[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                board[i][j] = ' ';
            }
        }
    }

    public TicTacToe() {
        // Si une sauvegarde existe, elle sera chargée dans le constructeur.
        if (!loadGame()) {
            N = 3; // Valeur par défaut
            board = new char[N][N];
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    board[i][j] = ' ';
                }
            }
        }
    }

    public void play() {
        Scanner scanner = new Scanner(System.in);
        if (loadGame()) {
            System.out.println("Game loaded!");
            moves = moves;
        }

        while (moves < N * N) {
            printBoard();
            int move = -1;
            while (true) {
                System.out.println("Joueur " + players[currentPlayerIndex] + ", faites votre choix (1-" + (N * N) + ") ou tapez '/save' pour sauvegarder et quitter la partie:");
                String input = scanner.nextLine();
                if ("/save".equals(input)) {
                    saveGame();
                    System.out.println("Game saved!");
                    return;
                }
                try {
                    move = Integer.parseInt(input);
                    if (isValidMove(move)) {
                        printBoardWithPreselection(move);
                        System.out.println("Appuyez sur Enter pour confirmer ou entrez un nouveau choix...");
                        while (true) {
                            String confirmation = scanner.nextLine();
                            if (confirmation.isEmpty()) { // If user pressed Enter
                                break;
                            }
                            try {
                                int newMove = Integer.parseInt(confirmation);
                                if (isValidMove(newMove)) {
                                    move = newMove;
                                    printBoardWithPreselection(move);
                                    System.out.println("Appuyez sur Enter pour confirmer ou entrez un nouveau choix...");
                                } else {
                                    System.out.println("Choix invalide, veuillez entrer un nouveau choix ou appuyez sur Enter pour confirmer le précédent.");
                                }
                            } catch (NumberFormatException innerEx) {
                                System.out.println("Veuillez entrer un nombre valide ou '/save' pour sauvegarder.");
                            }
                        }
                        break; // Break out of the outer loop once the choice is confirmed
                    } else {
                        System.out.println("Choix invalide, veuillez rejouer");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Veuillez entrer un nombre valide ou '/save' pour sauvegarder.");
                }
            }

            int row = (move - 1) / N;
            int col = (move - 1) % N;
            board[row][col] = players[currentPlayerIndex];
            moves++;
            if (hasWon(row, col)) {
                printBoard();
                System.out.println("Le joueur " + players[currentPlayerIndex] + " gagne la partie, BRAVO!");
                deleteSaveFile();  // Supprimez le fichier de sauvegarde ici
                return;
            }
            currentPlayerIndex = 1 - currentPlayerIndex;
        }
        printBoard();
        System.out.println("Match nuuuuUUUuuuul!");
        deleteSaveFile();
    }


    private boolean isValidMove(int move) {
        if (move < 1 || move > N * N) return false;
        int row = (move - 1) / N;
        int col = (move - 1) % N;
        return board[row][col] == ' ';
    }

    private boolean hasWon(int row, int col) {
        char currentPlayer = players[currentPlayerIndex];
        if (checkLine(row, 0, 0, 1)) {
            winningLine = new int[N][2];
            for (int i = 0; i < N; i++) {
                winningLine[i] = new int[]{row, i};
            }
            return true;
        }
        if (checkLine(0, col, 1, 0)) {
            winningLine = new int[N][2];
            for (int i = 0; i < N; i++) {
                winningLine[i] = new int[]{i, col};
            }
            return true;
        }
        if (row == col && checkLine(0, 0, 1, 1)) {
            winningLine = new int[N][2];
            for (int i = 0; i < N; i++) {
                winningLine[i] = new int[]{i, i};
            }
            return true;
        }
        if (row + col == N - 1 && checkLine(0, N - 1, 1, -1)) {
            winningLine = new int[N][2];
            for (int i = 0; i < N; i++) {
                winningLine[i] = new int[]{i, N - 1 - i};
            }
            return true;
        }
        return false;
    }


    private boolean checkLine(int startRow, int startCol, int dRow, int dCol) {
        char currentPlayer = players[currentPlayerIndex];
        for (int i = 0; i < N; i++) {
            if (board[startRow + i * dRow][startCol + i * dCol] != currentPlayer) {
                return false;
            }
        }
        return true;
    }
    private void printBoardWithPreselection(int move) {
        int preselectRow = (move - 1) / N;
        int preselectCol = (move - 1) % N;

        for (int i = 0; i < N; i++) {
            System.out.print("|");
            for (int j = 0; j < N; j++) {
                if (i == preselectRow && j == preselectCol) {
                    System.out.print("\u001B[33m"); // ANSI code for yellow
                    System.out.print(">" + (i * N + j + 1) + "<");
                    System.out.print("\u001B[0m"); // ANSI code to reset color
                } else if (board[i][j] == ' ') {
                    System.out.print(" " + (i * N + j + 1) + " ");
                } else {
                    System.out.print(" " + board[i][j] + " ");
                }
            }
            System.out.println("|");
        }
    }



    private void printBoard() {
        for (int i = 0; i < N; i++) {
            System.out.print("|");
            for (int j = 0; j < N; j++) {
                if (isWinningCell(i, j)) {
                    System.out.print("\u001B[32m"); // ANSI code for green
                } else if (board[i][j] == 'X') {
                    System.out.print("\u001B[34m"); // ANSI code for blue
                } else if (board[i][j] == 'O') {
                    System.out.print("\u001B[31m"); // ANSI code for red
                }

                if (board[i][j] == ' ') {
                    System.out.print(" " + (i * N + j + 1) + " ");
                } else {
                    System.out.print(" " + board[i][j]+ " ");
                }

                System.out.print("\u001B[0m"); // ANSI code to reset color
            }
            System.out.println("|");
        }
    }

    private boolean isWinningCell(int row, int col) {
        if (winningLine == null) return false;
        for (int[] cell : winningLine) {
            if (cell[0] == row && cell[1] == col) {
                return true;
            }
        }
        return false;
    }
    private void saveGame() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(N).append("\n"); // Sauvegardez la valeur de N en premier
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    sb.append(board[i][j]);
                }
                sb.append("\n");
            }
            sb.append(currentPlayerIndex).append("\n");
            sb.append(moves).append("\n");
            Files.write(Paths.get("game_save.txt"), sb.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    private int countMoves() {
//        int count = 0;
//        for (int i = 0; i < N; i++) {
//            for (int j = 0; j < N; j++) {
//                if (board[i][j] != ' ') {
//                    count++;
//                }
//            }
//        }
//        return count;
//    }


    public boolean loadGame() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("game_save.txt"));
            N = Integer.parseInt(lines.get(0)); // Lisez la valeur de N en premier
            board = new char[N][N]; // Initialisez le tableau avec la valeur de N
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    board[i][j] = lines.get(i + 1).charAt(j); // +1 car la première ligne est N
                }
            }
            currentPlayerIndex = Integer.parseInt(lines.get(N + 1)); // +1 car la première ligne est N
            moves = Integer.parseInt(lines.get(N+2));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    private void deleteSaveFile() {
        try {
            Files.deleteIfExists(Paths.get("game_save.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
