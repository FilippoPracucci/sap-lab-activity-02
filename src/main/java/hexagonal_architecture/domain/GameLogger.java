package hexagonal_architecture.domain;

import java.util.logging.Logger;

public class GameLogger {

    private static final java.util.logging.Logger logger = Logger.getLogger("[TicTacToe]");

    public static java.util.logging.Logger getLogger() {
        return logger;
    }
}
