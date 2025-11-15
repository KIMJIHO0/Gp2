package repository;

import dao.ReviewDAO;
import model.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewRepository implements ReviewDAO {
  private final List<Review> reviews= new ArrayList<>();

  public ReviewRepository(String filename) {
    Manager manager = new Manager();
    manager.readAll(filename, new Factory() {
      @Override
      public Manageable create() {
        return new ReviewItem();
      }
    });

    for(Manageable m: manager.mList){
      ReviewItem item = (ReviewItem)m;
      reviews.add(item.toReview());
    }
  }

  @Override
  public boolean addReview(Review review) {
    reviews.add(review);
    return true;
  }

  @Override
  public List<Review> getReviewsByWriter(int writerId) {
    List<Review> list = new ArrayList<>();
    for(Review r: reviews){
      if(r.writer_id==writerId)
        list.add(r);
    }
    return list;
  }

  @Override
  public List<Review> getReviewsByTour(int tourId) {
    List<Review> list = new ArrayList<>();
    for(Review r: reviews){
      if(r.tour_id==tourId)
        list.add(r);
    }
    return list;
  }

  public void seed(List<Review> list){
    reviews.addAll(list);
  }
}
