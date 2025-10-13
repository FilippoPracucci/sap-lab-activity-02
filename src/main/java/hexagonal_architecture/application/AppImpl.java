package hexagonal_architecture.application;

public class AppImpl implements App {

    private final UserService userService;
    private final GameService gameService;

    public AppImpl(final UserService userService, final GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    @Override
    public String registerUser(final String userName) {
        return this.userService.registerUser(userName).id();
    }

    @Override
    public String createNewGame() {
        return this.gameService.createNewGame().getId();
    }

    @Override
    public boolean joinGame(final String userId, final String gameId, final String symbol) {
        try {
            this.gameService.joinGame(userId, gameId, symbol);
            return true;
        } catch (final Exception e) {
            return false;
        }
    }

    @Override
    public boolean makeAMove(final String userId, final String gameId, final String symbol, final int x, final int y) {
        try {
            this.gameService.makeAMove(userId, gameId, symbol, x, y);
            return true;
        } catch (final Exception e) {
            return false;
        }
    }
}
