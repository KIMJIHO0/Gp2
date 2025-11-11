import javax.swing.JFrame;
import javax.swing.SwingUtilities;

// 1. DAO 인터페이스 및 구현체 임포트
import dao.UserDAO;
import dao.TourDAO;
import dao.ReservationDAO;
import dao.ReviewDAO;
import adapters.memory.MemUserDAO;
import adapters.memory.MemTourDAO;
import adapters.memory.MemReservationDAO;
import adapters.memory.MemReviewDAO;

// 2. Manager 임포트
import manager.UserManager;
import manager.TourCatalog;
import manager.ReservationManager;
import manager.ReviewManager;
// import SessionManager; // (루트 디렉토리)

// 3. UI-Kit 및 페이지 임포트
import ui_kit.MainFrame;
import ui_kit.ServiceContext;
import ui_kit.AppPage;
import pages.DefaultPage;
// (실제 운영 시) import pages.LoginPage;
// (실제 운영 시) import pages.HomePage;
// (실제 운영 시) import pages.TourPanel;

/**
 * 애플리케이션의 메인 엔트리 클래스입니다.
 * 모든 의존성을 조립(Wiring)하고 앱을 실행합니다.
 */
public class TestApp {

    public static void main(String[] args) {
        // Swing 앱은 항상 Event Dispatch Thread(EDT)에서 실행해야 합니다.
        SwingUtilities.invokeLater(() -> {
            
            // --- 1. DAO 계층 생성 (메모리 기반 임시 구현체) ---
            UserDAO userDAO = new MemUserDAO();
            TourDAO tourDAO = new MemTourDAO();
            ReservationDAO reservationDAO = new MemReservationDAO();
            ReviewDAO reviewDAO = new MemReviewDAO();

            // --- 2. Manager 계층 생성 (DAO 주입) ---
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
            // (팀원들이 pages/ 패키지에 만든 패널들을 여기서 조립합니다)
            
            // 조건 3: 생성한 기본 페이지 등록
            mainFrame.addPage(new DefaultPage(context)); 
            
            // (실제 운영 시 예시)
            // mainFrame.addPage(new LoginPage(context));
            // mainFrame.addPage(new HomePage(context));
            // mainFrame.addPage(new UserPanel(context));
            // mainFrame.addPage(new TourPanel(context));

            // --- 7. 애플리케이션 실행 ---
            mainFrame.setTitle("Tour Management System (TMS)");
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setSize(1280, 800);
            mainFrame.setLocationRelativeTo(null); // 화면 중앙에 배치

            // 조건 3: 만든 기본 페이지를 시작 페이지로 띄움
            mainFrame.showPage("default", null); 
            mainFrame.setVisible(true);
        });
    }
}