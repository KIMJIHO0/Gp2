package ui_kit;

/**
 * 리뷰 작성 완료 후 다른 페이지(특히 TourDetailPage)에게
 * "특정 투어에 리뷰가 추가되었다"는 사실을 전달하는 이벤트 클래스.
 *
 * AppEventBus.publish(new ReviewAddedEvent(tourId, writerId));
 */
public class ReviewAddedEvent {

    private final long tourId;    // 어떤 투어의 리뷰인지
    private final long writerId;  // 어떤 유저가 작성했는지

    public ReviewAddedEvent(long tourId, long writerId) {
        this.tourId = tourId;
        this.writerId = writerId;
    }

    public long getTourId() {
        return tourId;
    }

    public long getWriterId() {
        return writerId;
    }
}
