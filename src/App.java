import javax.swing.JFrame;
import javax.swing.SwingUtilities;

// DAO 인터페이스 및 구현체 임포트
import dao.RecommendationDAO;
import dao.UserDAO2;
import dao.TourDAO2;
import dao.ReservationDAO2;
import dao.ReviewDAO;
import repository.UserRepository2;
import repository.TourRepository2;
import repository.ReservationRepository2;
import repository.ReviewRepository;
import repository.RecommendationRepository;

// Manager 임포트
import manager.UserManager2;
import manager.SessionManager;
import manager.TourCatalog2;
import manager.ReservationManager2;
import manager.RecommendationManager;
import manager.ReviewManager;

// UI-Kit 및 페이지 임포트
import ui_kit.MainFrame;
import ui_kit.ServiceContext;
import pages.*;


/**
 * 애플리케이션의 메인 엔트리 포인트 (베타 1).
 * 의존성을 설정하고 UI를 실행합니다.
 */
public class App {

    public static void main(String[] args) {
        // Swing 앱은 항상 Event Dispatch Thread(EDT)에서 실행해야 합니다.
        SwingUtilities.invokeLater(() -> {

            // --- 1. DAO 계층 생성 ---
            // repository 폴더의 DAO 구현체 사용
            // 상품정보 외에 리뷰 등은 임시로 추가
            UserDAO2 userDAO = new UserRepository2("src/data/UserData2.txt");
            TourDAO2 tourDAO = new TourRepository2("src/data/TourPackageData2.txt");
            ReservationDAO2 reservationDAO = new ReservationRepository2("src/data/ReservationData2.txt");
            ReviewDAO reviewDAO = new ReviewRepository("src/data/ReviewData.txt");
            RecommendationDAO recommendDAO = new RecommendationRepository(userDAO, reservationDAO, tourDAO, "src/data/DemographicData.txt");

            // --- 2. Manager 계층 생성 (DAO 주입) ---
            UserManager2 userManager = new UserManager2(userDAO);
            TourCatalog2 tourCatalog = new TourCatalog2(tourDAO);
            ReservationManager2 reservationManager = new ReservationManager2(reservationDAO, userDAO, tourDAO);
            ReviewManager reviewManager = new ReviewManager(reviewDAO, reservationDAO, userDAO, tourDAO);
            RecommendationManager recommendationManager = new RecommendationManager(recommendDAO);
            SessionManager sessionManager = new SessionManager();

            // --- 3. ServiceContext 생성 및 모든 서비스/매니저 등록 ---
            ServiceContext context = new ServiceContext();
            context.register(UserManager2.class, userManager);
            context.register(TourCatalog2.class, tourCatalog);
            context.register(ReservationManager2.class, reservationManager);
            context.register(ReviewManager.class, reviewManager);
            context.register(RecommendationManager.class, recommendationManager);
            context.register(SessionManager.class, sessionManager);

            // --- 4. 임시 세션 추가 (0000 계정) ---
            // UserData.txt의 '0000 admin' 사용
            // sessionManager.login(1245L);

            // --- 5. MainFrame 생성 (UI 셸) ---
            MainFrame mainFrame = new MainFrame();

            // --- 6. 페이지 생성 및 MainFrame에 등록 ---
            mainFrame.addPage(new DefaultPage(context));
            mainFrame.addPage(new CatalogPage(context));       // id: catalog
            mainFrame.addPage(new PackageDetailPage(context)); // id: tourDetail
            mainFrame.addPage(new ReviewWritePage(context));   // id: reviewWrite
            mainFrame.addPage(new LoginPage(context));
            mainFrame.addPage(new SignupPage(context));

            // --- 7. 애플리케이션 실행 ---
            mainFrame.setTitle("여행 예약 시스템 (Beta 1)");
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setSize(1280, 800);
            mainFrame.setLocationRelativeTo(null); // 화면 중앙에 배치

            // --- 8. 맨 처음에 CatalogPage를 띄움 ---
            mainFrame.showPage(LoginPage.PAGE_ID, null); // 1: 패키지 목록
            mainFrame.setVisible(true);
        });
    }
}
