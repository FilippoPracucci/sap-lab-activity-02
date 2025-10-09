package hexagonal_architecture.application;

import hexagonal_architecture.domain.User;

import java.util.Optional;

public interface UserRegistry {
    void saveUser(User user);
    Optional<User> findUserById(String userId);
    Iterable<User> findAllUsers();
    void initUsers();
}
