import org.json.JSONObject;
// JavaClientPlayer extends PlayerInterface and represents a player that interacts with a game through a Java client.
public class JavaClientPlayer extends PlayerInterface {
    // The Java client used for communicating with a game server or API.
    private JavaClient javaClient;
    private String gameID;
    private String strategy;
    // Constructor. Initializes the player with a stone type and strategy, and starts a new game.
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

    // Initiates a new game using the client, setting up the game ID.
    private void initiateGame() {
        String response = javaClient.sendGet("new/?strategy=" + strategy);
        this.gameID = parseGameID(response);
        System.out.println("Game initiated with ID: " + gameID); // Debug statement
    }

    // Parses the game ID from the server's response.
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

    // Parses the server's response to determine the next move.
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
