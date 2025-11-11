// dao/UserDAO.java
package dao;

import model.User;
import java.util.List;
import java.util.Optional;

public interface UserDAO {
    boolean addUser(User user);
    Optional<User> getUser(int id);
    List<User> getAllUsers();
    boolean updateUser(User user);
    boolean deleteUser(int id);
}
