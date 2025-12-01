package pages.component;

import ui_kit.AppButton;
import ui_kit.AppPanel;
import ui_kit.UITheme;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;

/**
 * 예약 내역 항목을 보여주는 배너 컴포넌트.
 * TourBanner를 상속하여 텍스트 및 썸네일 구조를 재사용하고,
 * EAST 영역의 버튼을 '상세보기'와 '리뷰/취소' 버튼 두 개로 확장합니다.
 */
public class ReservationBanner extends TourBanner {

    // 새로운 버튼을 위한 필드
    private AppButton secondaryButton;

    /**
     * @param title 여행 제목
     * @param region 지역
     * @param duration 기간
     * @param price 가격
     * @param rating 평점
     * @param isCompleted 예약 완료/사용 여부 (true: 리뷰, false: 취소)
     * @param detailListener '상세보기' 버튼 클릭 리스너
     * @param secondaryListener '리뷰' 또는 '취소' 버튼 클릭 리스너
     */
    public ReservationBanner(
        String title, 
        String region, 
        String duration, 
        String price, 
        String headcount,
        String rating,
        boolean isCompleted,
        ActionListener detailListener,
        ActionListener secondaryListener
    ) {
        // 부모 생성자 호출 (TourBanner의 모든 초기화, BorderLayout, 텍스트 설정 등 수행)
        super(title, region, duration, price, headcount, rating);
        
        // 부모 클래스에서 생성된 '상세보기' 버튼에 리스너 등록
        // detailButton 필드는 TourBanner의 initEast에서 생성되었지만 protected 메서드에 의해 호출되므로
        // 이 시점에서는 이미 존재합니다. (클래스 내부 필드 detailButton을 직접 사용)
        if (detailListener != null) {
            super.addDetailButtonListener(detailListener);
        }
        
        // 추가된 2순위 버튼에 리스너 등록
        if (secondaryListener != null && secondaryButton != null) {
            secondaryButton.addActionListener(secondaryListener);
        }
        
        // isCompleted 값에 따라 2순위 버튼의 텍스트와 스타일 설정
        setSecondaryButtonState(isCompleted);
    }

    /**
     * 2순위 버튼의 텍스트와 색상을 설정합니다.
     * @param isCompleted 예약 완료/사용 여부
     */
    public void setSecondaryButtonState(boolean isCompleted) {
        if (secondaryButton == null) return;

        if (isCompleted) {
            // 완료된 예약: 리뷰 작성 가능
            secondaryButton.setText("리뷰");
            secondaryButton.setBackground(UITheme.RESERVE_BANNER_RESERVE_BTN_COLOR); // 파란색
        } else {
            // 미완료 예약: 취소 가능
            secondaryButton.setText("취소");
            secondaryButton.setBackground(UITheme.RESERVE_BANNER_CANCEL_BTN_COLOR); // 붉은색
        }
    }

    /**
     * TourBanner의 initEast() 메서드를 오버라이드하여 
     * 버튼 두 개를 수직으로 배치합니다.
     */
    @Override
    protected void initEast() {
        // GridBagLayout을 사용하여 버튼들을 수직 중앙 정렬하고 간격을 줍니다.
        AppPanel buttonWrapper = new AppPanel(new GridBagLayout());
        buttonWrapper.setBackground(UITheme.TRANSPARENT);

        // 1. 상세보기 버튼 (부모 필드 detailButton)
        detailButton = new AppButton("상세보기", false);
        detailButton.setBackground(UITheme.TOUR_BANNER_DETAILBTN_COLOR);
        // 버튼 크기를 통일하기 위해 선호 크기 설정
        detailButton.setPreferredSize(UITheme.TOUR_BANNER_EAST_BTN_SIZE); 
        
        // 2. 2순위 버튼 (리뷰 또는 취소)
        secondaryButton = new AppButton(" ", false); // 초기 텍스트는 생성자에서 설정됨
        secondaryButton.setPreferredSize(UITheme.TOUR_BANNER_EAST_BTN_SIZE);
        
        // GridBagConstraints 설정
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; // 첫 번째 열
        gbc.insets = new Insets(0, 0, 5, 0); // 버튼 사이에 수직 갭 5px
        gbc.anchor = GridBagConstraints.CENTER;

        // 1. 상세보기 버튼 (위쪽)
        gbc.gridy = 0;
        buttonWrapper.add(detailButton, gbc);

        // 2. 2순위 버튼 (아래쪽)
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 0, 0, 0); // 위쪽과 분리하기 위한 갭
        buttonWrapper.add(secondaryButton, gbc);

        // 완성된 래퍼 패널을 EAST에 추가
        add(buttonWrapper, BorderLayout.EAST);
    }
}