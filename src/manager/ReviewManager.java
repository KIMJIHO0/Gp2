package manager;

import dao.ReservationDAO;
import dao.ReviewDAO;
import dao.TourDAO;
import dao.UserDAO;
import model.Review;

import java.time.LocalDate;
import java.util.List;

/**
 * 리뷰 처리 담당 클래스.
 * - 리뷰 등록 시 검증 로직과 결과 코드를 제공.
 * - 실제 저장/조회는 ReviewDAO, ReservationDAO 등의 구현체에 위임.
 */
public class ReviewManager {

    /**
     * 리뷰 등록 결과 코드.
     * SUCCESS          : 등록 성공
     * INVALID_WRITER   : 존재하지 않는 작성자
     * INVALID_TOUR     : 존재하지 않는 투어 상품
     * ALREADY_REVIEWED : 이미 해당 투어에 리뷰를 작성한 사용자
     * DIDNT_TOUR       : 해당 투어 예약 이력이 없음
     * RATE_OUT_OF_RANGE: 평점 범위를 벗어남
     */
    public enum ResponseCode {
        SUCCESS,
        INVALID_WRITER,
        INVALID_TOUR,
        ALREADY_REVIEWED,
        DIDNT_TOUR,
        RATE_OUT_OF_RANGE
    }

    // 허용 평점 범위 [최소, 최대]
    public static final int[] RATE_RANGE = {1, 10};

    private final ReviewDAO reviewDAO;
    private final ReservationDAO reservationDAO;
    private final UserDAO userDAO;
    private final TourDAO tourDAO;

    /**
     * 필요한 DAO 구현체들을 주입받아 생성.
     */
    public ReviewManager(ReviewDAO reviewDAO,
                         ReservationDAO reservationDAO,
                         UserDAO userDAO,
                         TourDAO tourDAO) {
        this.reviewDAO = reviewDAO;
        this.reservationDAO = reservationDAO;
        this.userDAO = userDAO;
        this.tourDAO = tourDAO;
    }

    /**
     * 리뷰 등록.
     * 규칙:
     *  - 작성자(id) 존재해야 함.
     *  - 투어(id) 존재해야 함.
     *  - 평점은 RATE_RANGE 범위 내.
     *  - 같은 작성자가 같은 투어에 중복 리뷰 불가.
     *  - 작성자는 해당 투어에 대한 예약 이력이 있어야 함.
     * ID 생성과 실제 저장은 ReviewDAO 구현체 책임.
     *
     * @return 결과 코드(ResponseCode)
     */
    public ResponseCode review(int writer_id, int tour_id,
                               int rate, String content) {

        // 작성자 검증
        if (userDAO.getUser(writer_id).isEmpty()) {
            return ResponseCode.INVALID_WRITER;
        }

        // 투어 검증
        if (tourDAO.getTour(tour_id).isEmpty()) {
            return ResponseCode.INVALID_TOUR;
        }

        // 평점 범위 검증
        if (rate < RATE_RANGE[0] || rate > RATE_RANGE[1]) {
            return ResponseCode.RATE_OUT_OF_RANGE;
        }

        // 중복 리뷰 검증: 이미 같은 투어에 리뷰했는지
        boolean already = reviewDAO.getReviewsByWriter(writer_id).stream()
                .anyMatch(v -> v.tour_id == tour_id);
        if (already) {
            return ResponseCode.ALREADY_REVIEWED;
        }

        // 투어 이용 이력 검증: 해당 유저가 이 투어를 예약한 적이 있는지
        boolean didTour = reservationDAO.getReservationsByUser(writer_id).stream()
                .anyMatch(r -> r.tour_id == tour_id);
        if (!didTour) {
            return ResponseCode.DIDNT_TOUR;
        }

        // 임시 ID(0)로 Review 생성, 실제 ID는 DAO 구현에서 부여
        Review review = new Review(
                0,
                writer_id,
                tour_id,
                rate,
                content == null ? "" : content,
                LocalDate.now()
        );

        reviewDAO.addReview(review);
        return ResponseCode.SUCCESS;
    }

    /**
     * 특정 작성자의 모든 리뷰 목록 조회.
     */
    public List<Review> getReviewsByWriter(int writer_id) {
        return reviewDAO.getReviewsByWriter(writer_id);
    }

    /**
     * 특정 투어에 대한 모든 리뷰 목록 조회.
     */
    public List<Review> getReviewsByTour(int tour_id) {
        return reviewDAO.getReviewsByTour(tour_id);
    }

    /**
     * 특정 투어의 평균 평점 계산.
     * 리뷰가 없으면 0.0 반환.
     */
    public double getAverageRateOfTour(int tour_id) {
        var list = getReviewsByTour(tour_id);
        if (list.isEmpty()) return 0.0;
        int sum = 0;
        for (Review r : list) {
            sum += r.rate;
        }
        return (double) sum / list.size();
    }
}
