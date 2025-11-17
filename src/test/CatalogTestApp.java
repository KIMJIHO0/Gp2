package test;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

// 1. DAO 인터페이스 및 구현체 임포트
import dao.UserDAO;
import dao.TourDAO;
import dao.ReservationDAO;
import dao.ReviewDAO;
import repository.UserRepository;
import repository.TourRepository;
import repository.ReservationRepository;
import repository.ReviewRepository;

// 2. Manager 임포트
import manager.UserManager;
import manager.TourCatalog;
import manager.ReservationManager;
import manager.ReviewManager;
import manager.SessionManager; // (manager 패키지 내로 가정)

// 3. UI-Kit 및 페이지 임포트
import ui_kit.MainFrame;
import ui_kit.ServiceContext;
// import ui_kit.AppPage;
import pages.DefaultPage;
import pages.TourDetailPage; // [수정] SamplePage (TourDetailPage) 임포트

// CatalogPage
import pages.CatalogPage;

/**
 * 애플리케이션의 메인 엔트리 클래스입니다.
 * 모든 의존성을 조립(Wiring)하고 앱을 실행합니다.
 */
public class CatalogTestApp {

    public static void main(String[] args) {
        // Swing 앱은 항상 Event Dispatch Thread(EDT)에서 실행해야 합니다.
        SwingUtilities.invokeLater(() -> {
            
            // --- 1. DAO 계층 생성 ---
            UserDAO userDAO = new UserRepository("src/test_data/UserData.txt");
            TourDAO tourDAO = new TourRepository("src/data/TourPackageData.txt");
            ReservationDAO reservationDAO = new ReservationRepository("src/test_data/ReservationData.txt");
            ReviewDAO reviewDAO = new ReviewRepository("src/test_data/ReviewData.txt");

            // --- 2. Manager 계층 생성 (DAO 주입) ---
            // (제공해주신 생성자 시그니처 유지)
            UserManager userManager = new UserManager(userDAO);
            TourCatalog tourCatalog = new TourCatalog(tourDAO);
            ReservationManager reservationManager = new ReservationManager(reservationDAO,
                                                           userDAO, tourDAO);
            ReviewManager reviewManager = new ReviewManager(reviewDAO,
                                                 reservationDAO, userDAO, tourDAO);
            
            // --- 3. 전역 세션 매니저 생성 ---
            SessionManager sessionManager = new SessionManager();

            // --- 4. ServiceContext 생성 및 모든 서비스/매니저 등록 ---
            ServiceContext context = new ServiceContext();
            context.register(UserManager.class, userManager);
            context.register(TourCatalog.class, tourCatalog);
            context.register(ReservationManager.class, reservationManager);
            context.register(ReviewManager.class, reviewManager);
            context.register(SessionManager.class, sessionManager); // SessionManager 등록

            // --- 5. MainFrame 생성 (UI 셸) ---
            MainFrame mainFrame = new MainFrame();

            // --- 6. 페이지 생성 및 MainFrame에 등록 ---
            mainFrame.addPage(new DefaultPage(context)); 
            
            // [수정] SamplePage (TourDetailPage) 등록
            mainFrame.addPage(new TourDetailPage(context));

            // 본목적
            mainFrame.addPage(new CatalogPage(context));
            
            // (실제 운영 시 예시)
            // mainFrame.addPage(new LoginPage(context));
            // mainFrame.addPage(new HomePage(context));
            // mainFrame.addPage(new UserPanel(context));
            // mainFrame.addPage(new TourPanel(context));

            // --- 7. 애플리케이션 실행 ---
            mainFrame.setTitle("CatalogPage 테스트");
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setSize(1280, 800);
            mainFrame.setLocationRelativeTo(null); // 화면 중앙에 배치

            // [수정] "default" 대신 "tourDetail" 페이지를 샘플 ID(123L)와 함께 시작
            mainFrame.showPage("catalog", null); 
            mainFrame.setVisible(true);
        });
    }
}