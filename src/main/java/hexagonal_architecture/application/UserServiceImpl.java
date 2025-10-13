package hexagonal_architecture.application;

import hexagonal_architecture.domain.User;

import java.util.Comparator;
import java.util.stream.StreamSupport;

public class UserServiceImpl implements UserService {

    private final UserRegistry registry;
    private int usersIdCount;

    public UserServiceImpl(final UserRegistry registry) {
        this.registry = registry;
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
