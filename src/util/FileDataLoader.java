package util;

import model.Reservation;
import model.Review;
import model.TourPackage;
import model.User;

import javax.swing.event.ListDataEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileDataLoader {
  public static List<User> loadUsers(String path) throws IOException {
    List<User> list = new ArrayList<>();
    for(String line: Files.readAllLines(Paths.get(path))) {
      if(line.isEmpty()) continue;
      String[] arr = line.split(",");
      int id = Integer.parseInt(arr[0].trim());
      String pw = arr[1].trim();
      list.add(new  User(id, pw));
    }
    return list;
  }

  public static List<TourPackage> loadTours(String path) throws IOException {
    List<TourPackage> list = new ArrayList<>();
    for(String line: Files.readAllLines(Paths.get(path))) {
      if(line.isEmpty()) continue;
      //1, "제주 힐링 올레길 투어", "제주도", 320000, 3, ["올레길 걷기", "우도 배편", "성산일출봉"]
      String[] arr = line.split(",");
      int id = Integer.parseInt(arr[0].trim());
      String name = arr[1].trim();
      String place = arr[2].trim();
      int price = Integer.parseInt(arr[3].trim());
      int day = Integer.parseInt(arr[4].trim());
      List<String> schedule = Arrays.asList(arr[5].trim().split(","));
    }
    return list;
  }

  public static List<Reservation> loadReservations(String path) throws IOException {
    List<Reservation> list = new ArrayList<>();
    for(String line: Files.readAllLines(Paths.get(path))) {
      if(line.isEmpty()) continue;

      //1, 1245, 9, 2024-04-12, pending, 2024-03-02
      String[] arr = line.split(",");
      int id = Integer.parseInt(arr[0].trim());
      int clientId = Integer.parseInt(arr[1].trim());
      int tourId = Integer.parseInt(arr[2].trim());
      var startDate = LocalDate.parse(arr[3].trim());
      String status = arr[4].trim();
      var reservedDate = LocalDate.parse(arr[5].trim());

      list.add(new Reservation(id, clientId, tourId, startDate, status, reservedDate));
    }
    return list;
  }

  public static List<Review> loadReviews(String path) throws IOException {
    List<Review> list = new ArrayList<>();
    for(String line: Files.readAllLines(Paths.get(path))) {
      if(line.isEmpty()) continue;

      //1, 1245, 9, 8, "전체적으로 좋았음.", 2023-11-12
      String[] arr = line.split(",");
      int id = Integer.parseInt(arr[0].trim());
      int writerId = Integer.parseInt(arr[1].trim());
      int tourId = Integer.parseInt(arr[2].trim());
      int rate = Integer.parseInt(arr[3].trim());
      String content = arr[4].trim();
      var date = LocalDate.parse(arr[5].trim());

      list.add(new Review(id, writerId, tourId, rate, content, date));
    }
    return list;
  }
}
