package model;

import java.time.Instant;
import model.enums.ReservationStatus;

/**
 * 예약 엔티티.
 * - id         : 예약 식별자
 * - userId     : 회원 ID
 * - packageId  : 패키지 ID
 * - createdAt  : 생성 시각
 * - status     : 상태(BOOKED/CANCELED)
 */
public class Reservation {
    public final String id;
    public final String userId;
    public final String packageId;
    public final String createdAt;
    public final ReservationStatus status;

    public Reservation(String id, String userId, String packageId,
                       String createdAt, ReservationStatus status) {
        this.id = id;
        this.userId = userId;
        this.packageId = packageId;
        this.createdAt = createdAt;
        this.status = status;
    }

    // 상태 갱신 시 새 인스턴스 반환(불변 모델 유지)
    public Reservation withStatus(ReservationStatus newStatus) {
        return new Reservation(id, userId, packageId, createdAt, newStatus);
    }
}
