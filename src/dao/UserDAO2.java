// dao/UserDAO2.java
package dao;

import model.User2;

import java.util.List;
import java.util.Optional;

public interface UserDAO2 {
    boolean addUser(User2 user);
    Optional<User2> getUser(int id);
    List<User2> getAllUsers();
    boolean updateUser(User2 user);
    boolean deleteUser(int id);
}
