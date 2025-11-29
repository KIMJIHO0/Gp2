package repository;

import model.User2;
import model.User2.Sex;
import java.util.Scanner;

public class UserItem implements Manageable{
  private int id;
  private String password;
  String name;
  int age;
  Sex sex;


  @Override
  public void read(Scanner scan) {
    this.id=scan.nextInt();
    this.password=scan.next();
    this.name=scan.next();
    this.age=scan.nextInt();
    this.sex=Sex.valueOf(scan.next());
  }

  @Override
  public void print() {
    System.out.print(toUser());
  }

  @Override
  public boolean matches(String kwd) {
    return Integer.toString(id).contains(kwd);
  }

  public int getId() {return id;}
  public User2 toUser() {
    return new User2(id, password, name, age, sex);
  }
}
