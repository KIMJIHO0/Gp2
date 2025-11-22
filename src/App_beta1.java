import javax.swing.JFrame;
import javax.swing.SwingUtilities;

// DAO 인터페이스 및 구현체 임포트
import dao.RecommendationDAO;
import dao.UserDAO;
import dao.TourDAO;
import dao.ReservationDAO;
import dao.ReviewDAO;
import repository.UserRepository;
import repository.TourRepository;
import repository.ReservationRepository;
import repository.ReviewRepository;

// Manager 임포트
import manager.RecommendationManager;
import manager.UserManager;
import manager.TourCatalog;
import manager.ReservationManager;
import manager.ReviewManager;
import manager.SessionManager;

// UI-Kit 및 페이지 임포트
import ui_kit.MainFrame;
import ui_kit.ServiceContext;
import pages.DefaultPage;
import pages.PackageDetailPage;
import pages.ReviewWritePage;
import pages.CatalogPage;
import pages.LoginPage;
import pages.MainPage;

import java.util.ArrayList;
import java.util.List;
import model.Recommendation;

/**
 * 애플리케이션의 메인 엔트리 포인트 (베타 1).
 * 의존성을 설정하고 UI를 실행합니다.
 */
public class App_beta1 {

    public static void main(String[] args) {
        // Swing 앱은 항상 Event Dispatch Thread(EDT)에서 실행해야 합니다.
        SwingUtilities.invokeLater(() -> {

            // --- 1. DAO 계층 생성 ---
            // repository 폴더의 DAO 구현체 사용
            // 상품정보 외에 리뷰 등은 임시로 추가
            UserDAO userDAO = new UserRepository("src/test_data/UserData.txt");
            TourDAO tourDAO = new TourRepository("src/data/TourPackageData.txt");
            ReservationDAO reservationDAO = new ReservationRepository("src/test_data/ReservationData.txt");
            ReviewDAO reviewDAO = new ReviewRepository("src/test_data/ReviewData.txt");
            
            // RecommendationDAO는 구현체가 없으므로, 임시 익명 클래스로 생성
            RecommendationDAO recommendationDAO = new RecommendationDAO() {
                @Override
                public List<Recommendation> getRecommendations() {
                    return new ArrayList<>();
                }
            };

            // --- 2. Manager 계층 생성 (DAO 주입) ---
            UserManager userManager = new UserManager(userDAO);
            TourCatalog tourCatalog = new TourCatalog(tourDAO);
            ReservationManager reservationManager = new ReservationManager(reservationDAO, userDAO, tourDAO);
            ReviewManager reviewManager = new ReviewManager(reviewDAO, reservationDAO, userDAO, tourDAO);
            RecommendationManager recommendationManager = new RecommendationManager(recommendationDAO, tourCatalog, reservationManager);
            SessionManager sessionManager = new SessionManager();

            // --- 3. ServiceContext 생성 및 모든 서비스/매니저 등록 ---
            ServiceContext context = new ServiceContext();
            context.register(UserManager.class, userManager);
            context.register(TourCatalog.class, tourCatalog);
            context.register(ReservationManager.class, reservationManager);
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
            mainFrame.addPage(new MainPage(context));

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
