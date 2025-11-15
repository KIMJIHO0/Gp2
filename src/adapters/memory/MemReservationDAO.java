package adapters.memory;

import dao.ReservationDAO;
import model.Reservation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MemReservationDAO implements ReservationDAO {
    private final Map<Integer, Reservation> store = new ConcurrentHashMap<>();

    @Override
    public boolean addReservation(Reservation r) {
        if (store.containsKey(r.id)) return false;
        store.put(r.id, r);
        return true;
    }

    @Override
    public boolean updateReservation(Reservation r) {
        if (!store.containsKey(r.id)) return false;
        store.put(r.id, r);
        return true;
    }

    @Override
    public Optional<Reservation> getReservation(int id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Reservation> getReservationsByUser(int clientId) {
        return store.values().stream()
                .filter(r -> r.client_id == clientId)
                .collect(Collectors.toList());
    }

    public void seed(List<Reservation> reservations) {
       for(Reservation r : reservations) {
          store.put(r.id, r);
       }
     }
}
