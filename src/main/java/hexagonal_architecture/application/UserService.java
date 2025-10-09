package hexagonal_architecture.application;

import hexagonal_architecture.domain.User;

public interface UserService {
    User registerUser(String username);
}
