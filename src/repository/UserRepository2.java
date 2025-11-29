package repository;

import dao.UserDAO2;
import model.User;
import model.User2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository2 implements UserDAO2 {
  private final List<User2> users = new ArrayList<>();

  public UserRepository2(String filename) {
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
  public boolean addUser(User2 user2) {
    if(getUser(user2.id).isPresent()) return false;
    users.add(user2);
    return true;
  }

  @Override
  public Optional<User2> getUser(int id) {
    return users.stream()
        .filter(u->u.id==id)
        .findFirst();
  }

  @Override
  public List<User2> getAllUsers() {
    return new ArrayList<>(users);
  }

  @Override
  public boolean updateUser(User2 user2) {
    for(int i = 0; i < users.size(); i++){
      if(users.get(i).id == user2.id){
        users.set(i, user2);
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean deleteUser(int id) {
    return users.removeIf(u->u.id==id);
  }

  // UserDAO 1버전 임시 구현(미사용)
  // @deprecated
  @Override
  public boolean addUser(User user) {
      return false;
  }
  @Override
  public boolean updateUser(User user) {
      return false;
  }
}
