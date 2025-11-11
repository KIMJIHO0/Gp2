package pages;

import ui_kit.*;
import ui_kit.AppLabel.LabelType;
import manager.TourCatalog;
import manager.ReservationManager;
import manager.SessionManager;
// import model.TourPackage; // (실제 모델 클래스)
// import events.UserLoggedOutEvent; // (실제 이벤트 클래스)
// import events.ReviewAddedEvent; // (실제 이벤트 클래스)

import javax.swing.*;
import java.awt.*;

/**
 * AppPage의 모든 기능과 ui-kit 레이아웃을 활용하는 종합 샘플 페이지입니다.
 * "투어 상세 정보" 페이지를 가정합니다.
 */
public class TourDetailPage extends AppPage {

    // 1. 매니저 (생성자에서 주입)
    private TourCatalog tourCatalog;
    private ReservationManager reservationManager;
    private SessionManager sessionManager;

    // 2. 현재 페이지의 상태
    private Long currentTourId;
    // private TourPackage currentTour; // (실제 로드될 데이터)

    // 3. UI 컴포넌트 (ui-kit 사용)
    private AppLabel titleLabel;
    private AppProgressBar loadingBar;
    private AppTextArea descriptionArea;
    private AppTextField reservationDateField;
    private AppButton reserveButton;
    private AppButton addReviewButton;
    private AppButton backToListButton;

    /**
     * AppPage의 계약: ServiceContext를 받아 부모에게 넘기고, 필요한 매니저를 가져옵니다.
     */
    public TourDetailPage(ServiceContext context) {
        super(context);
        
        // 1. ServiceContext에서 필요한 매니저 인스턴스를 가져옵니다.
        this.tourCatalog = context.get(TourCatalog.class);
        this.reservationManager = context.get(ReservationManager.class);
        this.sessionManager = context.get(SessionManager.class);
        
        // 2. UI 레이아웃 구성
        initUI();
        
        // 3. 버튼 등에 이벤트 리스너 연결
        initListeners();
        
        // 4. 전역 이벤트 구독
        subscribeToGlobalEvents();
    }

    /**
     * AppTitledPanel, AppPanel을 사용한 레이아웃 구성
     */
    private void initUI() {
        this.setLayout(new BorderLayout(10, 10)); // 페이지 전체 레이아웃

        // --- 상단: 제목 ---
        titleLabel = new AppLabel("투어 정보를 로드 중입니다...", LabelType.TITLE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(titleLabel, BorderLayout.NORTH);

        // --- 중앙: 투어 상세 정보 ---
        AppTitledPanel infoPanel = new AppTitledPanel("상세 정보");
        infoPanel.setLayout(new BorderLayout(5, 5));
        
        descriptionArea = new AppTextArea(); // AppTextArea는 JScrollPane을 내장
        descriptionArea.setEditable(false);
        infoPanel.add(descriptionArea, BorderLayout.CENTER);
        
        loadingBar = new AppProgressBar(); // 로딩 바
        infoPanel.add(loadingBar, BorderLayout.SOUTH);

        this.add(infoPanel, BorderLayout.CENTER);

        // --- 하단: 예약 및 기타 액션 ---
        AppTitledPanel actionPanel = new AppTitledPanel("예약 및 기타");
        actionPanel.setLayout(new BorderLayout(5, 5));

        // 예약 폼 (AppPanel을 div처럼 사용)
        AppPanel reservationForm = new AppPanel(new FlowLayout(FlowLayout.LEFT));
        reservationForm.add(new AppLabel("희망 날짜:"));
        reservationDateField = new AppTextField( 12);
        reserveButton = new AppButton("예약하기");
        reservationForm.add(reservationDateField);
        reservationForm.add(reserveButton);
        actionPanel.add(reservationForm, BorderLayout.NORTH);

        // 기타 버튼 (AppPanel을 div처럼 사용)
        AppPanel otherButtons = new AppPanel(new FlowLayout(FlowLayout.RIGHT));
        addReviewButton = new AppButton("리뷰 작성");
        backToListButton = new AppButton("목록으로");
        otherButtons.add(addReviewButton);
        otherButtons.add(backToListButton);
        actionPanel.add(otherButtons, BorderLayout.SOUTH);

        this.add(actionPanel, BorderLayout.SOUTH);
    }

    /**
     * UI 컴포넌트의 이벤트 리스너를 설정합니다.
     */
    private void initListeners() {
        // "목록으로" 버튼 클릭 -> "tourList" 페이지로 데이터 없이 이동
        backToListButton.addActionListener(e -> {
            navigateTo("tourList"); // 1. navigateTo (데이터 없음)
        });

        // "예약하기" 버튼 클릭 -> 예약 실행
        reserveButton.addActionListener(e -> {
            handleReservation();
        });
        
        // "리뷰 작성" 버튼 클릭 -> 전역 이벤트 발행
        addReviewButton.addActionListener(e -> {
            // 2. publishEvent (현재 투어 ID를 이벤트 객체에 담아 발행)
            // (ReviewAddedEvent 클래스가 events/ 패키지에 정의되어 있다고 가정)
            // publishEvent(new ReviewAddedEvent(this.currentTourId));
            
            JOptionPane.showMessageDialog(this, "리뷰 작성 이벤트가 발행되었습니다. (구독한 페이지가 반응합니다)");
        });
    }
    
    /**
     * 전역 이벤트를 구독합니다.
     */
    private void subscribeToGlobalEvents() {
        // 3. subscribeEvent
        // 만약 다른 곳(예: 메인 메뉴)에서 로그아웃 이벤트가 발행되면
        // (UserLoggedOutEvent 클래스가 events/ 패키지에 정의되어 있다고 가정)
        /*
        subscribeEvent(UserLoggedOutEvent.class, event -> {
            // 이 페이지의 상태를 초기화하고
            this.currentTourId = null;
            this.titleLabel.setText("로그아웃되었습니다.");
            this.descriptionArea.setText("");
            
            // 로그인 페이지로 이동
            navigateTo("login");
        });
        */
    }

    // --- AppPage의 핵심 기능 사용 ---

    @Override
    public String getPageId() {
        return "tourDetail"; // 이 페이지의 고유 ID
    }

    /**
     * 4. onPageShown: 페이지가 표시될 때 호출되며, contextData를 받습니다.
     */
    @Override
    public void onPageShown(Object contextData) {
        // "tourList" 페이지에서 넘겨준 투어 ID를 받습니다.
        if (contextData instanceof Long) {
            this.currentTourId = (Long) contextData;
            
            // 5. runAsyncTask: 받은 ID로 상세 정보를 비동기 로드합니다.
            loadTourDetails(this.currentTourId);
        } else {
            // ID가 없거나 잘못된 경우
            titleLabel.setText("오류: 잘못된 접근입니다.");
            descriptionArea.setText("목록에서 투어를 선택해주세요.");
            loadingBar.setVisible(false);
        }
    }

    /**
     * (Helper) tourId를 사용해 상세 정보를 로드합니다.
     */
    private void loadTourDetails(Long tourId) {
        loadingBar.setIndeterminate(true); // 로딩 바 시작
        loadingBar.setVisible(true);

        runAsyncTask(
            () -> {
                // [백그라운드 스레드]
                // tourCatalog.getTourById(tourId)가 TourPackage 객체를 반환한다고 가정
                Thread.sleep(1000); // (네트워크 지연 흉내)
                return "임시 투어 패키지 (ID: " + tourId + ")"; // tourCatalog.getTourById(tourId);
            },
            (result) -> {
                // [EDT - 성공]
                // this.currentTour = (TourPackage) result; // 실제 데이터 저장
                // titleLabel.setText(currentTour.getName());
                // descriptionArea.setText(currentTour.getDetails());
                
                titleLabel.setText("투어 상세 (ID: " + tourId + ")");
                descriptionArea.setText(result.toString() + "\n\n이 투어의 상세 설명이 여기에 표시됩니다.");
                
                loadingBar.setIndeterminate(false);
                loadingBar.setValue(100);
            },
            (error) -> {
                // [EDT - 실패]
                titleLabel.setText("투어 정보 로드 실패");
                descriptionArea.setText(error.getMessage());
                loadingBar.setIndeterminate(false);
            }
        );
    }

    /**
     * (Helper) "예약하기" 버튼 로직
     */
    private void handleReservation() {
        String date = reservationDateField.getText();
        Long userId = sessionManager.getCurrentUserId();

        if (userId == null) {
            JOptionPane.showMessageDialog(this, "로그인이 필요합니다.", "오류", JOptionPane.ERROR_MESSAGE);
            navigateTo("login");
            return;
        }

        // 예약을 비동기로 처리
        runAsyncTask(
            () -> {
                // [백그라운드 스레드]
                // reservationManager.reserve(...)가 예약 ID(Long)를 반환한다고 가정
                Thread.sleep(500); // (처리 시간 흉내)
                return 999L; // reservationManager.reserve(userId, this.currentTourId, date);
            },
            (reservationId) -> {
                // [EDT - 성공]
                JOptionPane.showMessageDialog(this, "예약 성공! 예약 ID: " + reservationId);
                
                // 6. navigateTo (데이터 포함): 예약 ID를 들고 "결과" 페이지로 이동
                navigateTo("reservationResult", reservationId);
            },
            (error) -> {
                // [EDT - 실패]
                JOptionPane.showMessageDialog(this, "예약 실패: " + error.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        );
    }
}