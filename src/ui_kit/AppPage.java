
package ui_kit;

import javax.swing.JPanel;
import javax.swing.SwingWorker;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * 모든 페이지(패널)이 반드시! 상속받아야 하는 추상 클래스
 * 다른 분들은 꼭 이 클래스가 제공하는 헬퍼 메서드만 사용
 */
public abstract class AppPage extends JPanel {
    /**
     * Main에서 주입받은 ServiceContext
     * 사용자는 (this.)context.get(UserManager.class) 형태로 매니저에 접근한다
     */
    protected final ServiceContext context;

    /**
     * @param context 따라서 ServiceContext는 필수
     */
    public AppPage(ServiceContext context) {
        this.context = context;
        setBackground(UITheme.PANEL_BACKGROUND_COLOR);
        setBorder(UITheme.PANEL_BORDER);
    }

    /**
     * [필수 구현]
     * 고유 id 반환.
     * 실행할 때마다 값이 달라져서는 안됨.
     * 예: "home", "userList", "tourDetail"
     */
    public abstract String getPageId();

    /**
     * [선택적 오버라이드]
     * 페이지가 화면에 표시될 때 MainFrame에 의해 자동 호출
     * (ex. Manager로부터 최신 데이터 로딩 및 갱신을 runAsyncTask로 호출)
     */
    public void onPageShown() {
        // if you need, override it
    }

    /**
     * [선택적 오버라이드2]
     * 페이지가 다른 페이지로 전환되어 가려질 때 자동 호출
     * (ex. 이벤트 리스너 해제, 타이머 중지 등)
     */
    public void onPageHidden() {
        // if you need, override it
    }

    // --- Helper Methods (유틸리티) ---

    /**
     * [Helper]
     * 다른 페이지로 이동 요청.
     * 실패할 경우 onPageHidden은 미호출
     * @param pageId 이동할 페이지의 ID (AppPage의 getPageId() 값)
     */
    protected void navigateTo(String pageId) {
        AppEventBus.getInstance().publish(new PageChangeEvent(pageId));
    }

    /**
     * [Helper]
     * 전역 이벤트 트리거(발행)
     * @param event 발행할 이벤트 객체(이벤트 동작에 필요한 정보 전달)
     */
    protected void publishEvent(Object event) {
        AppEventBus.getInstance().publish(event);
    }

    /**
     * [Helper]
     * 전역 이벤트를 구독(Observe)
     * 등록된 리스너는 해당 타입(Class)의 이벤트가 발생했을 때 자동 호출됨
     */
    protected <T> void subscribeEvent(Class<T> eventType, Consumer<T> listener) {
        AppEventBus.getInstance().subscribe(eventType, listener);
    }

    /**
     * [Helper]
     * SwingWorker의 스레드 작업 간편화.
     * 백그라운드에 작업을 요청하고, 해당 작업의 성공/실패 여부에 따라 핸들링.
     * 
     * backgroundTask의 결과값은 onDone의 인자로,
     * 실패 시 예외 객체는 onError의 인자로 전달됨.
     * 
     * 아아 누가 이 고통을 알아주실까요
     * 신이시여 제가 이 완성본을 볼 수 있게 해주심에 감사드립니다. 모두를 사랑하게 하소서...
     * 
     * @param backgroundTask 백그라운드에서 실행될 작업 (DB/API 호출 등)
     * @param onDone EDT에서 실행될 성공 콜백 (UI 업데이트 등)
     * @param onError EDT에서 실행될 실패 콜백 (에러 핸들링)
     */
    // <T> void : 반환값 없는 제네릭 메서드
    protected <T> void runAsyncTask(Callable<T> backgroundTask, Consumer<T> onDone, Consumer<Exception> onError) {
        // 익명 자식 클래스 즉시 구현 및 생성
        new SwingWorker<T, Void>() {
            // 백그라운드 작업용 콜백
            @Override
            protected T doInBackground() throws Exception {
                return backgroundTask.call();
            }

            @Override
            protected void done() {
                try {
                    T res = get(); // 작업 결과
                    onDone.accept(res); // 성공 시 onDone으로 전달
                } catch (Exception e) {
                    // 실패
                    onError.accept(e);
                }
            }
        }.execute();
    }
}
