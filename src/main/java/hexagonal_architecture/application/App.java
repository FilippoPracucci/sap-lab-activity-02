package hexagonal_architecture.application;

import annotations.InBoundPort;

@InBoundPort
public interface App {
    String registerUser(String userName);
    String createNewGame();
    boolean joinGame(String userId, String gameId, String symbol);
    boolean makeAMove(String userId, String gameId, String symbol, int x, int y);
}
