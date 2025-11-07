package adapters.memory;

import dao.TourDAO;
import model.TourPackage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MemTourDAO implements TourDAO {
    private final Map<Integer, TourPackage> store = new ConcurrentHashMap<>();

    public void seed(TourPackage tour) {
        store.put(tour.id, tour);
    }

    @Override
    public List<TourPackage> getAllTours() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<TourPackage> getTour(int id) {
        return Optional.ofNullable(store.get(id));
    }
}
