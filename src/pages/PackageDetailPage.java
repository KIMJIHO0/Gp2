package pages;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.*;
import manager.ReservationManager2;
import manager.ReviewManager;
import manager.SessionManager;
import manager.TourCatalog2;
import model.Review;
import model.TourPackage2;
import pages.component.AppNameLabel;
import pages.component.ImageSliderPanel;
import pages.component.InfoRowPanel;
import ui_kit.*;


public class PackageDetailPage extends AppPage {

    // 1. 매니저 (생성자에서 주입)
    private TourCatalog2 tourCatalog;
    private ReservationManager2 reservationManager;
    private SessionManager sessionManager;
    private ReviewManager reviewManager;

    // 2. 현재 페이지의 상태
    private Long currentTourId;
    // private TourPackage2 currentTour; // (실제 로드될 데이터)

    // 3. UI 컴포넌트 (ui-kit 사용)
    private AppNameLabel titleLabel;
    private AppLabel infoTitle;
    private AppLabel selectedDateLabel;
    private AppLabel priceLabel;
    private AppLabel plusInfoLabel;
    private AppTextField reservationDateField;
    private AppButton reserveButton;
    private AppButton backToListButton;
    private AppButton dateSelectButton;
    private AppButton peopleSelectButton;
    private AppList<String> reviewList;
    private ImageSliderPanel imgSlider;
    private InfoRowPanel regionRow;
    private InfoRowPanel periodRow;
    private InfoRowPanel transportRow;
    private InfoRowPanel scheduleRow;
    private InfoRowPanel reservationStatusRow;

    private static final Color U_B_COLOR = UITheme.SEARCH_BAR_BG_COLOR;
    private int selectedPeopleCount = 1;    // 기본 인원 1명
    private int basePrice = 0;              // TourPackage에서 가져올 예정


    /**
     * AppPage의 계약: ServiceContext를 받아 부모에게 넘기고, 필요한 매니저를 가져옵니다.
     */
    public PackageDetailPage(ServiceContext context) {
        super(context);

        this.tourCatalog = context.get(TourCatalog2.class);
        this.reservationManager = context.get(ReservationManager2.class);
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

        // --- 상단: 제목+목록으로 나가기 버튼 ---
        AppPanel titlePanel = new AppPanel(new BorderLayout(10,10)); //titleLabel과 backToListButton을 묶음
        titlePanel.setPreferredSize(new Dimension(0, 80));
        titlePanel.setOpaque(true);
        titlePanel.setBackground(U_B_COLOR);
        this.add(titlePanel, BorderLayout.NORTH);

        backToListButton = new AppButton("←목록으로", false);
        titlePanel.add(backToListButton, BorderLayout.WEST);
        backToListButton.setFont(getFont().deriveFont(Font.BOLD, 16f));
        backToListButton.setBackground(null);
        backToListButton.setBorderPainted(false);
        backToListButton.setOpaque(false);

        titleLabel = new AppNameLabel("투어 정보를 로드 중입니다...");
        try {
            titleLabel.scale(0.7);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        AppPanel spacePanel = new AppPanel(){
            @Override
            public Dimension getPreferredSize() {
            // 왼쪽 패널과 동일한 크기를 반환 → 좌우 대칭
            return backToListButton.getPreferredSize();
            }
        };
        spacePanel.setBackground(U_B_COLOR);
        titlePanel.add(spacePanel, BorderLayout.EAST);


        // --- 중앙 전체 컨테이너: 상세 + 리뷰 + 액션 --- <스크롤이 있어야 함
        AppPanel centerPanel = new AppPanel(new BorderLayout(0,0));
        AppScrollPane contentPanel = new AppScrollPane(centerPanel);
        this.add(contentPanel, BorderLayout.CENTER);

        // --- 1. 상세 정보 영역 (중앙 컨테이너의 상단부) ---
        AppPanel infoPanel = new AppPanel(new BorderLayout());//CENTER, SOUTH만 사용
        centerPanel.add(infoPanel, BorderLayout.NORTH);
        AppPanel padPanel_L = new AppPanel();
        AppPanel padPanel_R = new AppPanel();
        infoPanel.add(padPanel_L, BorderLayout.WEST);
        infoPanel.add(padPanel_R, BorderLayout.EAST);

        AppPanel infoContain = new AppPanel(new FlowLayout(FlowLayout.CENTER, 10,10));
        infoPanel.add(infoContain, BorderLayout.CENTER);
        infoContain.setBackground(new Color(255,255,255));
        imgSlider = new ImageSliderPanel(); 
        imgSlider.setPreferredSize(new Dimension(500, 340)); // 원하는 사이즈로 조정
        infoContain.add(imgSlider, BorderLayout.WEST); //이미지 추가
        AppPanel padPanel_C = new AppPanel();
        infoContain.add(padPanel_C);
        padPanel_C.setBackground(null);
        padPanel_C.setPreferredSize(new Dimension(65,80));
        
        AppPanel infoHeader = new AppPanel(new BorderLayout());//이미지 옆의 내용 추가----
        infoHeader.setPreferredSize(new Dimension(500, 340));
        infoContain.add(infoHeader, BorderLayout.EAST);
        //infoHeader은 NORTH, CENTER, SOUTH만

        infoTitle = new AppLabel("제목 로드 중...");
        infoTitle.setHorizontalAlignment(SwingConstants.CENTER);
        infoHeader.add(infoTitle, BorderLayout.NORTH);
        JSeparator miniSepBar = new JSeparator(SwingConstants.HORIZONTAL);
        infoHeader.add(miniSepBar, BorderLayout.CENTER);
        AppPanel headerContent = new AppPanel();//NORTH CENTER SOUTH
        infoHeader.add(headerContent, BorderLayout.SOUTH);
        
        // TODO: headerContent의 NORTH에 디자인상은 평균평점과 리뷰수.
        //일시 보류gka
        AppPanel infoBlock = new AppPanel(new BorderLayout());
        infoBlock.setLayout(new BoxLayout(infoBlock, BoxLayout.Y_AXIS));
        headerContent.add(infoBlock, BorderLayout.SOUTH);
        regionRow = new InfoRowPanel("지역: ", "로딩중...");
        periodRow = new InfoRowPanel("기간: ", "로딩중...");
        transportRow = new InfoRowPanel("이동수단: ", "로딩중...");
        infoBlock.add(regionRow);
        infoBlock.add(periodRow);
        infoBlock.add(transportRow);

        AppPanel breifInfo = new AppPanel(new BorderLayout()); //north south는 bar자리
        infoPanel.add(breifInfo, BorderLayout.SOUTH);
        JSeparator subSepBar_U = new JSeparator(SwingConstants.HORIZONTAL);
        breifInfo.add(subSepBar_U, BorderLayout.NORTH);
        JSeparator subSepBar_B = new JSeparator(SwingConstants.HORIZONTAL);
        breifInfo.add(subSepBar_B, BorderLayout.SOUTH);
        AppPanel breifInfoBlock = new AppPanel();
        breifInfoBlock.setLayout(new BoxLayout(breifInfoBlock, BoxLayout.Y_AXIS));
        breifInfo.add(breifInfoBlock, BorderLayout.WEST);
        scheduleRow = new InfoRowPanel("일정", "로딩중...");
        reservationStatusRow = new InfoRowPanel("예약현황", "로딩중...");
        breifInfoBlock.add(scheduleRow);
        breifInfoBlock.add(reservationStatusRow);

        // --- 2. 추가 상세 정보 영역(필요시 작성, 현재는 더미 페이지만.) ---
        AppPanel plusInfoPanel = new AppPanel(new BorderLayout());
        centerPanel.add(plusInfoPanel, BorderLayout.CENTER);
        plusInfoLabel = new AppLabel("여기에 내용을 넣을 수 있습니다.");
        plusInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        plusInfoPanel.add(plusInfoLabel, BorderLayout.NORTH);

        // --- 3. 리뷰 영역, 일단은 기존 상태 그대로 붙임. ---
        AppTitledPanel reviewPanel = new AppTitledPanel("리뷰");
        reviewPanel.setLayout(new BorderLayout(5, 5));
        centerPanel.add(reviewPanel, BorderLayout.SOUTH);
        reviewList = new AppList<>();
        reviewPanel.add(reviewList, BorderLayout.CENTER);


        // --- 하단: 예약 및 기타 액션 ---
        AppPanel actionPanel = new AppPanel(new BorderLayout(10,10)); //하단 컴포넌트를 묶음
        actionPanel.setPreferredSize(new Dimension(0, 80));
        actionPanel.setOpaque(true);
        actionPanel.setBackground(U_B_COLOR);
        this.add(actionPanel, BorderLayout.SOUTH);

        AppPanel actionRowPanel = new AppPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actionRowPanel.setBackground(U_B_COLOR);
        // 숨겨진 날짜 저장용 텍스트 필드 (UI에는 붙이지 않음/handleReservation()에서 수정 없이 사용)
        reservationDateField = new AppTextField(12);
        // 1) 날짜 선택 버튼 + 선택된 날짜 라벨
        dateSelectButton = new AppButton("날짜 선택");
        selectedDateLabel = new AppLabel("날짜 미선택", AppLabel.LabelType.SMALL);

        // 2) 인원 선택 버튼 + 금액 라벨 (이벤트는 다음 단계에서)
        peopleSelectButton = new AppButton("인원 선택");
        priceLabel = new AppLabel("0", AppLabel.LabelType.SMALL);
        AppLabel wonLabel = new AppLabel("만원", AppLabel.LabelType.SMALL);

        // 3) 예약하기 버튼
        reserveButton = new AppButton("예약하기");

        // 한 줄에 순서대로 배치
        actionRowPanel.add(dateSelectButton);
        actionRowPanel.add(selectedDateLabel);
        actionRowPanel.add(peopleSelectButton);
        actionRowPanel.add(priceLabel);
        actionRowPanel.add(wonLabel);
        actionRowPanel.add(reserveButton);

        // 하단 패널에 붙이기
        actionPanel.add(actionRowPanel, BorderLayout.EAST);
    }

    private void initListeners() {
        // "목록으로" 버튼 클릭 -> "tourList" 페이지로 데이터 없이 이동
        backToListButton.addActionListener(e -> {
            navigateTo("catalog", 1); // 1. navigateTo (데이터 없음)
        });

        // "예약하기" 버튼 클릭 -> 예약 실행
        reserveButton.addActionListener(e -> {
            handleReservation();
        });

        // "날짜 선택" 버튼 클릭 -> 날짜 입력받기
        dateSelectButton.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(
                this,
                "희망 날짜를 입력해주세요 (예: 2025-11-18)",
                "날짜 선택",
                JOptionPane.PLAIN_MESSAGE
            );

            // 취소하거나 빈 입력이면 무시
            if (input == null || input.isBlank()) {
                return;
            }

            input = input.trim();

            try {
                // 형식 검증 (yyyy-MM-dd)
                LocalDate parsed = LocalDate.parse(input);

                // 라벨에 표시
                selectedDateLabel.setText(parsed.toString());

                // 예약 로직에서 쓰기 위해 내부 필드에도 세팅
                reservationDateField.setText(parsed.toString());

            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "날짜 형식이 올바르지 않습니다.\n예: 2025-11-18",
                    "오류",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // "인원 선택" 버튼 클릭 -> 인원 입력 + 금액 재계산
        peopleSelectButton.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this,"인원 수를 입력해주세요 (예: 1, 2, 3...)",
                "인원 선택",JOptionPane.PLAIN_MESSAGE);

            // 취소 또는 공백이면 무시
            if (input == null || input.isBlank()) return;

            input = input.trim();

            try {
                int people = Integer.parseInt(input);

                if (people <= 0) {
                    JOptionPane.showMessageDialog(this,"인원 수는 1명 이상이어야 합니다.",
                    "오류",JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // TODO: 필요하면 최대 인원 제한도 여기서 체크
                selectedPeopleCount = people;

                // 금액 라벨 갱신
                updatePriceLabel();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "인원 수는 숫자로 입력해주세요.",
                "오류",JOptionPane.ERROR_MESSAGE);
            }
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
            plusInfoLabel.setText("목록에서 투어를 선택해주세요.");

            
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
        runAsyncTask(
            () -> {
                // [백그라운드 스레드]
                int id = tourId.intValue();
                TourPackage2 tour = tourCatalog.getTour(id);
                
                if (tour == null) {
                    throw new IllegalStateException("해당 투어 정보를 찾을 수 없습니다. ID=" + id);
                }
                return tour;
            },
            (tour) -> {
                titleLabel.setText(tour.name);
                infoTitle.setText(tour.name);

                imgSlider.setSlids(java.util.List.of(
                    "슬라이드 1: 더미",
                    "슬라이드 2: 더미",
                    "슬라이드 3: 더미"
                ));
                // 상단 infoBlock 값 채우기
                if (regionRow != null) {
                    regionRow.setValue(tour.place); // 지역
                }
                if (periodRow != null) {
                    periodRow.setValue(tour.day_long + "일"); // 기간
                }
                
                // TODO : 이동수단 임시값
                if (transportRow != null) {
                    //transportRow.setValue(tour.transport != null ? tour.transport : "-");
                    transportRow.setValue("버스");
                }
                // TODO : breifInfoBlock 값 채우기 (일정/예약현황은 일단 임시 값)
                if (scheduleRow != null) scheduleRow.setValue("총 " + tour.day_long + "일 일정");
                // TODO : 실제 예약 현황 로직 생기면 교체
                if (reservationStatusRow != null) reservationStatusRow.setValue("예약 가능");
                    
                // 상세 정보 문자열 구성
                StringBuilder sb = new StringBuilder();
                sb.append("지역: ").append(tour.place).append("\n");
                sb.append("일정: ").append(tour.day_long).append("일\n\n");

                sb.append("■ 일정 상세\n");
                for (String s : tour.schedule) {
                    sb.append("- ").append(s).append("\n");
                }

                plusInfoLabel.setText(toHtml(sb.toString()));

                // === 여기서부터 하단 예약 바 초기화 ===

                // TODO: TourPackage2 안의 실제 가격 필드에 맞게 수정할 것.
                // 예시: tour.pricePerPerson, tour.price, tour.basePrice 등
                // 지금은 tour.price 라고 가정:
                this.basePrice = tour.price;   // <-- 실제 필드명에 맞게 바꿔라
                this.selectedPeopleCount = 1; // 기본 인원 1명으로 초기화
                updatePriceLabel(); // 금액 라벨 갱신

                // 날짜/라벨 초기화 (선택)
                if (selectedDateLabel != null) selectedDateLabel.setText("날짜 미선택");

                loadReviewsForCurrentTour(tourId);
            },
            (error) -> {
                // [EDT - 실패]
                titleLabel.setText("투어 정보 로드 실패");
                plusInfoLabel.setText(error.getMessage());

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
                return reservationManager.reserve(clientId, tourId, startDate, selectedPeopleCount); // ResponseCode 반환
            },
            
            // [성공 콜백 - EDT] ResponseCode에 따른 분기
            (code) -> {
                reserveButton.setEnabled(true);
                // [EDT - 성공]
                if (code == ReservationManager2.ResponseCode.SUCCESS) {
                    // 예약 성공 → 이동 여부 묻는 확인 다이얼로그
                    String[] select = {"예약 목록", "패키지 목록", "추천 목록", "취소"};
                    int choice = JOptionPane.showOptionDialog(this, "예약이 완료되었습니다.", "예약 완료",
                    0, 0, null, select, select[0]);

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

    // 하단 금액 라벨을 현재 basePrice × selectedPeopleCount 값으로 갱신
    private void updatePriceLabel() {
        if (priceLabel == null) return;  // 아직 UI 초기화 전일 수 있으니 방어
        int total = basePrice * selectedPeopleCount;
        priceLabel.setText(String.valueOf(total));
    }

    private String toHtml(String multiLineText) {
        if (multiLineText == null) return "<html></html>";
        String escaped = multiLineText
            .replace("\n", "<br>")
            .replace(" ", "&nbsp;"); // 선택 (들여쓰기 유지)
        return "<html>" + escaped + "</html>";
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