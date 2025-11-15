package adapters.memory;

import dao.UserDAO;
import model.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MemUserDAO implements UserDAO {
    private final Map<Integer, User> store = new ConcurrentHashMap<>();

    @Override
    public boolean addUser(User user) {
        if (store.containsKey(user.id)) return false;
        store.put(user.id, user);
        return true;
    }

    @Override
    public Optional<User> getUser(int id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(store.values());
    }

    @Override
    public boolean updateUser(User user) {
        if (!store.containsKey(user.id)) return false;
        store.put(user.id, user);
        return true;
    }

    @Override
    public boolean deleteUser(int id) {
        return store.remove(id) != null;
    }

     public void seed(List<User> users) {
       for(User user : users) {
         store.put(user.id, user);
       }
     }
}
