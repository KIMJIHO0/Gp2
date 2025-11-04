package ports;

import java.util.List;
import java.util.Optional;
import model.TravelPackage;
import model.enums.Region;
import model.enums.Theme;

public interface PackageStore {
    Optional<TravelPackage> findById(String id);
    List<TravelPackage> findAll();
    List<TravelPackage> search(Region region, Theme theme, Integer maxPrice);
}
