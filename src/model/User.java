// 파일 위치를 나타내는 패키지 선언.
// 동일 패키지에 있는 클래스끼리는 import 없이 접근 가능.
package model;

/**
 * 불변(immutable) 사용자 모델.
 * - id: 사용자 식별자(로그인 아이디 또는 고유 키)
 * - pwHash: 비밀번호의 해시 문자열(평문 금지)
 * - createdAt: 생성 시각 문자열(예: "2025-11-03T12:34:56Z")
 *
 * 불변성을 위해 모든 필드를 final로 선언하고, setter를 두지 않는다.
 */
public class User {

    // 사용자 고유 아이디. DB의 PK 또는 로그인 아이디로 사용 가능.
    public final String id;

    // 비밀번호 해시. 단순 평문이 아닌 해시 결과만 저장해야 한다.
    // 예: bcrypt/argon2 + salt 적용 결과 문자열.
    public final String pwHash;

    // 생성 일시. 간단히 문자열로 보관
    public final String createdAt;

    /**
     * 생성자(Constructor)
     * 전달받은 값을 이 객체의 필드에 할당해 완성한다.
     * this.id = id 의 의미:
     *  - 왼쪽 this.id 는 "이 객체의 id 필드"
     *  - 오른쪽 id 는 "메서드 매개변수 id"
     */
    public User(String id, String pwHash, String createdAt) {
        this.id = id;
        this.pwHash = pwHash;
        this.createdAt = createdAt;
    }
}
