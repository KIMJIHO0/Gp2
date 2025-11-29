package manager;

import dao.ReservationDAO;
import dao.TourDAO;
import dao.UserDAO;
import model.Reservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 예약 처리 담당 클래스.
 * - 실제 저장소 접근은 ReservationDAO / UserDAO / TourDAO 구현체에 위임.
 * - 예약/취소 시의 검증 규칙과 결과 코드만 가진다.
 */
public class ReservationManager {

    /**
     * 예약 요청 결과 코드.
     * SUCCESS          : 예약 성공
     * INVALID_CLIENT   : 존재하지 않는 사용자
     * INVALID_TOUR     : 존재하지 않는 상품
     * ALREADY_RESERVED : 이미 같은 조건의 예약 존재
     * OLD_DATE         : 과거 날짜로 요청
     */
    public enum ResponseCode {
        SUCCESS,
        INVALID_CLIENT,
        INVALID_TOUR,
        ALREADY_RESERVED,
        OLD_DATE
    }

    /**
     * 예약 취소 결과 코드.
     * SUCCESS              : 취소 성공
     * INVALID_RESERVATION  : 존재하지 않는 예약
     * ALREADY_CANCELED     : 이미 취소된 예약
     * ALREADY_FULFILLED    : 이미 이용 완료된 예약
     */
    public enum CancelCode {
        SUCCESS,
        INVALID_RESERVATION,
        ALREADY_CANCELED,
        ALREADY_FULFILLED
    }

    // 예약 데이터 접근
    private final ReservationDAO reservationDAO;
    // 사용자/상품 존재 여부 확인용
    private final UserDAO userDAO;
    private final TourDAO tourDAO;

    /**
     * ReservationDAO, UserDAO, TourDAO 구현체를 주입받아 생성.
     */
    public ReservationManager(ReservationDAO reservationDAO,
                              UserDAO userDAO,
                              TourDAO tourDAO) {
        this.reservationDAO = reservationDAO;
        this.userDAO = userDAO;
        this.tourDAO = tourDAO;
    }

    /**
     * 특정 사용자 예약 ID 목록 조회.
     * @param userId 사용자 ID
     * @return 예약 ID 리스트 (없으면 빈 리스트)
     */
    public List<Integer> getListByUserId(int userId) {
        return reservationDAO.getReservationsByUser(userId).stream()
                .map(r -> r.id)
                .toList();
    }

    /**
     * 예약 상세 조회.
     * @param id 예약 ID
     * @return Reservation 또는 없으면 null
     */
    public Reservation getReservation(int id) {
        return reservationDAO.getReservation(id).orElse(null);
    }

    /**
     * 예약 생성.
     * 규칙:
     *  - 유효한 사용자/상품이어야 함.
     *  - 시작일은 현재 날짜 이후.
     *  - 동일 사용자 + 동일 상품 + 동일 시작일 예약이 이미 있으면 실패.
     * ID 생성과 실제 저장은 ReservationDAO 구현체 책임.
     *
     * @return 결과 코드(ResponseCode)
     */
    public ResponseCode reserve(int client_id, int tour_id, LocalDate start_date) {
        // 사용자 검증
        if (userDAO.getUser(client_id).isEmpty()) {
            return ResponseCode.INVALID_CLIENT;
        }
        // 상품 검증
        if (tourDAO.getTour(tour_id).isEmpty()) {
            return ResponseCode.INVALID_TOUR;
        }
        // 날짜 검증
        if (start_date.isBefore(LocalDate.now())) {
            return ResponseCode.OLD_DATE;
        }

        // 중복 예약 검증
        boolean already = reservationDAO.getReservationsByUser(client_id).stream()
                .anyMatch(r -> r.tour_id == tour_id && r.start_date.equals(start_date));
        if (already) {
            return ResponseCode.ALREADY_RESERVED;
        }

        // ID는 0(더미)로 전달, DAO가 실제 ID를 채워 저장해야 한다.
        Reservation temp = new Reservation(
                0,
                client_id,
                tour_id,
                start_date,
                "pending",
                LocalDate.now()
        );
        reservationDAO.addReservation(temp);

        return ResponseCode.SUCCESS;
    }

    /**
     * 예약 취소.
     * 규칙:
     *  - 예약이 없으면 INVALID_RESERVATION
     *  - 이미 canceled면 ALREADY_CANCELED
     *  - 이미 fulfilled면 ALREADY_FULFILLED
     *  - 그 외에는 상태를 canceled로 변경
     *
     * @param reservation_id 취소할 예약 ID
     * @return 취소 결과 코드(CancelCode)
     */
    public CancelCode cancel(int reservation_id) {
        var opt = reservationDAO.getReservation(reservation_id);
        if (opt.isEmpty()) {
            return CancelCode.INVALID_RESERVATION;
        }

        Reservation r = opt.get();

        if ("canceled".equals(r.status)) {
            return CancelCode.ALREADY_CANCELED;
        }
        if ("fulfilled".equals(r.status)) {
            return CancelCode.ALREADY_FULFILLED;
        }

        // 상태만 변경한 새 객체 생성 후 DAO에 갱신 요청
        Reservation updated = r.withStatus("canceled");
        reservationDAO.updateReservation(updated);

        return CancelCode.SUCCESS;
    }
}
