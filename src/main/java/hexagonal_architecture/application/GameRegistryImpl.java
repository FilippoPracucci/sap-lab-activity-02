package hexagonal_architecture.application;

import hexagonal_architecture.App;
import hexagonal_architecture.domain.Game;
import hexagonal_architecture.infrastructure.GameRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GameRegistryImpl implements GameRegistry {

    private final Map<String, Game> games = new HashMap<>();

    @Override
    public void saveGame(final Game game) {
        this.games.put(game.getId(), game);
    }

    @Override
    public Optional<Game> findGameById(final String gameId) {
        return Optional.ofNullable(this.games.get(gameId));
    }
}
