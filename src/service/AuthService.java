package service; // 서비스 계층 패키지

import common.Result;
import model.User;

/**
 * 인증 서비스 계약(인터페이스).
 * 구현체 예: AuthServiceImpl
 *
 * 책임:
 *  - 회원가입(signUp): 입력 검증, 비밀번호 해시, 중복 검사, 저장
 *  - 로그인(signIn): 사용자 조회, 비밀번호 검증
 * 반환:
 *  - Result<User>: 성공/실패를 한 타입으로 통일해 전달
 */
public interface AuthService {

    /**
     * 회원가입(use-case)
     * @param id  가입 ID
     * @param pw  평문 비밀번호(구현체에서 해시 처리)
     * @return    Result.ok(User) 또는 Result.fail(코드, 메시지)
     */
    Result<User> signUp(String id, String pw);

    /**
     * 로그인(use-case)
     * @param id  로그인 ID
     * @param pw  평문 비밀번호
     * @return    Result.ok(User) 또는 Result.fail(코드, 메시지)
     */
    Result<User> signIn(String id, String pw);
}
