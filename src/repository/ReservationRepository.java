package repository;

import dao.ReservationDAO;
import model.Reservation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservationRepository implements ReservationDAO {
  private final List<Reservation> reservations = new ArrayList<>();

  public ReservationRepository(String filename) {
    Manager manager = new Manager();
    manager.readAll(filename, new Factory(){
      public Manageable create() {
        return new ReservationItem();
      }
    });

    for(Manageable m: manager.mList){
      ReservationItem item = (ReservationItem) m;
      reservations.add(item.toReservation());
    }
  }

  @Override
  public boolean addReservation(Reservation reservation) {
    reservations.add(reservation);
    return true;
  }

  @Override
  public boolean updateReservation(Reservation reservation) {
    for(int i=0; i<reservations.size(); i++){
      if(reservations.get(i).id == reservation.id){
        reservations.set(i, reservation);
        return true;
      }
    }
    return false;
  }

  @Override
  public Optional<Reservation> getReservation(int id) {
    return reservations.stream()
        .filter(r->r.id==id)
        .findFirst();
  }

  @Override
  public List<Reservation> getReservationsByUser(int userId) {
    List<Reservation> list = new ArrayList<>();
    for(Reservation r: reservations){
      if(r.client_id==userId){
        list.add(r);
      }
    }
    return list;
  }

  public void seed(List<Reservation> list) {
    reservations.addAll(list);
  }
}
