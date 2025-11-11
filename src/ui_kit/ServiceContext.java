package ui_kit;

import java.util.HashMap;
import java.util.Map;

/**
 * 서비스(Manager) 인스턴스를 보관하는 의존성 주입(DI) 및 관리(유사 전역 싱글톤)용 컨테이너
 * Main.java에서 모든 Manager를 생성하여 이 컨텍스트에 등록합
 * 이 컨텍스트를 모든 AppPage에 전달하여 의존성 관리
 * 
 * i_really_hate_and_love_dependency_management
 * 이 간단한 거 하나 만들겠다고 2시간을 도식 그리는 데 썼다고? UI도 못 만들고?
 * 아ㅏ아ㅓㄹ낭밀아ㅓㅁㄴ이알
 * fxxx
 * **굳이 볼 필요 없다 말씀드리긴 할 테지만 혹시나 이 주석을 보신다면 관대한 이해 부탁드립니다**
 */
public class ServiceContext {
    private final Map<Class<?>, Object> services = new HashMap<>();

    /**
     * 컨텍스트 -> 서비스 계층(Manager들) 인스턴스 등록
     * 유사 전역 싱글톤 관리용. 구현을 어떻게 해주실지 모르니.
     * @param type 서비스의 클래스 타입 (ex_ UserManager.class)
     * @param instance 등록할 실제 인스턴스
     */
    public void register(Class<?> type, Object instance) {
        services.put(type, instance);
    }

    /**
     * 컨텍스트에서 등록된 서비스 인스턴스 bring
     * @param type 요청할 서비스의 클래스 타입
     * @return 등록된 서비스 인스턴스
     */
    @SuppressWarnings("unchecked") // unchecked 제네릭 타입 형변환 허용
    public <T> T get(Class<T> type) {
        T service = (T) services.get(type);
        if (service == null) {
            throw new RuntimeException("Service not found: " + type.getName());
        }
        return service;
    }

}
