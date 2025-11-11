package model;

import java.time.LocalDate;

/** 예약 정보 */
public class Reservation {
    public final int id;
    public final int client_id;        // 예약한 사용자 id
    public final int tour_id;          // 상품 id
    public final LocalDate start_date; // 여행 시작일
    public final String status;        // "pending" / "fulfilled" / "canceled"
    public final LocalDate reservedDate;

    public Reservation(int id, int client_id, int tour_id,
                       LocalDate start_date,
                       String status,
                       LocalDate reservedDate) {
        this.id = id;
        this.client_id = client_id;
        this.tour_id = tour_id;
        this.start_date = start_date;
        this.status = status;
        this.reservedDate = reservedDate;
    }

    public Reservation withStatus(String newStatus) {
        return new Reservation(id, client_id, tour_id,
                start_date, newStatus, reservedDate);
    }
}
