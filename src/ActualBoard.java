import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// The ActualBoard class extends JPanel, indicating it's a GUI component.
public class ActualBoard extends JPanel {
    // Counter for the number of stones on the board.

    private int counter_stones = 0;
    // Constants for tile size and board size, defining the dimensions of the game board.
    protected static final int TILE_SIZE = 30;
    protected static final int BOARD_SIZE = 15;
    // A two-dimensional array representing the grid of the board.
    private static PlayerInterface[][] grid = new PlayerInterface[BOARD_SIZE][BOARD_SIZE];
    // Variables to track the last move made on the board.
    private int lastMoveX = -1;
    private int lastMoveY = -1;
    // Variables to track the mouse position on the board.
    private int mouseRow = -1;
    private int mouseCol = -1;
    // A list to store places, which might be specific positions or areas on the board.
    private List<Place> places_list = new ArrayList<>();
    private BufferedImage backgroundImage;

    public ActualBoard() {
        // Setting the preferred size of the panel based on the board size and tile size.
        this.setPreferredSize(new Dimension(BOARD_SIZE * TILE_SIZE, BOARD_SIZE * TILE_SIZE));
        // Attempt to load a background image, handling potential IO exceptions.
        try {
            backgroundImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("images/CloudBackground.png")));
        } catch (IOException e) {
            e.printStackTrace();
            backgroundImage = null;
        }
        clear();
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX() / TILE_SIZE;
                int y = e.getY() / TILE_SIZE;
                lastMoveX = x;
                lastMoveY = y;
                repaint();
            }
        });
        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseRow = e.getY() / TILE_SIZE;
                mouseCol = e.getX() / TILE_SIZE;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
        }

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                PlayerInterface player = grid[i][j];
                if (player != null) {
                    g.setColor(player.getStoneType().equals(Stone.BLACK) ? Color.BLACK : Color.GRAY);
                    g.fillOval(j * TILE_SIZE, i * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                } else {
                    g.setColor(Color.LIGHT_GRAY);
                    g.drawOval(j * TILE_SIZE, i * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        List<Place> winningPlaces = winningRow();
        if (winningPlaces != null) {
            g.setColor(Color.RED);
            for (Place place : winningPlaces) {
                g.drawOval(place.y * TILE_SIZE, place.x * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(place.y * TILE_SIZE + 1, place.x * TILE_SIZE + 1, TILE_SIZE - 2, TILE_SIZE - 2);
            }
        }

        if (mouseRow >= 0 && mouseRow < BOARD_SIZE && mouseCol >= 0 && mouseCol < BOARD_SIZE) {
            g.setColor(new Color(255, 255, 0, 128));
            g.fillOval(mouseCol * TILE_SIZE, mouseRow * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }
    }

    public int getLastMoveX() {
        return lastMoveX;
    }

    public int getLastMoveY() {
        return lastMoveY;
    }

    public ActualBoard(int size) {
        grid = new PlayerInterface[size][size];
    }

// Returns the size of the board, which is the length of the grid.
    public int sizeBoard() {
        return grid.length;
    }

// Clears the board by reinitializing the grid with PlayerInterface objects.
    public void clear() {
        grid = new PlayerInterface[sizeBoard()][sizeBoard()];
    }

// Checks if the board is full by comparing the counter of stones with the total number of positions on the board.
    public boolean isFull() {
        return counter_stones == sizeBoard()*sizeBoard();
    }

    public void placeStone(int x, int y, PlayerInterface player) {
        counter_stones++;
        lastMoveX = x;
        lastMoveY = y;
        grid[x][y] = player;
    }

    public boolean isEmpty(int x, int y) {
        return grid[x][y]== null;
    }

    public boolean isOccupied(int x, int y) {
        return grid[x][y] != null;
    }

    public boolean isOccupiedBy(int x, int y, PlayerInterface player) {
        return grid[x][y].equals(player);
    }

    public PlayerInterface playerAt(int x, int y) {
        return grid[x][y];
    }

    /**
     * Checks for a winning sequence in a specific direction (dx, dy) starting from (x, y) for a given player.
     * It adds the positions to places_list if they are part of a winning sequence. Returns true if 5 consecutive positions belong to the player.
     */
    private boolean checkDirection(int x, int y, PlayerInterface player, int dx, int dy) {
        int count = 0;
        for (int i = -4; i <= 4; i++) {
            int newX = x + dx * i;
            int newY = y + dy * i;

            if (newX >= 0 && newX < sizeBoard() && newY >= 0 && newY < sizeBoard() && grid[newX][newY] == player) {
                count++;
                places_list.add(new Place(newX, newY));
                if (count == 5) return true;
            } else {
                places_list.clear();
                count = 0;
            }
        }

        return false;
    }
    private boolean checkDirection(int x, int y, int dx, int dy) {
        int count = 0;
        Object currentPlayer = grid[x][y];

        if (currentPlayer == null) {
            return false;
        }

        List<Place> tempPlacesList = new ArrayList<>();

        for (int i = -4; i <= 4; i++) {
            int newX = x + dx * i;
            int newY = y + dy * i;

            if (newX >= 0 && newX < sizeBoard() && newY >= 0 && newY < sizeBoard() && grid[newX][newY] == currentPlayer) {
                count++;
                tempPlacesList.add(new Place(newX, newY));
                if (count == 5) {
                    places_list = tempPlacesList;
                    return true;
                }
            } else {
                tempPlacesList.clear();
                count = 0;
            }
        }

        return false;
    }

    /**
     * Checks if the given player has won by having five consecutive tiles in any direction.
     * It calls checkDirection for horizontal, vertical, and both diagonal directions.
     */
    private boolean checkWin(int x, int y, PlayerInterface player) {
        if (x == -1 || y == -1){
            return true;
        }
        return checkDirection(x, y, player, 1, 0) ||
                checkDirection(x, y, player, 0, 1) ||
                checkDirection(x, y, player, 1, 1) ||
                checkDirection(x, y, player, 1, -1);

    }
    private boolean checkWin(int x, int y) {
        return checkDirection(x, y, 1, 0) ||
                checkDirection(x, y, 0, 1) ||
                checkDirection(x, y, 1, 1) ||
                checkDirection(x, y, 1, -1);
    }

    /**
     * Checks the entire board to determine if the given player has won.
     * Iterates through each cell, and if it belongs to the currentPlayer, checks for a winning condition at that cell.
     */
    private boolean hasCurrentPlayerWon(PlayerInterface currentPlayer) {
        for (int i = 0; i < sizeBoard(); i++) {
            for (int j = 0; j < sizeBoard(); j++) {
                if (grid[i][j] == currentPlayer &&
                        checkWin(i, j, currentPlayer)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Public method that delegates to hasCurrentPlayerWon to determine if the specified player has won.
    public boolean isWonBy(PlayerInterface player) {
        return hasCurrentPlayerWon(player);
    }

    /**
     * Determines the winning row (line of consecutive tiles) for the current state of the board.
     * If a win condition is found, it returns the list of winning places; otherwise, returns null.
     */
    public List<Place> winningRow() {
        for (int i = 0; i < sizeBoard(); i++) {
            for (int j = 0; j < sizeBoard(); j++) {
                if (checkWin(i, j)) {
                    return places_list;
                }
            }
        }
        return null;
    }

// Returns the current grid representing the state of the board.
    public PlayerInterface[][] getGrid(){
        return grid;
    }

    /**
     * Converts the current grid of PlayerInterface to a grid of Stone objects.
     * If a cell in the grid is occupied by a player, it gets the corresponding Stone type; if not, it remains null.
     */
    public Stone[][] getStoneGrid() {
        Stone[][] stoneGrid = new Stone[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                stoneGrid[i][j] = grid[i][j] != null ? grid[i][j].getStoneType() : null;
            }
        }
        return stoneGrid;
    }

    /**
     * An inner class representing a location on the board with x and y coordinates.
     * Provides a method to return a string representation of the Place.
     */
    public static class Place {
        @Override
        public String toString() {
            return "Place{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }

        public final int x;

        public final int y;


        public Place(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}


