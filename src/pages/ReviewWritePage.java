//pageID: reviewWrite
//아이디어: 예약 항목에 대한 정보가 필요한가?
//initListeners()부분의 취소 버튼 클릭 시 연결되는 id 수정 필요

package pages;
//v
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import manager.ReviewManager;
import manager.SessionManager;
import manager.TourCatalog;
import model.TourPackage;
import ui_kit.*;


public class ReviewWritePage extends AppPage {
    private TourCatalog tourCatalog;
    private ReviewManager reviewManager;
    private SessionManager sessionManager;

    // --- 2. 페이지 상태 필드 ---
    // 이 페이지가 어떤 패키지에 대한 리뷰인지 식별하는 ID (navigateTo로부터 전달받음)
    private Long targetPackageId;

    private int rating = 0;
    private static final int STAR_COUNT = 5; //확정된 별 개수(0~5)

    //ui 컴포넌트 영역
    private AppLabel packageLabel; //리뷰를 작성할 패키지 이름
    private AppProgressBar loadingBar; //리뷰 저장 시 로딩 상태
    private AppTextArea reviewArea; //입력 영역
    private AppButton completeButton; //완료 버튼
    private AppButton cancelButton; //취소 버튼
    private AppLabel[] stars; //별.


    public ReviewWritePage(ServiceContext context) {
        super(context);
        // 필요한 매니저 주입.
        //    - TourCatalog: 패키지 이름/정보 조회용
        //    - ReviewManager: 리뷰 작성/저장 호출용
        //    - SessionManager: 로그인 사용자 ID 확인용
        this.tourCatalog = context.get(TourCatalog.class);
        this.reviewManager = context.get(ReviewManager.class);
        this.sessionManager = context.get(SessionManager.class);
        
        // 2. UI 레이아웃 구성
        initUI();
        
        // 3) 이벤트 리스너 연결 (버튼 클릭, 별점 클릭 등)
        initListeners();
    }

    private void initUI() {
        this.setLayout(new BorderLayout(10,10));
        
        //상품 이름(제목)----
        packageLabel = new AppLabel("정보를 불러오는 중입니다...");
        packageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(packageLabel, BorderLayout.NORTH);

        //중간-리뷰 내용들----
        AppTitledPanel reviewPanel = new AppTitledPanel("리뷰 작성");
        reviewPanel.setLayout(new BorderLayout(5,5));
        this.add(reviewPanel, BorderLayout.CENTER);

        //여기서 별점 체크- AppPanel을 div처럼 사용
        AppPanel ratingPanel = new AppPanel(new FlowLayout(FlowLayout.LEFT));
        ratingPanel.add(new AppLabel("별점: "));
        createRatingStars(ratingPanel);
        reviewPanel.add(ratingPanel, BorderLayout.NORTH);

        //리뷰 작성
        reviewArea = new AppTextArea();
        reviewPanel.add(reviewArea, BorderLayout.CENTER);

        loadingBar = new AppProgressBar();
        loadingBar.setVisible(false);
        loadingBar.setIndeterminate(false);
        reviewPanel.add(loadingBar, BorderLayout.SOUTH);

        //하단- 작성 완료 버튼/취소 버튼
        AppPanel buttons = new AppPanel(new FlowLayout(FlowLayout.RIGHT));
        completeButton = new AppButton("작성 완료");
        cancelButton = new AppButton("취소");
        buttons.add(completeButton);
        buttons.add(cancelButton);
        this.add(buttons, BorderLayout.SOUTH);
    }

    //이벤트 리스너 설정
    private void initListeners() {
        //작성 완료 버튼
        completeButton.addActionListener(e->{
            onSubmitReview();
        });

        //취소 버튼
        cancelButton.addActionListener(e->{
            if(targetPackageId != null){
                navigateTo("catalog", 2);
            } //여기를 나중에 예약 확인 페이지 id로 수정
            else{
                navigateTo("catalog", 1);
            }
        });
    }


    //AppPage 계약상 필수:
    @Override
    public String getPageId() {
        //이 페이지 navigateTo 할 때 사용하는 ID
        return "reviewWrite";
    }

    //onPageStehown: 페이지가 표시될 때 호출되며, contextData를 받습니다.
    @Override
    public void onPageShown(Object contextData) {
        if (contextData instanceof Long) {
            this.targetPackageId = (Long) contextData;
            TourPackage tour = tourCatalog.getTour(targetPackageId.intValue());
            packageLabel.setText(tour.name);

        } else {
            this.targetPackageId = null;
            packageLabel.setText("오류: 잘못된 접근입니다. 예약 확인 페이지에서 다시 시도해주세요.");
            completeButton.setEnabled(false);
        }
    }

    //별점 로직
    private void createRatingStars(JPanel parent) {
        stars = new AppLabel[STAR_COUNT];
        rating = 0;

        for (int i = 0; i < STAR_COUNT; i++) {
            final int starValue = i + 1;        // 1~5
            AppLabel star = new AppLabel("☆");
            star.setFont(star.getFont().deriveFont(24f)); //필요시 크기 조절

            star.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    // hover: 일단 starValue까지 칠하지만, 범위는 0~5로 강제
                    paintStars(starValue);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    // 마우스를 치우면 기존 rating 기준으로 다시 칠해짐
                    paintStars(rating);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    // 클릭하면 rating 확정 (0~5 밖으로는 안 나가게 강제)
                    setRating(starValue);
                    paintStars(rating);
                }
            });

            stars[i] = star;
            parent.add(star);
        }
        paintStars(0);
    }

    private void paintStars(int count) {
        if (count < 0) count = 0;
        if (count > STAR_COUNT) count = STAR_COUNT;
        for (int i = 0; i < STAR_COUNT; i++) {
            stars[i].setText(i < count ? "★" : "☆");
        }
    }
    private void setRating(int value) {
        if (value < 0) value = 0;
        if (value > STAR_COUNT) value = STAR_COUNT; // STAR_COUNT = 5
        this.rating = value;
    }


    //리뷰 작성 완료 버튼 클릭 시 로직
    private void onSubmitReview() {
        // 1) 로그인 여부 확인
        Long userId = sessionManager.getCurrentUserId();
        if (userId == null) {
            JOptionPane.showMessageDialog(this, "로그인이 필요합니다.", "오류", JOptionPane.ERROR_MESSAGE);
            navigateTo("login"); // 임시: 로그인 페이지로 보냄
            return;
    }

        // 2) targetPackageId 유효성 확인
        if (targetPackageId == null) {
            JOptionPane.showMessageDialog(this, "잘못된 접근입니다. 다시 시도해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3) 입력값 읽기 (Long → int 변환)
        final int writerId;
        final int tourId;
        try {
            writerId = Math.toIntExact(userId);
            tourId = Math.toIntExact(targetPackageId);
        } catch (ArithmeticException ex) {
            JOptionPane.showMessageDialog(this, "ID 범위를 벗어났습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
        }

        final int selectedRating = this.rating;
        final String content = reviewArea.getText();
        // rating == 0이어도 그대로 ReviewManager에 던짐 → RATE_RANGE에서 걸리면 ResponseCode로 처리

        // 4) 로딩바 표시 + 버튼 비활성화
        loadingBar.setVisible(true);
        loadingBar.setIndeterminate(true);
        completeButton.setEnabled(false);

        // 5) 비동기 작업 실행
        runAsyncTask(
            // [백그라운드 스레드] ReviewManager 호출
            () -> reviewManager.review(writerId, tourId, selectedRating, content),

            // [성공 콜백 - EDT] ResponseCode를 받아 처리
            code -> {
                loadingBar.setIndeterminate(false);
                loadingBar.setVisible(false);
                completeButton.setEnabled(true);

                if (code == ReviewManager.ResponseCode.SUCCESS) {
                    JOptionPane.showMessageDialog(this, "리뷰가 등록되었습니다.");
                    publishEvent(new ReviewAddedEvent(tourId, writerId));

                    // 성공 시: 패키지 상세로 복귀
                    navigateTo("catalog", targetPackageId);
                } else {
                    // 실패 코드에 따른 에러 메시지
                    showErrorForResponseCode(code);
                }
            },

            // [실패 콜백 - EDT] 예외 발생 시 처리
            error -> {
                loadingBar.setIndeterminate(false);
                loadingBar.setVisible(false);
                completeButton.setEnabled(true);

                JOptionPane.showMessageDialog(
                    this,
                    "리뷰 등록 중 오류가 발생했습니다: " + error.getMessage(),
                    "오류",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        );
    }
    private void showErrorForResponseCode(ReviewManager.ResponseCode code) {
        String msg;
        msg = switch (code) {
            case INVALID_WRITER -> "작성자 정보를 확인할 수 없습니다.";
            case INVALID_TOUR -> "해당 투어 정보를 찾을 수 없습니다.";
            case ALREADY_REVIEWED -> "이미 이 투어에 대한 리뷰를 작성했습니다.";
            case DIDNT_TOUR -> "해당 투어를 이용한 예약 이력이 없습니다.";
            case RATE_OUT_OF_RANGE -> "평점이 허용 범위를 벗어났습니다. (" +
                ReviewManager.RATE_RANGE[0] + " ~ " + ReviewManager.RATE_RANGE[1] + ")";
            default -> "리뷰 등록에 실패했습니다. 잠시 후 다시 시도해주세요.";
        };

        JOptionPane.showMessageDialog(this, msg, "리뷰 등록 실패", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void onPageHidden() {
        // 페이지가 다른 화면으로 전환될 때 최소한으로 정리할 것들

        // 로딩 중이었으면 정리
        if (loadingBar != null) {
            loadingBar.setIndeterminate(false);
            loadingBar.setVisible(false);
        }

        // 완료 버튼 비활성화 상태라면 다시 켜두기
        if (completeButton != null) {
            completeButton.setEnabled(true);
        }

        rating = 0;
        if (reviewArea != null) {
            reviewArea.setText("");
        }
    }
}