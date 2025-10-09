package hexagonal_architecture.application;

import hexagonal_architecture.domain.Game;

public interface GameService {
    Game createNewGame();
    void joinGame(String userId, String gameId, String symbol) throws Exception;
    void makeAMove(String userId, String gameId, String symbol, int x, int y) throws Exception;
}
