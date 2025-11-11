package adapters.memory;

import dao.ReviewDAO;
import model.Review;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MemReviewDAO implements ReviewDAO {
    private final Map<Integer, Review> store = new ConcurrentHashMap<>();

    @Override
    public boolean addReview(Review review) {
        if (store.containsKey(review.id)) return false;
        store.put(review.id, review);
        return true;
    }

    @Override
    public List<Review> getReviewsByWriter(int writerId) {
        return store.values().stream()
                .filter(r -> r.writer_id == writerId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Review> getReviewsByTour(int tourId) {
        return store.values().stream()
                .filter(r -> r.tour_id == tourId)
                .collect(Collectors.toList());
    }
}
