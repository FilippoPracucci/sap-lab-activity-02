package hexagonal_architecture.application;

import annotations.InBoundPort;
import hexagonal_architecture.domain.Game;

@InBoundPort
public interface GameService {
    Game createNewGame();
    void joinGame(String userId, String gameId, String symbol) throws Exception;
    void makeAMove(String userId, String gameId, String symbol, int x, int y) throws Exception;
}
