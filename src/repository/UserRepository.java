package repository;

import dao.UserDAO;
import model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository implements UserDAO {
  private final List<User> users = new ArrayList<>();

  public UserRepository(String filename) {
    Manager manager = new Manager();
    manager.readAll(filename, new Factory(){
      public Manageable create() {
        return new UserItem();
      }
    });

    for(Manageable m: manager.mList){
      UserItem item = (UserItem) m;
      users.add(item.toUser());
    }
  }

  @Override
  public boolean addUser(User user) {
    if(getUser(user.id).isPresent()) return false;
    users.add(user);
    return true;
  }

  @Override
  public Optional<User> getUser(int id) {
    return users.stream()
        .filter(u->u.id==id)
        .findFirst();
  }

  @Override
  public List<User> getAllUsers() {
    return new ArrayList<>(users);
  }

  @Override
  public boolean updateUser(User user) {
    for(int i = 0; i < users.size(); i++){
      if(users.get(i).id == user.id){
        users.set(i, user);
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean deleteUser(int id) {
    return users.removeIf(u->u.id==id);
  }
}
