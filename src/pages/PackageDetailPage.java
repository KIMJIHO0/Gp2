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
// TODO : message 테마 일괄 변경 필요
import util.RateToStar;

public class PackageDetailPage extends AppPage {

    // 1. 매니저 (생성자에서 주입)
    private TourCatalog2 tourCatalog;
    private ReservationManager2 reservationManager;
    private SessionManager sessionManager;
    private ReviewManager reviewManager;

    // 2. 현재 페이지의 상태
    private Long currentTourId;
    private TourPackage2 currentTour;

    // 3. UI 컴포넌트 (ui-kit 사용)
    private AppNameLabel titleLabel;
    private AppLabel infoTitle;
    private AppLabel selectedDateLabel;
    private AppLabel priceLabel;
    private AppLabel plusInfoLabel;
    private AppLabel ratingSummaryLabel;
    private AppLabel peopleLabel;
    private AppTextField reservationDateField;
    private AppButton reserveButton;
    private AppButton backToListButton;
    private AppButton dateSelectButton;
    private AppButton peopleSelectButton;
    private AppComboBox<Integer> peopleCombo;
    private AppComboBox<LocalDate> dateCombo;
    private AppList<String> reviewList;
    private ImageSliderPanel imgSlider;
    private InfoRowPanel regionRow;
    private InfoRowPanel periodRow;
    private InfoRowPanel transportRow;
    private InfoRowPanel scheduleRow;
    private InfoRowPanel reservationStatusRow;

    private static final Color U_B_COLOR = UITheme.SEARCH_BAR_BG_COLOR;
    private int selectedPeopleCount = 1;    // 기본 인원 1명

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
        infoPanel.setBackground(new Color(255,255,255));

        AppPanel infoContain = new AppPanel(new FlowLayout(FlowLayout.CENTER, 10,40));
        infoPanel.add(infoContain, BorderLayout.CENTER);
        infoContain.setBackground(null);
        imgSlider = new ImageSliderPanel(); 
        imgSlider.setPreferredSize(new Dimension(500, 300)); // 원하는 사이즈로 조정
        infoContain.add(imgSlider, BorderLayout.WEST); //이미지 추가
        AppPanel padPanel_C = new AppPanel();
        infoContain.add(padPanel_C);
        padPanel_C.setBackground(null);
        padPanel_C.setPreferredSize(new Dimension(65,80));
        
        AppPanel infoHeader = new AppPanel(new BorderLayout());//이미지 옆의 내용 추가----
        infoHeader.setPreferredSize(new Dimension(500, 310));
        infoHeader.setBackground(null);
        infoContain.add(infoHeader, BorderLayout.EAST);
        //infoHeader은 NORTH, CENTER, SOUTH만

        infoTitle = new AppLabel("제목 로드 중...", AppLabel.LabelType.SUBTITLE);
        infoTitle.setFont(getFont().deriveFont(30f));
        infoTitle.setHorizontalAlignment(SwingConstants.LEFT);
        infoHeader.add(infoTitle, BorderLayout.NORTH);
        AppPanel sepBarPanel = new AppPanel(new FlowLayout());
        JSeparator miniSepBar = new JSeparator(SwingConstants.HORIZONTAL);
        miniSepBar.setPreferredSize(new Dimension(480, 1));
        miniSepBar.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        sepBarPanel.add(miniSepBar);
        sepBarPanel.setBackground(null);
        infoHeader.add(sepBarPanel, BorderLayout.CENTER);
        AppPanel headerContent = new AppPanel(new BorderLayout());//NORTH CENTER SOUTH
        infoHeader.add(headerContent, BorderLayout.SOUTH);
        headerContent.setPreferredSize(new Dimension(480, 250));
        headerContent.setBackground(null);

        AppPanel ratingPanel = new AppPanel(new FlowLayout(FlowLayout.RIGHT, 15,5));
        headerContent.add(ratingPanel, BorderLayout.NORTH);
        ratingSummaryLabel = new AppLabel("평점 정보 로드 중...", AppLabel.LabelType.NORMAL);
        ratingSummaryLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        ratingSummaryLabel.setFont(getFont().deriveFont(16f));
        ratingPanel.add(ratingSummaryLabel);
        ratingPanel.setBackground(null);
        

        AppPanel infoBlock = new AppPanel(new BorderLayout());
        infoBlock.setBackground(null);
        infoBlock.setLayout(new BoxLayout(infoBlock, BoxLayout.Y_AXIS));
        headerContent.add(infoBlock, BorderLayout.SOUTH);
        regionRow = new InfoRowPanel("지역: ", "로딩중...", AppLabel.LabelType.BOLD, AppLabel.LabelType.NORMAL);
        periodRow = new InfoRowPanel("기간: ", "로딩중...", AppLabel.LabelType.BOLD, AppLabel.LabelType.NORMAL);
        transportRow = new InfoRowPanel("이동수단: ", "로딩중...", AppLabel.LabelType.BOLD, AppLabel.LabelType.NORMAL);
        regionRow.setBackground(null);
        periodRow.setBackground(null);
        transportRow.setBackground(null);
        regionRow.setFont(getFont().deriveFont(20f));
        infoBlock.add(regionRow);
        infoBlock.add(periodRow);
        infoBlock.add(transportRow);
        // TODO : infoBlock/breifInfoBlock들 텍스트 크기 조정

        AppPanel breifInfo = new AppPanel(new BorderLayout()); //north south는 bar자리
        infoPanel.add(breifInfo, BorderLayout.SOUTH);
        breifInfo.setBackground(null);
        JSeparator subSepBar_U = new JSeparator(SwingConstants.HORIZONTAL);
        breifInfo.add(subSepBar_U, BorderLayout.NORTH);
        JSeparator subSepBar_B = new JSeparator(SwingConstants.HORIZONTAL);
        breifInfo.add(subSepBar_B, BorderLayout.SOUTH);
        AppPanel breifInfoBlock = new AppPanel();
        breifInfoBlock.setLayout(new BoxLayout(breifInfoBlock, BoxLayout.Y_AXIS));
        breifInfo.add(breifInfoBlock, BorderLayout.WEST);
        breifInfoBlock.setBackground(null);
        scheduleRow = new InfoRowPanel("일정", "로딩중...");
        reservationStatusRow = new InfoRowPanel("예약현황", "로딩중...");
        scheduleRow.setBackground(null);
        reservationStatusRow.setBackground(null);
        breifInfoBlock.add(scheduleRow);
        breifInfoBlock.add(reservationStatusRow);

        // --- 2. 추가 상세 정보 영역 ---
        // TODO : UI디자인 수정 필요
        AppPanel plusInfoPanel = new AppPanel(new BorderLayout());
        centerPanel.add(plusInfoPanel, BorderLayout.CENTER);
        plusInfoPanel.setBackground(null);
        plusInfoLabel = new AppLabel("여기에 내용을 넣을 수 있습니다.");
        plusInfoLabel.setFont(getFont().deriveFont(20f));
        plusInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        plusInfoPanel.add(plusInfoLabel, BorderLayout.NORTH);

        // --- 3. 리뷰 영역, 일단은 기존 상태 그대로 붙임. ---
        // TODO : 리뷰 칸 축소, 디자인 수정, 내부에도 스크롤이 있어 전체 페이지 스크롤에 방해가 되는 문제
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
        dateCombo = new AppComboBox<>();
        dateCombo.setVisible(false);   // 처음엔 숨김

        // 2) 인원 선택 버튼 + 금액 라벨 (이벤트는 다음 단계에서)
        peopleSelectButton = new AppButton("인원 선택");
        peopleLabel = new AppLabel("인원", AppLabel.LabelType.BOLD);
        peopleCombo = new AppComboBox<>();
        peopleCombo.setVisible(false); // 처음엔 숨김

        priceLabel = new AppLabel("0", AppLabel.LabelType.SMALL);
        AppLabel wonLabel = new AppLabel("만원", AppLabel.LabelType.SMALL);

        // 3) 예약하기 버튼
        reserveButton = new AppButton("예약하기");

        // 한 줄에 순서대로 배치
        actionRowPanel.add(dateSelectButton);
        actionRowPanel.add(selectedDateLabel);
        actionRowPanel.add(dateCombo); 
        actionRowPanel.add(peopleSelectButton);
        actionRowPanel.add(peopleCombo);
        actionRowPanel.add(peopleLabel);
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
        
        // "인원 선택" 버튼 클릭 -> 콤보박스 토글 + 드롭다운 열기
        peopleSelectButton.addActionListener(e -> {
            if (peopleCombo == null) return;

            boolean visible = peopleCombo.isVisible();
            peopleCombo.setVisible(true);
            peopleSelectButton.setVisible(false);

            if (!visible) {
                // 처음 보여줄 때 바로 드롭다운 펼치기
                peopleCombo.requestFocusInWindow();
                peopleCombo.showPopup();
            }
        });
        // 콤보 박스 변경시 인원/가격 갱신
        peopleCombo.addActionListener(e -> {
            Object sel = peopleCombo.getSelectedItem();
            if (sel instanceof Integer i) {
                selectedPeopleCount = i;
                updatePriceLabel();
                peopleLabel.setText("인원: " + i + "명");
            }
            peopleSelectButton.setVisible(true);
            peopleCombo.setVisible(false);
        });
        // "날짜 선택" 버튼 클릭 -> 콤보박스 토글 + 드롭다운 열기
        dateSelectButton.addActionListener(e -> {
            if (dateCombo == null) return;
            if (!dateCombo.isEnabled()) return; // 예약 가능한 날짜 없을 때 방어

            boolean visible = dateCombo.isVisible();
            dateCombo.setVisible(true);
            dateSelectButton.setVisible(false);

            if (!visible) {
                dateCombo.requestFocusInWindow();
                dateCombo.showPopup();
            }
        });
        dateCombo.addActionListener(e -> {
            Object sel = dateCombo.getSelectedItem();
            if (sel instanceof LocalDate d) {
                String text = d.toString();           // "2025-12-01" 형식
                selectedDateLabel.setText(text);      // 화면에 표시
                reservationDateField.setText(text);   // 예약 로직에서 쓰는 값
            }
            dateSelectButton.setVisible(true);
            dateCombo.setVisible(false);
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
                this.currentTour = tour;
                titleLabel.setText(tour.name);
                infoTitle.setText(tour.name);
                // 이미지 슬라이더 실제 이미지 적용
                if (tour.shots != null && tour.shots.length > 0) {
                    imgSlider.setSlids(java.util.Arrays.asList(tour.shots));
                } else {
                    imgSlider.setSlids(java.util.List.of());   // 빈 슬라이드 표시
                }
                // 상단 infoBlock 값 채우기
                if (regionRow != null) {
                    regionRow.setValue(tour.place); // 지역
                }
                if (periodRow != null) {
                    periodRow.setValue(tour.day_long + "일"); // 기간
                }
                if (transportRow != null) {
                    String[] transport = tour.transport;
                    String transportText;
                    if (transport == null || transport.length == 0) {
                        transportText = "-";
                    } else {
                        transportText = String.join(", ", transport);
                    }

                    transportRow.setValue(transportText);
                }
                if (scheduleRow != null) {
                    java.util.List<String> schedule = tour.schedule;  // 이미 plusInfoLabel 만들 때 쓰던 그 배열

                    String text;
                    if (schedule == null || schedule.isEmpty()) {
                        text = "일정 정보가 없습니다.";
                    } else if (schedule.size() == 1) {
                        // 1일짜리거나 한 줄만 있을 때
                        text = "1일 일정 - " + schedule.get(0);
                    } else {
                        text = String.join("-", schedule);
                    }
                    scheduleRow.setValue(text);
                }
                if (reservationStatusRow != null) {
                    int min = 0;
                    int max = 0;
                    if (tour.headcount_range != null && tour.headcount_range.length >= 2) {
                        min = tour.headcount_range[0];
                        max = tour.headcount_range[1];
                    }
                    // TODO: 지금까지 예약된 인원으로 변경 가능하면 수정
                    int current = 0;
                    String statusText = String.format(
                        "현재 %d명  |  최대 %d명  |  최소출발인원 %d명", current, max, min);
                        
                    reservationStatusRow.setValue(statusText);
                }
                    
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

                int max = 1;
                if (tour.headcount_range != null && tour.headcount_range.length >= 2) {
                    max = tour.headcount_range[1];
                }
                if (max < 1) max = 1;
                // 콤보 옵션 채우기: 1 ~ max
                peopleCombo.removeAllItems();
                for (int i = 1; i <= max; i++) {
                    peopleCombo.addItem(i);
                }
                peopleCombo.setSelectedItem(selectedPeopleCount);
                // ★ peopleLabel 초기 표시
                if (peopleLabel != null) {
                    peopleLabel.setText("인원: " + selectedPeopleCount + "명");
                }
                updatePriceLabel();
                // === 날짜 콤보 옵션 채우기 ===
                if (dateCombo != null) {
                    dateCombo.removeAllItems();

                    // 오늘부터 N일치 중에서 예약 가능 날짜만 넣기 (N은 적당히 조절)
                    LocalDate today = LocalDate.now();
                    int added = 0;

                    for (int i = 0; i < 60; i++) { // 60일치 예시
                        LocalDate d = today.plusDays(i);
                        if (tourCatalog.checkDateValidate(tour, d)) {
                            dateCombo.addItem(d);
                            added++;
                        }
                    }

                    if (added == 0) {
                        dateSelectButton.setEnabled(false);
                        selectedDateLabel.setText("예약 가능한 날짜 없음");
                        reservationDateField.setText("");
                    } else {
                        dateSelectButton.setEnabled(true);
                        // 기본값은 '미선택' 유지 → 콤보 선택도 비우기
                        dateCombo.setSelectedIndex(-1);
                        selectedDateLabel.setText("날짜 미선택");
                        reservationDateField.setText("");
                    }
                }

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
        //날짜 유효성 검증
        boolean dateOk = tourCatalog.checkDateValidate(currentTour, startDate);
        if (!dateOk) {
            JOptionPane.showMessageDialog(this,"해당 날짜에는 예약할 수 없습니다.\n(예약 불가 날짜입니다.)","오류",JOptionPane.ERROR_MESSAGE);
            return;
        }
        //인원수 범위 검증
        boolean headcountOk = tourCatalog.checkHeadcountValidate(currentTour, selectedPeopleCount);
        if (!headcountOk) {
            int min = 0;
            int max = 0;
            if (currentTour.headcount_range != null && currentTour.headcount_range.length >= 2) {
                min = currentTour.headcount_range[0];
                max = currentTour.headcount_range[1];
            }

            String msg = String.format("인원 수가 허용 범위를 벗어났습니다.\n(최소 %d명, 최대 %d명)",min,max);
            JOptionPane.showMessageDialog(this,msg,"오류",JOptionPane.ERROR_MESSAGE);
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

    private void updatePriceLabel() {
        if (priceLabel == null) return;
        // 투어 정보가 아직 없으면 0 표시
        if (currentTour == null) {
            priceLabel.setText("0");
            return;
        }
        int total = tourCatalog.totalPrice(currentTour, selectedPeopleCount);
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

                for (Review r : reviews) {
                    String line = String.format("[%s] 작성자 %d - %s",RateToStar.stringify(r.rate),r.writer_id,r.content);
                    model.addElement(line);
                }
                double avgRate = reviewManager.getAverageRateOfTour(Math.toIntExact(currentTourId));
                int count = reviews.size();
                if (ratingSummaryLabel != null) ratingSummaryLabel.setText(String.format("평균 %s (%d개 리뷰)", RateToStar.stringify((int)Math.round(avgRate)), count));
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