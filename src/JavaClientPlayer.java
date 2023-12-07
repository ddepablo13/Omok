import org.json.JSONObject;

public class JavaClientPlayer extends PlayerInterface {

    private JavaClient javaClient;
    private String gameID;
    private String strategy;

    public JavaClientPlayer(Stone stoneType, String strategy) {
        super("Java Client", stoneType);
        this.strategy = strategy;
        this.javaClient = new JavaClient();
        initiateGame();
    }
    public JavaClient getClient() {
        return javaClient;
    }
    public String getGameID() {
        return this.gameID;
    }


    private void initiateGame() {
        String response = javaClient.sendGet("new/?strategy=" + strategy);
        this.gameID = parseGameID(response);
        System.out.println("Game initiated with ID: " + gameID); // Debug statement
    }

    private String parseGameID(String response) {
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.getBoolean("response")) {
            String pid = jsonObject.getString("pid");
            System.out.println("Received game ID: " + pid); // Debug statement
            return pid;
        } else {
            String reason = jsonObject.optString("reason", "Unknown error");
            throw new IllegalStateException("Failed to start a new game: " + reason);
        }
    }

    @Override
    public int[] makeMove(Stone[][] board) {
        // Assuming the last move is made by this player
        int[] lastMove = getLastMove();
        String moveQuery = String.format("play/?pid=%s&x=%d&y=%d", gameID, lastMove[0], lastMove[1]);
        String response = javaClient.sendGet(moveQuery);

        return parseMove(response);
    }

    public int[] parseMove(String response) {
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.getBoolean("response")) {
            JSONObject moveObject = jsonObject.getJSONObject("move");
            int x = moveObject.getInt("x");
            int y = moveObject.getInt("y");
            return new int[] {x, y};
        } else {
            String reason = jsonObject.optString("reason", "Unknown error");
            System.err.println("Failed to make a move: " + reason); // Logging error
            return null; // Returning null to indicate an error
        }
    }

}
