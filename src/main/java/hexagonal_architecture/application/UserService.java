package hexagonal_architecture.application;

import annotations.InBoundPort;
import hexagonal_architecture.domain.User;

@InBoundPort
public interface UserService {
    User registerUser(String username);
}
