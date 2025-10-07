package hexagonal_architecture.infrastructure;

import hexagonal_architecture.domain.Game;

public interface GameService {
    Game createNewGame();
    void joinGame(String userId, String gameId, String symbol) throws Exception;
    void makeAMove(String userId, String gameId, String symbol, int x, int y) throws Exception;
}
