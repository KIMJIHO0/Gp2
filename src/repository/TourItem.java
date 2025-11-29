package repository;

import model.TourPackage2;
import model.UnavailableDateRule;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TourItem implements Manageable {
  int id;
  String name;
  String place;
  int price;
  int dayLong;

  int extraPersonFee;
  String[] transport;
  int[] headcountRange;
  UnavailableDateRule unavailables;

  BufferedImage[] shots = new BufferedImage[0];
  BufferedImage thumbnail = null;

  String contactNumber;
  List<String> schedule = new ArrayList<>();

  public BufferedImage readImg(String path) {
    try {
      return ImageIO.read(new File(path));
    } catch (Exception e) {
      System.out.println("이미지 로드 실패");
      return null;
    }
  }

  @Override
  public void read(Scanner scan) {
    id = scan.nextInt();
    name = scan.next();
    place = scan.next();
    price = scan.nextInt();
    dayLong = scan.nextInt();
    //인당 추가비용
    extraPersonFee = scan.nextInt();
    //교통수단
    int transCount = scan.nextInt();
    transport = new String[transCount];
    for(int i = 0; i<transCount; i++){
      transport[i] = scan.next();
    }
    //최소,최대 인원
    headcountRange = new int[2];
    headcountRange[0] = scan.nextInt();
    headcountRange[1] = scan.nextInt();
    //날짜규칙 NONE, WEEKDAY, SPECIFIC
    String ruleType = scan.next();
    switch(ruleType){
      case "WEEKDAY":
        List<DayOfWeek> days = List.of(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY
        );
        unavailables = new UnavailableDateRule(List.of(), days, List.of());
        break;
      case "SPECIFIC":
        // 지정 날짜 개수
        int cnt = scan.nextInt();
        List<LocalDate> dates = new ArrayList<>();
        for(int i = 0; i < cnt; i++){
          dates.add(LocalDate.parse(scan.next()));
        }
        unavailables = new UnavailableDateRule(List.of(), List.of(), dates);
        break;
      case "NONE":
      default:
        unavailables = new UnavailableDateRule(List.of(), List.of(), List.of());
        break;
    }
    //연락처
    contactNumber = scan.next();
    //썸네일
    String thumbPath = scan.next();
    if(!thumbPath.equals("NULL")){
      thumbnail = readImg(thumbPath);
    }
    //shots
    int shotCount = scan.nextInt();
    List<BufferedImage> tmpShots = new ArrayList<>();
    for(int i = 0; i<shotCount; i++){
      String sp =  scan.next();
      tmpShots.add(readImg(sp));
    }
    shots = tmpShots.toArray(new BufferedImage[0]);
    //스케줄
    schedule.clear();
    for(int i = 0; i< dayLong; i++){
      if(scan.hasNext()){
        schedule.add(scan.next());
      }
    }

  }

  public TourPackage2 toTour() {
    return new TourPackage2(id, name, place, price, dayLong,
        extraPersonFee, transport, headcountRange, unavailables,
        shots, thumbnail, contactNumber, schedule);
  }

  @Override
  public void print() {
    System.out.print(toTour());
  }

  @Override
  public boolean matches(String kwd) {
    return name.contains(kwd) || place.contains(kwd);
  }
}
