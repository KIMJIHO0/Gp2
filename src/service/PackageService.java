package service;

import java.util.List;
import java.util.Optional;
import model.TravelPackage;
import model.enums.Region;
import model.enums.Theme;

public interface PackageService {
    List<TravelPackage> listAll();
    List<TravelPackage> search(Region region, Theme theme, Integer maxPrice);
    Optional<TravelPackage> detail(String packageId);
}
