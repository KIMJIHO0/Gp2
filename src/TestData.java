import repository.*;
import model.*;

import java.util.List;

public class TestData {
  public static void main(String[] args) {

    System.out.println("=== Loading Data ===");

    // 파일명은 네가 사용하는 실제 파일명으로 변경
    UserRepository2 userRepo = new UserRepository2("src/test_data/UserData2.txt");
    TourRepository2 tourRepo = new TourRepository2("src/test_data/TourPackageData2.txt");
    ReservationRepository2 resRepo = new ReservationRepository2("src/test_data/ReservationData2.txt");

    System.out.println("=== Users Loaded ===");
    for (User2 u : userRepo.getAllUsers()) {
      System.out.println(u);
    }

    System.out.println("\n=== Tours Loaded ===");
    for (TourPackage2 t : tourRepo.findAll()) {
      System.out.println(t);
    }

    System.out.println("\n=== Test: Find One User ===");
    int testUserId = 1245;
    userRepo.getUser(testUserId).ifPresentOrElse(
        u -> System.out.println("User found: " + u),
        () -> System.out.println("User not found!")
    );

    System.out.println("\n=== Test: Find One Tour ===");
    int testTourId = 1;
    tourRepo.findById(testTourId).ifPresentOrElse(
        t -> System.out.println("Tour found: " + t),
        () -> System.out.println("Tour not found!")
    );

    System.out.println("\n=== Test: Reservations for user ===");
    List<Reservation2> rlist = resRepo.getReservationsByUser(testUserId);
    for (Reservation2 r : rlist) {
      System.out.println(r);
    }

    System.out.println("\n=== Test Completed ===");
  }
}
