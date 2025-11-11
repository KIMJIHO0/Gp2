// dao/TourDAO.java
package dao;

import model.TourPackage;
import java.util.List;
import java.util.Optional;

public interface TourDAO {
    List<TourPackage> getAllTours();
    Optional<TourPackage> getTour(int id);
}
