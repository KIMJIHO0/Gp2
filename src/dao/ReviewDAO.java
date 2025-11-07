package dao;

import model.Review;
import java.util.List;


public interface ReviewDAO {

    boolean addReview(Review review);
    List<Review> getReviewsByWriter(int writerId);
    List<Review> getReviewsByTour(int tourId);
}
