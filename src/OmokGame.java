import javax.swing.*;
import java.awt.*;

/**
 *Were most objects converged and apply its functionalities to control the flow of the game.
 */
public class OmokGame extends JPanel {

    private final Board board;
    private Player currentPlayer;
    private final Player player1;
    private final Player player2;
    private final boolean isAIGame;
    private boolean isGameOver;

    private final PlayerPanel player1Panel;
    private final PlayerPanel player2Panel;

    /**
     * Sets object to desire attributes
     * @param player1Panel represents the panel associated with player 1
     * @param player1 represents the user's player
     * @param player2Panel represents the panel associated with player 2
     * @param player2 represents weather is another human player or an AI
     * @param isAIGame check if is a game against the AI
     */
    public OmokGame(PlayerPanel player1Panel, Player player1, PlayerPanel player2Panel, Player player2, boolean isAIGame) {
        this.isAIGame = isAIGame;
        board = new Board();
        this.setLayout(new BorderLayout());
        this.add(board, BorderLayout.CENTER);

        this.player1 = player1;
        this.player2 = isAIGame ? new AIPlayer(Stone.WHITE, board, player1) : player2;

        this.player1Panel = player1Panel;
        this.player2Panel = player2Panel;

        player1Panel.highlight(true);
        player2Panel.highlight(false);

        currentPlayer = player1;
        isGameOver = false;
        setupMouseListener();
    }

    /**
     * Sets up the events for mouse being clicked, (making a move where mouse was clicked).
     */
    private void setupMouseListener() {
        board.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (isGameOver) return;

                int col = e.getY() / Board.TILE_SIZE;
                int row = e.getX() / Board.TILE_SIZE;

                if (board.isOccupied(col, row)) {
                    JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(OmokGame.this),
                            "This spot is occupied.");
                } else {
                    makeMove(col, row);
                    player1Panel.highlight(currentPlayer == player1);
                    player2Panel.highlight(currentPlayer == player2);
                }
            }
        });
    }

    /**
     * get players 1 panel
     * @return object of type PlayerPanel
     */
    public PlayerPanel getPlayer1Panel() {
        return player1Panel;
    }
    /**
     * get players 2 panel
     * @return object of type PlayerPanel
     */
    public PlayerPanel getPlayer2Panel() {
        return player2Panel;
    }
    /**
     * gets players 1
     * @return object of type Player
     */
    public Player getPlayer1() {
        return player1;
    }
    /**
     * gets players 2
     * @return object of type Player
     */
    public Player getPlayer2() {
        return player2;
    }

    /**
     * Places a stone associated with the current player in the given coordinates
     * @param x represents the 'row' in the board
     * @param y represents the 'column' in the board
     */
    private void makeMove(int x, int y) {
        if(currentPlayer instanceof JavaClientPlayer){
            handleServerResponse(x, y, (JavaClientPlayer) currentPlayer);
        }else {
            board.placeStone(x, y, currentPlayer);
            currentPlayer.setLastMove(new int[]{x, y});
            if (currentPlayer == player1) {
                player1Panel.moveMade(new int[]{x, y});
            } else {
                player2Panel.moveMade(new int[]{x, y});
            }
            detectWin();
            if (!isGameOver && isAIGame && currentPlayer instanceof AIPlayer) {
                SwingUtilities.invokeLater(this::aiMakeMove);
            }
        }
    }
    private void handleServerResponse(int x, int y, JavaClientPlayer javaClientPlayer) {
        // Replace 'gameID' with actual game ID from javaClientPlayer
        String gameID = javaClientPlayer.getGameID(); // Assuming getGameID() method exists
        String query = String.format("play/?pid=%s&x=%d&y=%d", gameID, x, y);

        String response = javaClientPlayer.getClient().sendGet(query);
        int[] serverMove = javaClientPlayer.parseMove(response);

        if (serverMove != null) {
            board.placeStone(serverMove[0], serverMove[1], javaClientPlayer);
            javaClientPlayer.setLastMove(serverMove);
            player2Panel.moveMade(serverMove);
            detectWin();
        } else {
            System.err.println("Error handling server response"); // Error logging
        }
    }

    /**
     * Check if the current player has won or if there is a draw.
     */
    private void detectWin() {
        if (board.isWonBy(currentPlayer)) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),
                    currentPlayer.getName() + " wins!");
            isGameOver = true;
        } else if (board.isFull()) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),
                    "It's a draw!");
            isGameOver = true;
        } else {
            switchPlayer();
        }
        board.repaint();
        player1Panel.highlight(currentPlayer == player1);
        player2Panel.highlight(currentPlayer == player2);
    }

    /**
     * Get and place AIs the best move
     */
    private void aiMakeMove() {
        int[] aiMove = ((AIPlayer) currentPlayer).bestMove();
        if (aiMove != null) {
            board.placeStone(aiMove[0], aiMove[1], currentPlayer);
            currentPlayer.setLastMove(aiMove);
            player2Panel.moveMade(aiMove);
            detectWin();
        }
    }

    /**
     * Change names of users
     * @param name1 new name for player 1
     * @param name2 new name for player 2
     */
    public void changeUserNames(String name1, String name2){
        this.player1.name = name1;
        this.player2.name = name2;
    }

    /**
     * Change the current player to the other player.
     */
    private void switchPlayer() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
        player1Panel.highlight(currentPlayer == player1);
        player2Panel.highlight(currentPlayer == player2);
    }

    /**
     * Resets the game with a new board allowing player 1 to make the first move
     */
    public void resetGame() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Reset the game?",
                "Reset Game", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            board.clear();
            if (currentPlayer != player1) {
                switchPlayer();
            }
            currentPlayer = player1;
            isGameOver = false;
            board.repaint();
        }
    }


    /**
     * Get the last move made in the board
     * @return Array of length representing the coordinates of the last move
     */
    public int[] getLastMove(){
        return new int[]{board.getLastMoveX(), board.getLastMoveY()};
    }
}