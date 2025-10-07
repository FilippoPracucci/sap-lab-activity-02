package hexagonal_architecture.application;

import hexagonal_architecture.domain.User;
import hexagonal_architecture.infrastructure.UserRegistry;
import hexagonal_architecture.infrastructure.UserService;

import java.util.Comparator;
import java.util.stream.StreamSupport;

public class UserServiceImpl implements UserService {

    private static final String DB_USERS_FILE = "users.json";

    private final UserRegistry registry;
    private int usersIdCount;

    public UserServiceImpl() {
        this.registry = new VertxFileUserRegistry(DB_USERS_FILE);
        this.usersIdCount = StreamSupport.stream(this.registry.findAllUsers().spliterator(), false)
                .map(u -> Integer.valueOf(u.id().split("-")[1]))
                .max(Comparator.comparingInt(id -> id))
                .orElse(- 1) + 1;
    }

    @Override
    public User registerUser(final String userName) {
        final String userId = "user-" + this.usersIdCount;
        final User user = new User(userId, userName);
        this.usersIdCount++;
        this.registry.saveUser(user);
        return user;
    }
}
