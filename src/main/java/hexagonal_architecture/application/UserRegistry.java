package hexagonal_architecture.application;

import annotations.OutBoundPort;
import hexagonal_architecture.domain.User;

import java.util.Optional;

@OutBoundPort
public interface UserRegistry {
    void saveUser(User user);
    Optional<User> findUserById(String userId);
    Iterable<User> findAllUsers();
}
