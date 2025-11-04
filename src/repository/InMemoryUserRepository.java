package repository; // 저장소 계층 패키지

import java.util.*;
import model.User;

/**
 * 메모리 기반(UserId -> User) 저장소 구현.
 * - 앱 실행 중에만 데이터 유지. 프로세스 재시작 시 데이터 소멸.
 * - 단일 JVM 내 단순 테스트/프로토타입 용.
 */
public class InMemoryUserRepository implements UserRepository {

    // 사용자 ID를 키로 하는 해시맵. 같은 ID는 한 번만 저장 가능.
    private final Map<String, User> map = new HashMap<>();

    /**
     * ID로 사용자 조회.
     * - 없으면 Optional.empty() 반환.
     */
    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(map.get(id));
    }

    /**
     * 새 사용자 저장.
     * - 이미 동일 ID가 있으면 예외 발생.
     * - 동시성 충돌을 막기 위해 메서드 전체를 동기화.
     */
    @Override
    public synchronized User save(User u) {
        if (map.containsKey(u.id)) {
            throw new IllegalStateException("exists"); // 중복 ID 방지
        }
        map.put(u.id, u);
        return u;
    }
}
