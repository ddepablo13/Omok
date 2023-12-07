/**
 * Extends player and represents the behavior a Human Player.
 */
public class HumanPlayer extends PlayerInterface {
    private int[] lastMove;
    protected String name;

    /**
     * Sets the Human Player main features
     * @param stoneType Stone type that human will possess
     * @param name Name string that human will posses
     */
    public HumanPlayer(Stone stoneType, String name) {
        super(name, stoneType);
        this.name = name;
    }

    /**
     * Just sets the stone type of human player
     * @param stoneType Stone type that human will posses
     */
    public HumanPlayer(Stone stoneType) {
        super("NoName", stoneType);
        this.name = "Default name";
    }

    /**
     * Sets last move of the human player
     * @param lastMove to set up the lastMove
     */
    @Override
    public void setLastMove(int[] lastMove) {
        this.lastMove = lastMove;
    }

    /**
     * get the last move of the human player
     * @return last move made
     */
    @Override
    public int[] getLastMove() {
        return lastMove;
    }
}