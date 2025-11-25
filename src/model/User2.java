package model;

/** 확장 사용자: 이름/나이/성별 포함 (User 상속) */
public class User2 extends User {

    public enum Sex { MALE, FEMALE }

    public final String name;
    public final int age;
    public final Sex sex;

    public User2(int id, String password, String name, int age, Sex sex) {
        super(id, password);
        this.name = name;
        this.age = age;
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "User2{id=" + id + ", name=" + name + ", age=" + age + ", sex=" + sex + "}";
    }
}
