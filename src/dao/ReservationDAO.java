package dao;

import model.Reservation;
import java.util.List;
import java.util.Optional;


public interface ReservationDAO {


    boolean addReservation(Reservation reservation);
    boolean updateReservation(Reservation reservation);
    Optional<Reservation> getReservation(int id);
    List<Reservation> getReservationsByUser(int clientId);
}
