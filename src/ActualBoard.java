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

public class ActualBoard extends JPanel {

    private int counter_stones = 0;

    protected static final int TILE_SIZE = 30;
    protected static final int BOARD_SIZE = 15;
    private static PlayerInterface[][] grid = new PlayerInterface[BOARD_SIZE][BOARD_SIZE];
    private int lastMoveX = -1;
    private int lastMoveY = -1;

    private int mouseRow = -1;
    private int mouseCol = -1;
    private List<Place> places_list = new ArrayList<>();
    private BufferedImage backgroundImage;

    public ActualBoard() {
        this.setPreferredSize(new Dimension(BOARD_SIZE * TILE_SIZE, BOARD_SIZE * TILE_SIZE));
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

    public int sizeBoard() {
        return grid.length;
    }

    public void clear() {
        grid = new PlayerInterface[sizeBoard()][sizeBoard()];
    }

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

    public boolean isWonBy(PlayerInterface player) {
        return hasCurrentPlayerWon(player);
    }

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

    public PlayerInterface[][] getGrid(){
        return grid;
    }

    public Stone[][] getStoneGrid() {
        Stone[][] stoneGrid = new Stone[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                stoneGrid[i][j] = grid[i][j] != null ? grid[i][j].getStoneType() : null;
            }
        }
        return stoneGrid;
    }

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


