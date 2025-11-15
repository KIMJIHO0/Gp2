package model;

/** 사용자 기본 계정 정보 */
public class User {
    public final int id;
    public final String password; // 해시 또는 평문(실습에 맞게 선택)

    public User(int id, String password) {
        this.id = id;
        this.password = password;
    }

  @Override
  public String toString() {
    return "User{" + "id=" + id + ", password=" + password + '}';
  }
}
