package hexagonal_architecture.infrastructure;

import hexagonal_architecture.domain.User;

public interface UserService {
    User registerUser(String username);
}
