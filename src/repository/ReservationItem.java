package repository;

import model.Reservation;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Scanner;

public class ReservationItem implements Manageable{

  int id;
  int clientId;
  int tourId;
  LocalDate startDate;
  String status;
  LocalDate reservedDate;

  @Override
  public void read(Scanner scan) {
    id=scan.nextInt();
    clientId=scan.nextInt();
    tourId=scan.nextInt();
    startDate = LocalDate.parse(scan.next());
    status = scan.next();
    reservedDate = LocalDate.parse(scan.next());
  }

  public Reservation toReservation(){
    return new Reservation(id, clientId, tourId, startDate, status, reservedDate);
  }

  @Override
  public void print() {
    System.out.printf("%d %d %d %s %s %s\n",
        id, clientId, tourId, startDate, status, reservedDate);
  }

  @Override
  public boolean matches(String kwd) {
    return status.contains(kwd);
  }
}
