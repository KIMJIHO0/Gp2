package service;

import java.util.List;
import common.Result;
import model.Reservation;

public interface ReservationService {
    Result<Reservation> book(String userId, String packageId);
    Result<Reservation> cancel(String reservationId, String userId);
    List<Reservation> listByUser(String userId);
}
