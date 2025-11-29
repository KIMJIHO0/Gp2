// dao/UserDAO.java
package dao;

import model.User;
import java.util.List;
import java.util.Optional;

public interface UserDAO {
    boolean addUser(User user);
    Optional<? extends User> getUser(int id);
    List<? extends User> getAllUsers();
    boolean updateUser(User user);
    boolean deleteUser(int id);
}
