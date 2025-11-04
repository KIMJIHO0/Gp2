package service;

import java.util.List;
import common.Result;
import model.Review;

public interface ReviewService {
    Result<Review> write(String userId, String packageId, int score, String content);
    List<Review> listByPackage(String packageId);
    List<Review> listByUser(String userId);
}
