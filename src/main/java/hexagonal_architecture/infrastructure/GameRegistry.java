package hexagonal_architecture.infrastructure;

import hexagonal_architecture.domain.Game;

import java.util.Optional;

public interface GameRegistry {
    void saveGame(Game game);
    Optional<Game> findGameById(String gameId);
}
