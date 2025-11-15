package repository;

import model.User;

import java.util.Scanner;

public class UserItem implements Manageable{
  private int id;
  private String password;


  @Override
  public void read(Scanner scan) {
    //1245 pw7592
    this.id=scan.nextInt();
    this.password=scan.next();
  }

  @Override
  public void print() {
    System.out.printf("UserItem{id=%d, password=%s}\n", id, password);
  }

  @Override
  public boolean matches(String kwd) {
    return Integer.toString(id).contains(kwd);
  }

  public int getId() {return id;}
  public String getPassword() {return password;}

  public User toUser() {
    return new User(id, password);
  }
}
