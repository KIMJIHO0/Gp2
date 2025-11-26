package dao;

import model.Reservation2;

import java.util.List;
import java.util.Optional;

/** Reservation2 전용 DAO 계약 (저장 방식은 구현체에 위임) */
public interface ReservationDAO2 {
    boolean addReservation(Reservation2 r);          // 새 예약 저장 (id 채우기는 구현체 책임)
    boolean updateReservation(Reservation2 r);       // 상태 변경 등 갱신
    Optional<Reservation2> getReservation(int id);   // 단건 조회
    List<Reservation2> getReservationsByUser(int clientId); // 사용자별 목록
}
