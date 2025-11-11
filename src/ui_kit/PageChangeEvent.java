
package ui_kit;

// 페이지 전환 요청용 객체
// AppEventBus의 pulish 로직에 논리적으로 의존
public class PageChangeEvent {
    private final String pageId;
    private final Object contextData; // 전달할 데이터

    public PageChangeEvent(String pageId) {
        this.pageId = pageId;
        contextData = null;
    }

    public PageChangeEvent(String pageId, Object contextData) {
        this.pageId = pageId;
        this.contextData = contextData;
    }

    public String getPageId() {
        return pageId;
    }

    public Object getContextData() {
        return this.contextData; // 3. 데이터 Getter 추가
    }
}
