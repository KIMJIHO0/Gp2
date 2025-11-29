package manager;

import dao.UserDAO;
import model.User;
import util.Passwords;

import java.util.Optional;

/**
 * 사용자 정보 관리 담당.
 * - 실제 저장 방식(DB/파일 등)은 UserDAO 구현체에 위임.
 * - UserManager는 가입/조회/비밀번호 확인 규칙만 가진다.
 */
public class UserManager {

    // 유저 데이터 접근 객체(인터페이스). 생성자에서 주입받는다.
    private final UserDAO userDAO;

    /**
     * UserDAO 구현체를 받아서 UserManager를 생성.
     * UI, 메인, 스프링 등에서 의존성 주입.
     */
    public UserManager(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * 주어진 id의 사용자 존재 여부 확인.
     * @return 있으면 true, 없으면 false
     */
    public boolean exists(int id) {
        return userDAO.getUser(id).isPresent();
    }

    /**
     * 사용자 정보 조회.
     * @return User 객체 또는 없을 경우 null
     */
    public User getUser(int id) {
        return userDAO.getUser(id).orElse(null);
    }

    /**
     * 회원 가입 처리.
     * 규칙:
     *  - 이미 같은 id가 있으면 실패(false)
     *  - 비밀번호가 null이거나 너무 짧으면 실패(false)
     *  - 그 외에는 비밀번호를 해시해서 UserDAO에 저장 요청
     * @return 성공 시 true, 실패 시 false
     */
    public boolean register(int id, String pw) {
        if (exists(id)) return false;          // 중복 ID 방지
        if (pw == null || pw.length() < 4)     // 최소 길이 예시 규칙
            return false;

        String hashed = Passwords.hash(pw);    // 평문 비밀번호 해시
        return userDAO.addUser(new User(id, hashed));
    }

    /**
     * 로그인 시 비밀번호 확인.
     * 절차:
     *  - id로 사용자 조회
     *  - 없으면 false
     *  - 있으면 입력 비밀번호를 해시하여 저장된 값과 비교
     * @return 비밀번호 일치 시 true, 아니면 false
     */
    public boolean checkPassword(int id, String password) {
        var opt = userDAO.getUser(id);
        if (opt.isEmpty()) return false;                  // 사용자 없음
        return Passwords.matches(password, opt.get().password);
    }
}
