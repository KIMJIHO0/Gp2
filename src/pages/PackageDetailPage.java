package pages;
//v이따...연결해서 테스트해보고 리뷰 작성 버튼 삭제
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.*;
import manager.ReservationManager;
import manager.ReviewManager;
import manager.SessionManager;
import manager.TourCatalog;
import model.Review;
import model.TourPackage;
import ui_kit.*;
import ui_kit.AppLabel.LabelType;


public class PackageDetailPage extends AppPage {

    // 1. 매니저 (생성자에서 주입)
    private TourCatalog tourCatalog;
    private ReservationManager reservationManager;
    private SessionManager sessionManager;
    private ReviewManager reviewManager;

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
    private AppList<String> reviewList;

    /**
     * AppPage의 계약: ServiceContext를 받아 부모에게 넘기고, 필요한 매니저를 가져옵니다.
     */
    public PackageDetailPage(ServiceContext context) {
        super(context);

        this.tourCatalog = context.get(TourCatalog.class);
        this.reservationManager = context.get(ReservationManager.class);
        this.sessionManager = context.get(SessionManager.class);
        this.reviewManager = context.get(ReviewManager.class);

        initUI();
        initListeners();
        subscribeToGlobalEvents();
    }

    /**
     * AppTitledPanel, AppPanel을 사용한 레이아웃 구성
     */
    private void initUI() {
        this.setLayout(new BorderLayout(10, 10)); // 페이지 전체 레이아웃
        
        //디자인에 맞게 수정 필요 
        // --- 상단: 제목 ---
        titleLabel = new AppLabel("투어 정보를 로드 중입니다...", LabelType.TITLE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(titleLabel, BorderLayout.NORTH);

        // --- 중앙 전체 컨테이너: 상세 + 리뷰 + 액션 ---
        AppPanel centerPanel = new AppPanel(new BorderLayout(10, 10));
        this.add(centerPanel, BorderLayout.CENTER);

        // --- (1) 상세 정보 영역 ---
        AppTitledPanel infoPanel = new AppTitledPanel("상세 정보");
        infoPanel.setLayout(new BorderLayout(5, 5));

        descriptionArea = new AppTextArea();
        descriptionArea.setEditable(false);
        infoPanel.add(descriptionArea, BorderLayout.CENTER);

        loadingBar = new AppProgressBar();
        infoPanel.add(loadingBar, BorderLayout.SOUTH);

        centerPanel.add(infoPanel, BorderLayout.NORTH);

        // --- (2) 리뷰 목록 영역 ---
        AppTitledPanel reviewPanel = new AppTitledPanel("리뷰");
        reviewPanel.setLayout(new BorderLayout(5, 5));

        reviewList = new AppList<>();
        reviewPanel.add(reviewList, BorderLayout.CENTER);

        centerPanel.add(reviewPanel, BorderLayout.CENTER);

        // --- (3) 하단: 예약 및 기타 액션 ---
        AppTitledPanel actionPanel = new AppTitledPanel("예약 및 기타");
        actionPanel.setLayout(new BorderLayout(5, 5));

        AppPanel reservationForm = new AppPanel(new FlowLayout(FlowLayout.LEFT));
        reservationForm.add(new AppLabel("희망 날짜:"));
        reservationDateField = new AppTextField(12);
        reserveButton = new AppButton("예약하기");
        reservationForm.add(reservationDateField);
        reservationForm.add(reserveButton);
        actionPanel.add(reservationForm, BorderLayout.NORTH);

        AppPanel otherButtons = new AppPanel(new FlowLayout(FlowLayout.RIGHT));
        addReviewButton = new AppButton("리뷰 작성");
        backToListButton = new AppButton("목록으로");
        otherButtons.add(addReviewButton);
        otherButtons.add(backToListButton);
        actionPanel.add(otherButtons, BorderLayout.SOUTH);

        centerPanel.add(actionPanel, BorderLayout.SOUTH);
    }

    /**
     * UI 컴포넌트의 이벤트 리스너를 설정합니다.
     */
    private void initListeners() {
        // "목록으로" 버튼 클릭 -> "tourList" 페이지로 데이터 없이 이동
        backToListButton.addActionListener(e -> {
            navigateTo("catalog", 1); // 1. navigateTo (데이터 없음)
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
            
            //JOptionPane.showMessageDialog(this, "리뷰 작성 이벤트가 발행되었습니다. (구독한 페이지가 반응합니다)");
            navigateTo("reviewWrite", currentTourId);

        });
    }
    
    /**
     * 전역 이벤트를 구독합니다.
     */
    private void subscribeToGlobalEvents() {
        subscribeEvent(ReviewAddedEvent.class, event -> {
            if (currentTourId != null && currentTourId == event.getTourId()) {
                loadReviewsForCurrentTour(currentTourId);
            }
        });
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
            
            //상세 정보 선 로드
            loadTourDetails(this.currentTourId);
        
        } else {
            // ID가 없거나 잘못된 경우
            titleLabel.setText("오류: 잘못된 접근입니다.");
            descriptionArea.setText("목록에서 투어를 선택해주세요.");
            loadingBar.setVisible(false);

            //상세 정보 로드 실패 시 리뷰 오류
            DefaultListModel<String> model = new DefaultListModel<>();
            model.addElement("리뷰를 불러올 수 없습니다. 다시 목록에서 진입해주세요.");
            reviewList.setModel(model);
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
                int id = Math.toIntExact(tourId);   // Long → int 변환 (범위 초과 시 예외)
                TourPackage tour = tourCatalog.getTour(id);
                
                if (tour == null) {
                    throw new IllegalStateException("해당 투어 정보를 찾을 수 없습니다. ID=" + id);
                }
                return tour;
            },
            (tour) -> {
                titleLabel.setText(tour.name);

                // 상세 정보 문자열 구성
                StringBuilder sb = new StringBuilder();
                sb.append("지역: ").append(tour.place).append("\n");
                sb.append("일정: ").append(tour.day_long).append("일\n\n");

                sb.append("■ 일정 상세\n");
                for (String s : tour.schedule) {
                    sb.append("- ").append(s).append("\n");
                }

                descriptionArea.setText(sb.toString());
                
                loadingBar.setIndeterminate(false);
                loadingBar.setVisible(false);

                loadReviewsForCurrentTour(tourId);
            },
            (error) -> {
                // [EDT - 실패]
                titleLabel.setText("투어 정보 로드 실패");
                descriptionArea.setText(error.getMessage());
                loadingBar.setIndeterminate(false);
                loadingBar.setVisible(false);

                // ✅ 리뷰 영역도 “상세 실패 때문에 안 보여줌” 식으로 한 번만 정리
                DefaultListModel<String> model = new DefaultListModel<>();
                model.addElement("투어 정보를 불러오지 못해 리뷰도 표시할 수 없습니다.");
                reviewList.setModel(model);
            }
        );
    }

    /**
     * (Helper) "예약하기" 버튼 로직
     */
    private void handleReservation() {
        String dateText = reservationDateField.getText();
        Long userId = sessionManager.getCurrentUserId();

        if (userId == null) {
            JOptionPane.showMessageDialog(this, "로그인이 필요합니다.", "오류", JOptionPane.ERROR_MESSAGE);
            navigateTo("login");
            return;
        }
        // 3) 날짜 입력 확인
        if (dateText == null || dateText.isBlank()) {
            JOptionPane.showMessageDialog(this,"희망 날짜를 입력해주세요.","오류",JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 4) 날짜 파싱 (형식은 팀 규칙에 맞게 수정 가능. 여기선 yyyy-MM-dd 가정)
        final LocalDate startDate;
        try {
            startDate = LocalDate.parse(dateText); // 예: "2025-11-18"
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,"날짜 형식이 올바르지 않습니다. 예: 2025-11-18","오류",JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 버튼 잠깐 막아두기
        reserveButton.setEnabled(false);

        // 예약을 비동기로 처리
        runAsyncTask(
            () -> {
                // [백그라운드 스레드]
                int clientId = Math.toIntExact(userId);
                int tourId = Math.toIntExact(currentTourId);
                return reservationManager.reserve(clientId, tourId, startDate); // ResponseCode 반환
            },
            
            // [성공 콜백 - EDT] ResponseCode에 따른 분기
            (code) -> {
                reserveButton.setEnabled(true);
                // [EDT - 성공]
                if (code == ReservationManager.ResponseCode.SUCCESS) {
                    // 예약 성공 → 이동 여부 묻는 확인 다이얼로그
                    String[] select = {"예약 목록", "패키지 목록", "추천 목록", "취소"};
                    int choice = JOptionPane.showOptionDialog(this, "예약이 완료되었습니다.", "예약 완료",
                    0, 0, null, select, select[1]);

                    switch (choice) {
                        case 0 :
                            navigateTo("catalog", 2);
                            break;
                        case 1: navigateTo("catalog", 1); break;
                        case 2: navigateTo("catalog",3); break;
                        default: break;
                    }
                    // NO_OPTION이면 아무 것도 안 하고 현재 상세 페이지에 남아 있음
                
                }else {
                    // 실패 코드별 메시지 매핑
                    String msg = switch (code) {
                        case INVALID_CLIENT -> "사용자 정보를 확인할 수 없습니다.";
                        case INVALID_TOUR   -> "해당 상품 정보를 찾을 수 없습니다.";
                        case ALREADY_RESERVED -> "이미 같은 조건으로 예약한 내역이 있습니다.";
                        case OLD_DATE       -> "과거 날짜로는 예약할 수 없습니다.";
                        default             -> "예약에 실패했습니다. 잠시 후 다시 시도해주세요.";
                    };
                    JOptionPane.showMessageDialog(this,msg,"예약 실패",JOptionPane.ERROR_MESSAGE);
                }
            },
            (error) -> {
                // [EDT - 실패]
                JOptionPane.showMessageDialog(this, "예약 실패: " + error.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        );
    }

    private void loadReviewsForCurrentTour(Long tourId) {
        if (tourId == null) {
            // 잘못된 접근이면 리스트에 안내만 띄움
            DefaultListModel<String> model = new DefaultListModel<>();
            model.addElement("리뷰를 불러올 투어 ID가 없습니다.");
            reviewList.setModel(model);
            return;
        }

        // 첫 상태: 로딩 중 안내
        DefaultListModel<String> loadingModel = new DefaultListModel<>();
        loadingModel.addElement("리뷰를 불러오는 중입니다...");
        reviewList.setModel(loadingModel);

        runAsyncTask(
            // [백그라운드] 리뷰 목록 조회
            () -> {
                int tid = Math.toIntExact(tourId);
                return reviewManager.getReviewsByTour(tid);  // List<Review>
            },
            // [성공] UI에 바인딩
            reviews -> {
                DefaultListModel<String> model = new DefaultListModel<>();

                if (reviews.isEmpty()) {
                    model.addElement("등록된 리뷰가 없습니다.");
                } else {
                    for (Review r : reviews) {
                        // 원하는 형식으로 한 줄 문자열 구성
                        // 필요하면 줄 나누기나 포맷 더 다듬어도 됨
                        String line = String.format(
                            "[★%d] 작성자 %d - %s",
                            r.rate,
                            r.writer_id,
                            r.content
                        );
                        model.addElement(line);
                    }
                }

                reviewList.setModel(model);
            },
            // [실패] 에러 메시지 출력
            error -> {
                DefaultListModel<String> model = new DefaultListModel<>();
                model.addElement("리뷰를 불러오는 중 오류가 발생했습니다: " + error.getMessage());
                reviewList.setModel(model);
        }   
        );
    }

}