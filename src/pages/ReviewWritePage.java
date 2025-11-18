//pageID: reviewWrite
//아이디어: 예약 항목에 대한 정보가 필요한가?
//initListeners()부분의 취소 버튼 클릭 시 연결되는 id 수정 필요

package pages;
//v
import java.awt.*;
import javax.swing.*;
import manager.ReviewManager;
import manager.SessionManager;
import manager.TourCatalog;
import ui_kit.*;


public class ReviewWritePage extends AppPage {
    private TourCatalog tourCatalog;
    private ReviewManager reviewManager;
    private SessionManager sessionManager;

    // --- 2. 페이지 상태 필드 ---
    // 이 페이지가 어떤 패키지에 대한 리뷰인지 식별하는 ID (navigateTo로부터 전달받음)
    private Long targetPackageId;

    private int rating;
    //1~10으로 내부적 계산... 안될시 일단 1~5
    //별 클릭 시 값 변경

    //ui 컴포넌트 영역
    private AppLabel packageLabel; //리뷰를 작성할 패키지 이름
    private AppProgressBar loadingBar; //리뷰 저장 시 로딩 상태
    private AppTextArea reviewArea; //입력 영역
    private AppButton completeButton; //완료 버튼
    private AppButton cancelButton; //취소 버튼
    private AppButton[] starButtons; //별.


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

        //여기서 별점 체크- AppPanel을 div처럼 사용
        AppPanel ratingPanel = new AppPanel(new FlowLayout(FlowLayout.LEFT));
        ratingPanel.add(new AppLabel("별점: "));
        createRatingStars(ratingPanel);
        reviewPanel.add(ratingPanel, BorderLayout.NORTH);

        //리뷰 작성
        reviewArea = new AppTextArea();
        reviewPanel.add(reviewArea, BorderLayout.CENTER);

        loadingBar = new AppProgressBar();
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
                navigateTo("tourDetail", targetPackageId);
            } //여기를 나중에 예약 확인 페이지 id로 수정
            else{
                navigateTo("tourList");
            }
        });
    }


    //AppPage 계약상 필수:
    @Override
    public String getPageId() {
        //이 페이지 navigateTo 할 때 사용하는 ID
        return "reviewWrite";
    }

    /**
     * 2. (필수) 이 페이지가 화면에 보일 때마다 호출된다.
     * 여기서는:
     *  - 패키지 이름을 비동기로 조회해서 상단 라벨에 표시
     *  - 필요 시 초기 상태(별점, 리뷰 입력 영역) 정리
     */
    //onPageShown: 페이지가 표시될 때 호출되며, contextData를 받습니다.
    @Override
    public void onPageShown(Object contextData) {
        if (contextData instanceof Long) {
            this.targetPackageId = (Long) contextData;
            // TODO: 필요시 TourCatalog로 이름 로드
        } else {
            this.targetPackageId = null;
            packageLabel.setText("오류: 잘못된 접근입니다. 예약 확인 페이지에서 다시 시도해주세요.");
            completeButton.setEnabled(false);
        }
    }

    //별 만들기 로직 3개
    private void createRatingStars(JPanel parent) {
        starButtons = new AppButton[5];
        for (int i = 0; i < 5; i++) {
            final int value = i + 1;
            AppButton star = new AppButton("☆");

            star.addActionListener(e -> setRating(value));  // 이벤트 연결

            starButtons[i] = star;
            parent.add(star);
        }
        rating = 0;
        updateStarButtons();
    }
    private void setRating(int value) {
    this.rating = value;
    updateStarButtons();
    }
    private void updateStarButtons() {
        for (int i = 0; i < 5; i++) {
            starButtons[i].setText(i < rating ? "★" : "☆");
        }
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
                    navigateTo("tourDetail", targetPackageId);
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

        // 입력값을 매번 초기화할지 여부는 팀 정책에 따라 다르지만,
        // 일단 깔끔하게 비워두는 쪽으로 구현해 둔다.
        rating = 0;
        if (starButtons != null) {
            updateStarButtons();
        }

        if (reviewArea != null) {
            reviewArea.setText("");
        }

        // targetPackageId는 다음 onPageShown에서 새로 세팅되므로 그대로 둬도 되고,
        // 완전히 초기화하고 싶으면 아래 한 줄 추가:
        // targetPackageId = null;
    }

}
