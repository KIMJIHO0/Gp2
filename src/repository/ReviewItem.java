package repository;

import model.Review;

import java.time.LocalDate;
import java.util.Scanner;

public class ReviewItem implements Manageable{
  int id;
  int writerId;
  int tourId;
  int rate;
  String content;
  LocalDate writtenDate;

  @Override
  public void read(Scanner scan) {
    id=scan.nextInt();
    writerId=scan.nextInt();
    tourId=scan.nextInt();
    rate=scan.nextInt();
    content=scan.next();
    writtenDate=LocalDate.parse(scan.next());
  }

  public Review toReview(){
    return new Review(id, writerId, tourId, rate, content, writtenDate);
  }

  @Override
  public void print() {
    System.out.printf("%d %d %d %d %s %s\n",
        id, writerId, tourId, rate, content, writtenDate);
  }

  @Override
  public boolean matches(String kwd) {
    return content.contains(kwd);
  }
}
