package repository;

import model.TourPackage;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TourItem implements Manageable {

  int id;
  String name;
  String place;
  int price;
  int dayLong;
  List<String> schedule = new ArrayList<>();

  @Override
  public void read(Scanner scan) {
    //1 제주힐링올레길투어 제주도 320000 3 올레길걷기 우도배편 성산일출봉
    String line = scan.nextLine().trim();
    if (line.isEmpty()) return;

    String[] parts = line.split("\\s+");

    int idx = 0;
    id = Integer.parseInt(parts[idx++]);
    name = parts[idx++];
    place = parts[idx++];
    price = Integer.parseInt(parts[idx++]);
    dayLong = Integer.parseInt(parts[idx++]);

    schedule.clear();
    for(int i = 0; i < dayLong; i++){
      if(idx < parts.length)
        schedule.add(parts[idx++]);
    }
  }

  public TourPackage toTour() {
    return new TourPackage(id, name, place, price, dayLong, schedule);
  }

  @Override
  public void print() {
    System.out.printf("%d %s %s %d %d %s\n",
        id, name, place, price, dayLong, schedule.toString());
  }

  @Override
  public boolean matches(String kwd) {
    return name.contains(kwd) || place.contains(kwd);
  }
}
