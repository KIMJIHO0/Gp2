package test;

import dao.TourDAO2;
import manager.TourCatalog2;
import model.TourPackage2;
import model.UnavailableDateRule;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

/**
 * V1 파일(src/data/TourPackageData.txt)을 읽어
 * 메모리에서 TourPackage2로 어댑트한 뒤 TourCatalog2를 테스트한다.
 *
 * 외부 repository 패키지에 의존하지 않도록, 본 파일 안에 DAO 구현 포함.
 */
public class TourV2FromV1SmokeTest {

    /** V1 포맷을 읽어 TourPackage2로 변환하는 메모리 DAO */
    static final class V1AdapterDAO implements TourDAO2 {
        private final Path path;
        private final List<TourPackage2> cache;

        V1AdapterDAO(String filePath) {
            this.path = Path.of(filePath);
            this.cache = load();
        }

        @Override
        public Optional<TourPackage2> findById(int id) {
            return cache.stream().filter(t -> t.id == id).findFirst();
        }

        @Override
        public List<TourPackage2> findAll() {
            return cache;
        }

        private List<TourPackage2> load() {
            try {
                if (!Files.exists(path)) return List.of();
                List<String> lines = Files.readAllLines(path).stream()
                        .map(String::trim)
                        .filter(s -> !s.isBlank() && !s.startsWith("#"))
                        .collect(Collectors.toList());

                List<TourPackage2> list = new ArrayList<>();
                for (String line : lines) {
                    // V1 한 줄 포맷:
                    // id name place price day_long schedule1 schedule2 ...
                    String[] tok = line.split("\\s+");
                    if (tok.length < 5) continue;

                    int idx = 0;
                    int id = Integer.parseInt(tok[idx++]);
                    String name = tok[idx++];
                    String place = tok[idx++];
                    int price = Integer.parseInt(tok[idx++]);
                    int dayLong = Integer.parseInt(tok[idx++]);

                    List<String> schedule = new ArrayList<>();
                    while (idx < tok.length) schedule.add(tok[idx++]);

                    // ===== V2 필드 기본값 채우기 =====
                    int extraPersonFee = 30_000;                // 기본 인원 초과 1인당 3만원
                    String[] transport = defaultTransportFor(place);
                    int[] headcountRange = new int[]{2, 6};     // 기본 2~6명
                    UnavailableDateRule unavailables = defaultUnavailable(); // 예시 규칙(없어도 됨)
                    BufferedImage[] shots = null;               // 이미지 사용 안 함
                    BufferedImage thumbnail = null;
                    String contactNumber = "";                  // 미지정

                    list.add(new TourPackage2(
                            id, name, place, price, dayLong,
                            extraPersonFee, transport, headcountRange,
                            unavailables, shots, thumbnail,
                            contactNumber, schedule
                    ));
                }
                list.sort(Comparator.comparingInt(t -> t.id));
                return List.copyOf(list);
            } catch (Exception e) {
                throw new RuntimeException("Load failed: " + e.getMessage(), e);
            }
        }

        private static String[] defaultTransportFor(String place) {
            if (place.contains("제주")) return new String[]{"AIR", "BUS"};
            // 국내 주요 권역 간단 매핑
            Set<String> busPlaces = Set.of("강원권","속초","경주","여수","춘천","안동","담양","태안","보성","평창","포항","전주","남해");
            if (busPlaces.contains(place)) return new String[]{"BUS"};
            // 해외는 항공 기본
            return new String[]{"AIR", "BUS"};
        }

        private static UnavailableDateRule defaultUnavailable() {
            // 필요 없으면 null 반환해도 됨. 여기서는 예시로 '매주 일요일 불가'만 지정.
            return new UnavailableDateRule(
                    List.of(),                          // 기간 불가 없음
                    List.of(DayOfWeek.SUNDAY),          // 일요일 불가
                    List.of()                           // 특정일 불가 없음
            );
        }
    }

    public static void main(String[] args) {
        // 1) V1 파일을 그대로 사용해 V2 DAO 구성
        String v1Path = "src/data/TourPackageData.txt";
        TourDAO2 dao2 = new V1AdapterDAO(v1Path);

        // 2) V2 카탈로그로 조립
        TourCatalog2 catalog = new TourCatalog2(dao2);

        // 3) 기본 동작 확인
        System.out.println("count: " + catalog.getAllTours().size());
        System.out.println("exists(3): " + catalog.exists(3));

        var ids = catalog.getTourIds();
        System.out.println("ids[0..4]: " + ids.subList(0, Math.min(5, ids.size())));

        var t3 = catalog.getTour(3);
        System.out.println("tour#3: " + (t3 == null ? "null" : (t3.name + " / " + t3.place)));

        // 4) 검증/계산 테스트
        if (t3 != null) {
            System.out.println("headcount 2 ok: " + catalog.checkHeadcountValidate(t3, 2)); // true
            System.out.println("headcount 7 ok: " + catalog.checkHeadcountValidate(t3, 7)); // false

            System.out.println("total(2명): " + catalog.totalPrice(t3, 2)); // price
            System.out.println("total(4명): " + catalog.totalPrice(t3, 4)); // price + 2*extra

            System.out.println("date ok(+3d): " +
                    catalog.checkDateValidate(t3, LocalDate.now().plusDays(3))); // 일요일이면 false 가능
        }
    }
}
