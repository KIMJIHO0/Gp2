
package ui_kit;

// 페이지 전환 요청용 객체
// AppEventBus의 pulish 로직에 논리적으로 의존
public class PageChangeEvent {
    private final String pageId;

    public PageChangeEvent(String pageId) {
        this.pageId = pageId;
    }

    public String getPageId() {
        return pageId;
    }
}
