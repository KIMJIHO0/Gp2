package ui_kit;

import javax.swing.SwingUtilities; // invoke용
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap; // 동시성 관리용
import java.util.function.Consumer; // 22

/**
 * 애플리케이션 전역 이벤트 버스(싱글톤 기반) / static은 살짝 불편
 * 모든 UI 관련 이벤트 리스너(구독자)가 항상 EDT에서 실행되도록 보장
 */
public class AppEventBus {
    private static final AppEventBus INSTANCE = new AppEventBus();
    private AppEventBus() {
        if(INSTANCE != null){
            System.err.println("AppEventBus는 싱글톤입니다. getInstance() 정적 메서드를 사용해주세요.");
            StackTraceElement[] stes = new Throwable().getStackTrace(); // 예의상 호출 스택 출력
            for( StackTraceElement ste : stes ) {
                System.err.println("STACKTRACE - className : " + ste.getClassName() + ", methodName : " 
                        + ste.getMethodName() + ", line : " + ste.getLineNumber());
            }
        }
    }
    public static AppEventBus getInstance() {
        return INSTANCE;
    }

    // Concurrent(동시적) 해시맵으로 스레드 안전성 보장.
    // EDT 관리 빡세네요
    private final Map<Class<?>, List<Consumer<Object>>> listeners = new ConcurrentHashMap<>();

    /**
     * 이벤트 발행/트리거
     * @param event 발행할 이벤트 객체
     */
    public void publish(Object event) {
        if (event == null) return;

        Class<?> eventType = event.getClass();
        
        // 해당 이벤트 및 부모/인터페이스 이벤트를 구독하는 리스너 모두에게 전달
        listeners.forEach((subscribedType, listenerList) -> {
            if (subscribedType.isAssignableFrom(eventType)) {
                listenerList.forEach(listener -> {
                    // 전부 EDT로 invoke
                    SwingUtilities.invokeLater(() -> listener.accept(event));
                });
            }
        });
    }

    /**
     * 특정 타입의 이벤트를 구독(등록)
     * @param eventType 구독할 이벤트의 Class (ex. PageChangeEvent.class)
     * @param listener 이벤트를 처리할 Consumer(람다식)
     */
    @SuppressWarnings("unchecked")
    public <T> void subscribe(Class<T> eventType, Consumer<T> listener) {
        // 리스트가 없으면 새로 생성(if absent)
        List<Consumer<Object>> listenerList = listeners.computeIfAbsent(
            eventType, 
            k -> new ArrayList<>()
        );
        
        // 동기화 블록에서 리스트에 추가
        synchronized(listenerList) {
            listenerList.add((Consumer<Object>) listener);
        }
    }

    /**
     * 구독 취소.
     * once 구현하려다 그 정도로 역동적인 기능은 안 만들 것 같아서.
     * 보통 AppPage.onPageHidden에서 불필요한 데이터 갱신이나 렌더링(어차피 안 보임) 없앨 때 호출 권장
     */
    public <T> void unsubscribe(Class<T> eventType, Consumer<T> listener) {
        List<Consumer<Object>> listenerList = listeners.get(eventType);
        if (listenerList != null) {
            synchronized(listenerList) {
                listenerList.remove(listener);
            }
        }
    }
}