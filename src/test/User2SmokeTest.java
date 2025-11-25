package test;

import manager.UserManager2;
import dao.UserDAO2;
import model.User2;

import java.util.*;
/** 메모리 전용 UserDAO2 스텁 */
class InMemoryUserDAO2 implements UserDAO2 {
    private final Map<Integer, User2> map = new HashMap<>();
    @Override public boolean addUser(User2 u){ if(map.containsKey(u.id)) return false; map.put(u.id,u); return true; }
    @Override public Optional<User2> getUser(int id){ return Optional.ofNullable(map.get(id)); }
    @Override public List<User2> getAllUsers(){ return new ArrayList<>(map.values()); }
    @Override public boolean updateUser(User2 u){ if(!map.containsKey(u.id)) return false; map.put(u.id,u); return true; }
    @Override public boolean deleteUser(int id){ return map.remove(id)!=null; }
}

public class User2SmokeTest {
    public static void main(String[] args) {
        UserDAO2 dao2 = new InMemoryUserDAO2();
        UserManager2 um2 = new UserManager2(dao2);

        boolean reg = um2.register(7001, "pass1234", "김지호", 22, User2.Sex.MALE);
        System.out.println("register: " + reg);

        boolean ok = um2.checkPassword(7001, "pass1234");
        System.out.println("signin: " + ok);

        System.out.println("user2: " + um2.getUser(7001));
    }
}
