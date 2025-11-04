package repository; // 저장소 계층 패키지

/**
 *자바의 Optional<T>는 “값이 있을 수도 없을 수도 있음”을 명시적으로 표현하는 컨테이너다.
 * null을 직접 다루지 않고 NPE를 줄이게 해준다.
 */
import java.util.Optional;
import model.User;

/**
 * 사용자 저장소 계약(인터페이스).
 * 구현체 예: InMemoryUserRepository, JdbcUserRepository, JpaUserRepository
 */
public interface UserRepository {

    /**
     * ID로 사용자 조회.
     * - 존재하면 Optional.of(user)
     * - 없으면 Optional.empty()
     */
    Optional<User> findById(String id);

    /**
     * 사용자 저장.
     * - 성공 시 저장된 User 반환
     * - 중복 키 등 정책 위반 시 구현체가 예외를 던질 수 있음
     */
    User save(User user);
}
