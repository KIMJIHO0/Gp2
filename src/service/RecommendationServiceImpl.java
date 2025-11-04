package service;

import java.util.*;
import java.util.stream.Collectors;

import model.Reservation;
import model.Review;
import model.TravelPackage;
import model.enums.Region;
import model.enums.Theme;
import ports.PackageStore;
import ports.ReservationStore;
import ports.ReviewStore;

public class RecommendationServiceImpl implements RecommendationService {
    private final ReservationStore resStore;
    private final ReviewStore revStore;
    private final PackageStore pkgStore;

    public RecommendationServiceImpl(ReservationStore r, ReviewStore v, PackageStore p){
        this.resStore = r; this.revStore = v; this.pkgStore = p;
    }

    @Override
    public List<TravelPackage> recommendForUser(String userId, int limit) {
        Map<Region,Integer> regionScore = new EnumMap<>(Region.class);
        Map<Theme,Integer>  themeScore  = new EnumMap<>(Theme.class);

        for (Reservation r : resStore.findByUser(userId)) {
            pkgStore.findById(r.packageId).ifPresent(p -> {
                regionScore.merge(p.region, 3, Integer::sum);
                themeScore.merge(p.theme,  3, Integer::sum);
            });
        }
        for (Review rv : revStore.findByUser(userId)) {
            pkgStore.findById(rv.packageId).ifPresent(p -> {
                int w = rv.score;
                regionScore.merge(p.region, w, Integer::sum);
                themeScore.merge(p.theme,  w, Integer::sum);
            });
        }

        Set<String> already = resStore.findByUser(userId).stream()
                .map(r->r.packageId).collect(Collectors.toSet());

        return pkgStore.findAll().stream()
                .filter(p -> !already.contains(p.id))
                .sorted((a,b) -> Double.compare(score(b, regionScore, themeScore),
                        score(a, regionScore, themeScore)))
                .limit(Math.max(1, limit))
                .toList();
    }

    private double score(TravelPackage p, Map<Region,Integer> rs, Map<Theme,Integer> ts) {
        return p.rating*2.0 + rs.getOrDefault(p.region,0)*0.7 + ts.getOrDefault(p.theme,0)*0.7 - (p.price/500.0);
    }
}
