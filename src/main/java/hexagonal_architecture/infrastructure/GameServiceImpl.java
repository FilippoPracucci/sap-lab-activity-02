package hexagonal_architecture.infrastructure;

import hexagonal_architecture.domain.Game;
import hexagonal_architecture.domain.GameSymbolType;
import hexagonal_architecture.domain.User;
import hexagonal_architecture.domain.exceptions.InvalidJoinException;
import hexagonal_architecture.domain.exceptions.InvalidMoveException;
import hexagonal_architecture.application.GameRegistry;
import hexagonal_architecture.application.GameService;
import hexagonal_architecture.application.UserRegistry;

public class GameServiceImpl implements GameService {

    private final GameRegistry gameRegistry;
    private final UserRegistry userRegistry;
    private int gamesIdCount = 0;

    public GameServiceImpl(final GameRegistry gameRegistry, final UserRegistry userRegistry) {
        this.gameRegistry = gameRegistry;
        this.userRegistry = userRegistry;
    }

    @Override
    public Game createNewGame() {
        this.gamesIdCount++;
        final String gameId = "game-" + gamesIdCount;
        final Game game = new Game(gameId);
        this.gameRegistry.saveGame(game);
        return game;
    }

    @Override
    public void joinGame(final String userId, final String gameId, final String symbol) throws InvalidJoinException {
        final User user = this.userRegistry.findUserById(userId).orElseThrow();
        final Game game = this.gameRegistry.findGameById(gameId).orElseThrow();
        final GameSymbolType gameSymbol = symbol.equals("cross") ? GameSymbolType.CROSS : GameSymbolType.CIRCLE;
        game.joinGame(user, gameSymbol);
    }

    @Override
    public void makeAMove(final String userId, final String gameId, final String symbol, final int x, final int y) throws InvalidMoveException {
        final User user = this.userRegistry.findUserById(userId).orElseThrow();
        final Game game = this.gameRegistry.findGameById(gameId).orElseThrow();
        final GameSymbolType gameSymbol = symbol.equals("cross") ? GameSymbolType.CROSS : GameSymbolType.CIRCLE;
        game.makeAmove(user, gameSymbol, x, y);
    }
}
