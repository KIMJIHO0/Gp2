package ports;

import java.util.Optional;
import model.User;

/**
 * 데이터 관리 팀 구현 대상.
 * - 저장소 접근, 캐시, DB, 파일 등 내부 구현은 자유.
 * - 데이터 처리 계층은 이 인터페이스에만 의존.
 */
public interface UserStore {
    Optional<User> findById(String id);
    User save(User user);
}
