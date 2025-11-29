// dao/TourDAO.java
package dao;

import model.TourPackage;
import java.util.List;
import java.util.Optional;

public interface TourDAO {
    List<? extends TourPackage> getAllTours();
    Optional<? extends TourPackage> getTour(int id);
}
