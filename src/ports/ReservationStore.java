package ports;

import java.util.List;
import java.util.Optional;
import model.Reservation;

public interface ReservationStore {
    Optional<Reservation> findById(String id);
    List<Reservation> findByUser(String userId);
    List<Reservation> findByPackage(String packageId);
    Reservation save(Reservation reservation); // 생성/갱신
}
