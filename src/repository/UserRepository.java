package repository;

import java.util.Optional;
import model.User;

public interface UserRepository {
    Optional<User> findById(String id);
    User save(User user);
}
