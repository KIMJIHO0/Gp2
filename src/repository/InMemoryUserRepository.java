package repository;

import java.util.*;
import model.User;

public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> map = new HashMap<>();
    @Override public Optional<User> findById(String id){ return Optional.ofNullable(map.get(id)); }
    @Override public synchronized User save(User u){
        if (map.containsKey(u.id)) throw new IllegalStateException("exists");
        map.put(u.id, u);
        return u;
    }
}
