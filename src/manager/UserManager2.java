package manager;

import dao.UserDAO2;
import model.User2;
import util.Passwords;

import java.util.Objects;

/**
 * UserManager2
 * - 규칙만 확장: 이름/나이/성별을 받는 가입 메서드 추가
 * - 외부 인터페이스(UserManager와 독립)
 * - 내부 구현은 GenericUserManager 기반으로 변경 충격 최소화
 */
public class UserManager2 extends GenericUserManager<User2, UserDAO2> {

    public UserManager2(UserDAO2 userDAO2) {
        super(userDAO2);
    }

    /** 존재 여부 */
    @Override
    public boolean exists(int id) {
        return userDAO.getUser(id).isPresent();
    }

    /** 조회(없으면 null) */
    @Override
    public User2 getUser(int id) {
        return orNull(userDAO.getUser(id));
    }

    /**
     * 확장 가입: 이름/나이/성별 포함.
     * - 중복 ID 금지
     * - PW 최소 4자
     * - name 필수, age >= 0
     * - 해시 저장(Passwords.hash) 사용
     */
    public boolean register(int id, String rawPw, String name, int age, User2.Sex sex) {
        if (rawPw == null || rawPw.length() < 4) return false;
        if (name == null || name.isBlank()) return false;
        if (age < 0) return false;

        return registerInternal(id, () -> {
            String hash = Passwords.hash(rawPw);
            return userDAO.addUser(new User2(id, hash, name, age, sex));
        });
    }

    /**
     * 객체형 등록(과제 요구: register(? extends User) 대응).
     * - 넘겨온 User2의 password가 평문인지/해시인지 정책에 따라 정해질 수 있음
     * - 여기서는 안전하게 재해싱 옵션 제공(기본: true)
     */
    public boolean register(User2 user, boolean rehash) {
        if (user == null) return false;
        if (user.password == null || user.password.length() < 4) return false;
        if (user.name == null || user.name.isBlank()) return false;
        if (user.age < 0) return false;

        return registerInternal(user.id, () -> {
            String pwToSave = rehash ? Passwords.hash(user.password) : user.password;
            return userDAO.addUser(new User2(user.id, pwToSave, user.name, user.age, user.sex));
        });
    }

    /**
     * 로그인 검사
     * - 저장 포맷을 해시로 가정(Passwords.matches)
     * - 평문 저장 레거시가 있다면 equals 비교 오버로드를 별도로 추가
     */
    public boolean checkPassword(int id, String rawPw) {
        User2 u = getUser(id);
        if (u == null) return false;
        return Passwords.matches(rawPw, u.password);
    }

    /** 평문 저장 레거시 대비용(선택) */
    public boolean checkPasswordPlain(int id, String rawPw) {
        User2 u = getUser(id);
        if (u == null) return false;
        return Objects.equals(rawPw, u.password);
    }
}
