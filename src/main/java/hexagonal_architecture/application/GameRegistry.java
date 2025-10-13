package hexagonal_architecture.application;

import annotations.OutBoundPort;
import hexagonal_architecture.domain.Game;

import java.util.Optional;

@OutBoundPort
public interface GameRegistry {
    void saveGame(Game game);
    Optional<Game> findGameById(String gameId);
}
