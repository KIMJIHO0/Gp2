package repository;

import model.Reservation2;

import java.time.LocalDate;
import java.util.Scanner;

public class ReservationItem implements Manageable{

  int id;
  int clientId;
  int tourId;
  LocalDate startDate;
  String status;
  LocalDate reservedDate;
  int headcount;

  @Override
  public void read(Scanner scan) {
    id=scan.nextInt();
    clientId=scan.nextInt();
    tourId=scan.nextInt();
    headcount = scan.nextInt();
    startDate = LocalDate.parse(scan.next());
    status = scan.next();
    reservedDate = LocalDate.parse(scan.next());
  }

  public Reservation2 toReservation(){
    return new Reservation2(id, clientId, tourId, startDate, status, reservedDate, headcount);
  }

  @Override
  public void print() {
    System.out.printf("%d %d %d %s %s %s %d\n",
        id, clientId, tourId, startDate, status, reservedDate, headcount);
  }

  @Override
  public boolean matches(String kwd) {
    return status.contains(kwd);
  }
}
