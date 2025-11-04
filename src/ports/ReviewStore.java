package ports;

import java.util.List;
import model.Review;

public interface ReviewStore {
    Review save(Review review);
    List<Review> findByPackage(String packageId);
    List<Review> findByUser(String userId);
}
