import javax.swing.*;
import java.awt.*;

public class OmokGameDesign extends JPanel {

    private final ActualBoard actualBoard;
    private PlayerInterface currentPlayer;
    private final PlayerInterface player1;
    private final PlayerInterface player2;
    private final boolean isAIGame;
    private boolean isGameOver;

    private final PlayerUpdates player1Panel;
    private final PlayerUpdates player2Panel;

    public OmokGameDesign(PlayerUpdates player1Panel, PlayerInterface player1, PlayerUpdates player2Panel, PlayerInterface player2, boolean isAIGame) {
        this.isAIGame = isAIGame;
        actualBoard = new ActualBoard();
        this.setLayout(new BorderLayout());
        this.add(actualBoard, BorderLayout.CENTER);

        this.player1 = player1;
        this.player2 = isAIGame ? new ComputerPlayer(Stone.WHITE, actualBoard, player1) : player2;

        this.player1Panel = player1Panel;
        this.player2Panel = player2Panel;

        player1Panel.highlight(true);
        player2Panel.highlight(false);

        currentPlayer = player1;
        isGameOver = false;
        setupMouseListener();
    }

    private void setupMouseListener() {
        actualBoard.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (isGameOver) return;

                int col = e.getY() / ActualBoard.TILE_SIZE;
                int row = e.getX() / ActualBoard.TILE_SIZE;

                if (actualBoard.isOccupied(col, row)) {
                    JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(OmokGameDesign.this),
                            "This spot is occupied.");
                } else {
                    makeMove(col, row);
                    player1Panel.highlight(currentPlayer == player1);
                    player2Panel.highlight(currentPlayer == player2);
                }
            }
        });
    }

    public PlayerUpdates getPlayer1Panel() {
        return player1Panel;
    }

    public PlayerUpdates getPlayer2Panel() {
        return player2Panel;
    }

    public PlayerInterface getPlayer1() {
        return player1;
    }

    public PlayerInterface getPlayer2() {
        return player2;
    }

    private void makeMove(int x, int y) {
        if(currentPlayer instanceof JavaClientPlayer){
            handleServerResponse(x, y, (JavaClientPlayer) currentPlayer);
        }else {
            actualBoard.placeStone(x, y, currentPlayer);
            currentPlayer.setLastMove(new int[]{x, y});
            if (currentPlayer == player1) {
                player1Panel.moveMade(new int[]{x, y});
            } else {
                player2Panel.moveMade(new int[]{x, y});
            }
            detectWin();
            if (!isGameOver && isAIGame && currentPlayer instanceof ComputerPlayer) {
                SwingUtilities.invokeLater(this::aiMakeMove);
            }
        }
    }
    private void handleServerResponse(int x, int y, JavaClientPlayer javaClientPlayer) {
        // Replace 'gameID' with actual game ID from javaClientPlayer
        String gameID = javaClientPlayer.getGameID();
        String query = String.format("play/?pid=%s&x=%d&y=%d", gameID, x, y);

        String response = javaClientPlayer.getClient().sendGet(query);
        int[] serverMove = javaClientPlayer.parseMove(response);

        if (serverMove != null) {
            if(!actualBoard.isOccupied(serverMove[0], serverMove[1])) {
                actualBoard.placeStone(serverMove[0], serverMove[1], javaClientPlayer);
                javaClientPlayer.setLastMove(serverMove);
                player2Panel.moveMade(serverMove);
                detectWin();
            }else{
                System.out.println("Server tried to place a stone on occupied spot: [" + serverMove[0] + ", " + serverMove[1] + "]");
            }
        } else {
            System.err.println("Error handling server response"); // Error logging
        }
    }

    private void detectWin() {
        if (actualBoard.isWonBy(currentPlayer)) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),
                    currentPlayer.getName() + " wins!");
            isGameOver = true;
        } else if (actualBoard.isFull()) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),
                    "It's a draw!");
            isGameOver = true;
        } else {
            switchPlayer();
        }
        actualBoard.repaint();
        player1Panel.highlight(currentPlayer == player1);
        player2Panel.highlight(currentPlayer == player2);
    }

    private void aiMakeMove() {
        int[] aiMove = ((ComputerPlayer) currentPlayer).bestMove();
        if (aiMove != null) {
            actualBoard.placeStone(aiMove[0], aiMove[1], currentPlayer);
            currentPlayer.setLastMove(aiMove);
            player2Panel.moveMade(aiMove);
            detectWin();
        }
    }

    public void changeUserNames(String name1, String name2){
        this.player1.name = name1;
        this.player2.name = name2;
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
        player1Panel.highlight(currentPlayer == player1);
        player2Panel.highlight(currentPlayer == player2);
    }
    public void resetGame() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Reset the game?",
                "Reset Game", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            actualBoard.clear();
            if (currentPlayer != player1) {
                switchPlayer();
            }
            currentPlayer = player1;
            isGameOver = false;
            actualBoard.repaint();
        }
    }

    public int[] getLastMove(){
        return new int[]{actualBoard.getLastMoveX(), actualBoard.getLastMoveY()};
    }
}