package model;

import java.time.LocalDate;

/** V2 예약: 기존 Reservation을 상속하고 headcount만 추가 */
public class Reservation2 extends Reservation {
    public final int headcount;   // 추가: 예약 인원수

    public Reservation2(int id, int client_id, int tour_id,
                        LocalDate start_date, String status,
                        LocalDate reservedDate, int headcount) {
        super(id, client_id, tour_id, start_date, status, reservedDate);
        this.headcount = headcount;
    }

    /** 상태만 변경하여 새 Reservation2 반환 */
    @Override
    public Reservation2 withStatus(String newStatus) {
        return new Reservation2(id, client_id, tour_id, start_date, newStatus, reservedDate, headcount);
    }

    @Override
    public String toString() {
        return "Reservation2{" +
                "id=" + id +
                ", client_id=" + client_id +
                ", tour_id=" + tour_id +
                ", start_date=" + start_date +
                ", status='" + status + '\'' +
                ", reservedDate=" + reservedDate +
                ", headcount=" + headcount +
                '}';
    }
}
