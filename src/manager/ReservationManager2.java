package manager;

import dao.ReservationDAO2;
import dao.TourDAO2;
import dao.UserDAO;
import model.Reservation2;
import model.TourPackage2;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 예약 처리 V2 (headcount 포함, TourPackage2 규칙 사용)
 * - 저장/조회는 ReservationDAO2, TourDAO2, UserDAO에 위임
 * - 날짜/인원 유효성은 TourCatalog2의 규칙과 동일 로직 적용
 */
public class ReservationManager2 {

    /** 예약 요청 결과 코드(V2) */
    public enum ResponseCode {
        SUCCESS,
        INVALID_CLIENT,
        INVALID_TOUR,
        OLD_DATE,            // 과거 날짜
        DATE_UNAVAILABLE,    // 패키지의 예약 불가 규칙에 해당
        INVALID_HEADCOUNT,   // 허용 인원 범위 위반
        ALREADY_RESERVED     // 동일 사용자·상품·날짜 중복
    }

    /** 취소 결과 코드(V1과 동일 규칙) */
    public enum CancelCode {
        SUCCESS,
        INVALID_RESERVATION,
        ALREADY_CANCELED,
        ALREADY_FULFILLED
    }

    private final ReservationDAO2 reservationDAO2;
    private final UserDAO userDAO;   // 사용자 존재 확인은 V1/V2 공통으로 충분
    private final TourDAO2 tourDAO2; // TourPackage2 조회

    public ReservationManager2(ReservationDAO2 reservationDAO2,
                               UserDAO userDAO,
                               TourDAO2 tourDAO2) {
        this.reservationDAO2 = reservationDAO2;
        this.userDAO = userDAO;
        this.tourDAO2 = tourDAO2;
    }

    /** 사용자별 예약 ID 목록 */
    public List<Integer> getListByUserId(int userId) {
        return reservationDAO2.getReservationsByUser(userId)
                .stream().map(r -> r.id).toList();
    }

    /** 단건 조회(없으면 null) */
    public Reservation2 getReservation(int id) {
        return reservationDAO2.getReservation(id).orElse(null);
    }

    /**
     * 예약 생성(V2)
     * 규칙:
     *  - 유효한 사용자/상품
     *  - 시작일은 오늘 이후
     *  - 패키지 예약 불가 규칙 미충족(가능일이어야 함)
     *  - headcount가 [min, max] 범위 내
     *  - 동일 사용자+상품+시작일 중복 금지
     * 성공 시 DAO가 실제 id를 부여하여 저장
     */
    public ResponseCode reserve(int client_id, int tour_id,
                                LocalDate start_date, int headcount) {
        // 1) 사용자/상품 존재
        if (userDAO.getUser(client_id).isEmpty()) return ResponseCode.INVALID_CLIENT;

        Optional<TourPackage2> optTour = tourDAO2.findById(tour_id);
        if (optTour.isEmpty()) return ResponseCode.INVALID_TOUR;
        TourPackage2 tour = optTour.get();

        // 2) 날짜 유효
        if (start_date.isBefore(LocalDate.now())) return ResponseCode.OLD_DATE;
        // 예약 불가 규칙
        if (tour.unavailables != null && tour.unavailables.isUnavailable(start_date))
            return ResponseCode.DATE_UNAVAILABLE;

        // 3) 인원수 범위
        int min = tour.headcount_range[0];
        int max = tour.headcount_range[1];
        if (headcount < min || headcount > max) return ResponseCode.INVALID_HEADCOUNT;

        // 4) 중복 예약 방지
        boolean already = reservationDAO2.getReservationsByUser(client_id).stream()
                .anyMatch(r -> r.tour_id == tour_id && r.start_date.equals(start_date));
        if (already) return ResponseCode.ALREADY_RESERVED;

        // 5) 저장 (id는 구현체가 부여)
        Reservation2 tmp = new Reservation2(
                0, client_id, tour_id, start_date,
                "pending", LocalDate.now(), headcount
        );
        reservationDAO2.addReservation(tmp);

        return ResponseCode.SUCCESS;
    }

    /** 예약 취소(V1과 동일 로직) */
    public CancelCode cancel(int reservation_id) {
        Optional<Reservation2> opt = reservationDAO2.getReservation(reservation_id);
        if (opt.isEmpty()) return CancelCode.INVALID_RESERVATION;

        Reservation2 r = opt.get();
        if ("canceled".equals(r.status)) return CancelCode.ALREADY_CANCELED;
        if ("fulfilled".equals(r.status)) return CancelCode.ALREADY_FULFILLED;

        Reservation2 updated = r.withStatus("canceled");
        reservationDAO2.updateReservation(updated);
        return CancelCode.SUCCESS;
    }
}
