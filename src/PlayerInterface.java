/**
 * Abstract of basic player.
 */
public abstract class PlayerInterface {
    protected Stone stoneType;
    protected String name;
    protected int[] lastMove;

    public PlayerInterface(String name , Stone stoneType) {
        this.stoneType = stoneType;
        this.name = name;
    }
    public abstract int[] makeMove(Stone[][] board);

    public Stone getStoneType() {
        return stoneType;
    }

    public int[] getLastMove() {
        return lastMove;
    }

    public void setLastMove(int[] move) {
        this.lastMove = move;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}