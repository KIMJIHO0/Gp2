// // 테스트코드(By. Gemini)


// package test;

// import javax.swing.SwingUtilities;
// import java.util.List;

// import ui_kit.*;

// // --- DAO/Manager 임시 구현 (실제로는 Impl 클래스) ---
// // 이 클래스들은 귀하의 다이어그램에 있는 실제 구현체(Impl)라고 가정합니다.
// class UserDAOImpl {} // implements UserDAO
// class TourDAOImpl {} // implements TourDAO
// class ReservationDAOImpl {} // implements ReservationDAO

// class UserManager {
//     public UserManager(UserDAOImpl dao) { /* ... */ }
//     public boolean login(String id, String pw) { return true; }
// }
// class TourCatalog {
//     public TourCatalog(TourDAOImpl dao) { /* ... */ }
//     public List<String> getAllTours() { return List.of("제주도", "강릉"); }
// }
// class ReservationManager {
//     public ReservationManager(ReservationDAOImpl dao) { /* ... */ }
// }

// // --- Panel Example ---
// class HomePanel extends AppPage {
//     public HomePanel(ServiceContext context) {
//         super(context);
//         add(new javax.swing.JLabel("홈 화면입니다."));
//         javax.swing.JButton userBtn = new javax.swing.JButton("유저 목록으로");
//         userBtn.addActionListener(e -> navigateTo("userList")); // "userList" ID로 이동 요청
//         add(userBtn);
//     }
//     @Override public String getPageId() { return "home"; }
// }

// class UserPanel extends AppPage {
//     private javax.swing.JList<String> list;
//     private javax.swing.DefaultListModel<String> model;
//     private UserManager userManager;
//     private TourCatalog tourCatalog;

//     public UserPanel(ServiceContext context) {
//         super(context);
//         // 1. 생성자에서 필요한 매니저를 주입받습니다.
//         userManager = context.get(UserManager.class);
//         tourCatalog = context.get(TourCatalog.class);
        
//         model = new javax.swing.DefaultListModel<>();
//         list = new javax.swing.JList<>(model);
//         add(new javax.swing.JScrollPane(list));
//     }
//     @Override public String getPageId() { return "userList"; }

//     @Override public void onPageShown() {
//         // 2. 페이지가 보일 때, runAsyncTask로 데이터를 로드합니다.
//         runAsyncTask(
//             () -> {
//                 // [백그라운드] - 매니저 호출 (DB/네트워크)
//                 return tourCatalog.getAllTours(); 
//             },
//             (tourNames) -> {
//                 // [EDT] - 성공 시 UI 업데이트
//                 model.clear();
//                 tourNames.forEach(model::addElement);
//             },
//             (error) -> {
//                 // [EDT] - 실패 시 에러 처리
//                 javax.swing.JOptionPane.showMessageDialog(this, "로드 실패: " + error.getMessage());
//             }
//         );
//     }
// }
// // (OrderPanel, ReviewPanel 등... 동일한 방식으로 개발)

// // --- 애플리케이션 조립 및 실행 ---

// public class UITest1 {
//     public static void main(String[] args) {
//         // [핵심] Swing 앱은 항상 EDT에서 시작해야 합니다.
//         SwingUtilities.invokeLater(() -> {
            
//             // --- 1. DAO 계층 생성 ---
//             UserDAOImpl userDAO = new UserDAOImpl();
//             TourDAOImpl tourDAO = new TourDAOImpl();
//             ReservationDAOImpl reservationDAO = new ReservationDAOImpl();
            
//             // --- 2. Manager(Service) 계층 생성 (DAO 주입) ---
//             UserManager userManager = new UserManager(userDAO);
//             TourCatalog tourCatalog = new TourCatalog(tourDAO);
//             ReservationManager reservationManager = new ReservationManager(reservationDAO);

//             // --- 3. ServiceContext 생성 및 Manager 등록 ---
//             ServiceContext context = new ServiceContext();
//             context.register(UserManager.class, userManager);
//             context.register(TourCatalog.class, tourCatalog);
//             context.register(ReservationManager.class, reservationManager);
            
//             // --- 4. MainFrame 생성 ---
//             MainFrame mainFrame = new MainFrame();
            
//             // --- 5. 피처 패널(AppPage) 생성 및 Frame에 등록 (Context 주입) ---
//             // 팀원들이 만든 패널들을 여기서 조립합니다.
//             mainFrame.addPage(new HomePanel(context));
//             mainFrame.addPage(new UserPanel(context));
//             // mainFrame.addPage(new OrderPanel(context));
//             // mainFrame.addPage(new ReviewPanel(context));
            
//             // --- 6. 앱 실행 ---
//             mainFrame.showPage("home"); // 시작 페이지 설정
//             mainFrame.setVisible(true);
//         });
//     }
// }