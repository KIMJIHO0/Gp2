package repository;

import dao.ReservationDAO2;
import model.Reservation2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservationRepository2 implements ReservationDAO2 {
  private final List<Reservation2> reservations = new ArrayList<>();

  public ReservationRepository2(String filename) {
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
  public boolean addReservation(Reservation2 reservation) {
    reservations.add(reservation);
    return true;
  }

  @Override
  public boolean updateReservation(Reservation2 reservation) {
    for(int i=0; i<reservations.size(); i++){
      if(reservations.get(i).id == reservation.id){
        reservations.set(i, reservation);
        return true;
      }
    }
    return false;
  }

  @Override
  public Optional<Reservation2> getReservation(int id) {
    return reservations.stream()
        .filter(r->r.id==id)
        .findFirst();
  }

  @Override
  public List<Reservation2> getReservationsByUser(int userId) {
    List<Reservation2> list = new ArrayList<>();
    for(Reservation2 r: reservations){
      if(r.client_id==userId){
        list.add(r);
      }
    }
    return list;
  }

}
