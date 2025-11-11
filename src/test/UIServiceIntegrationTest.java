
package test;

import adapters.memory.*;
import dao.*;
import manager.*;
import model.User;
import ui_kit.*;

import javax.swing.*;
import java.awt.*;

/**
 * UI 컴포넌트와 서비스(Manager) 계층의 통합 테스트 클래스입니다.
 * 이 테스트는 실제 UI 컴포넌트와 인메모리 DAO를 사용하는 Manager를 함께 사용하여
 * 전체 애플리케이션의 핵심 흐름(UI -> 서비스 -> 데이터)을 검증합니다.
 */
public class UIServiceIntegrationTest {

    public static void main(String[] args) {
        // 모든 GUI 작업은 Event Dispatch Thread(EDT)에서 실행되어야 합니다.
        SwingUtilities.invokeLater(() -> {
            // 1. DAO 인스턴스 준비 (인메모리 구현체 사용)
            UserDAO userDAO = new MemUserDAO();
            TourDAO tourDAO = new MemTourDAO();
            ReservationDAO reservationDAO = new MemReservationDAO();
            ReviewDAO reviewDAO = new MemReviewDAO();

            // 2. 테스트용 데이터 시드
            // 테스트를 위해 초기 사용자 데이터를 DAO에 추가합니다.
            userDAO.addUser(new User(1001, util.Passwords.hash("pw1234")));

            // 3. Manager 인스턴스 생성 (DAO 주입)
            UserManager userManager = new UserManager(userDAO);
            TourCatalog tourCatalog = new TourCatalog(tourDAO);
            ReservationManager reservationManager = new ReservationManager(reservationDAO, userDAO, tourDAO);
            ReviewManager reviewManager = new ReviewManager(reviewDAO, reservationDAO, userDAO, tourDAO);

            // 4. ServiceContext 생성 및 Manager 등록
            // AppPage에서 Manager들을 사용할 수 있도록 컨텍스트에 등록합니다.
            ServiceContext serviceContext = new ServiceContext();
            serviceContext.register(UserManager.class, userManager);
            serviceContext.register(TourCatalog.class, tourCatalog);
            serviceContext.register(ReservationManager.class, reservationManager);
            serviceContext.register(ReviewManager.class, reviewManager);

            // 5. 메인 프레임 및 테스트 패널 설정
            JFrame frame = new JFrame("UI-Service 통합 테스트");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);

            // ServiceContext를 주입하여 테스트 패널 생성
            IntegrationTestPanel testPanel = new IntegrationTestPanel(serviceContext);
            
            frame.add(testPanel);
            frame.setVisible(true);
        });
    }

    /**
     * 실제 UI 페이지(패널) 역할을 하는 테스트용 클래스입니다.
     * AppPage을 상속받아 ServiceContext를 통해 비즈니스 로직(Manager)에 접근합니다.
     */
    static class IntegrationTestPanel extends AppPage {

        private final AppTextField userIdField;
        private final AppButton searchButton;
        private final AppLabel resultLabel;

        public IntegrationTestPanel(ServiceContext context) {
            super(context); // 부모 클래스에 ServiceContext 전달
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

            // UI 컴포넌트 초기화
            add(new AppLabel("사용자 ID:", AppLabel.LabelType.BOLD));
            userIdField = new AppTextField(10);
            userIdField.setText("1001"); // 기본값 설정
            add(userIdField);

            searchButton = new AppButton("사용자 정보 조회");
            add(searchButton);

            resultLabel = new AppLabel("결과가 여기에 표시됩니다.", AppLabel.LabelType.MUTED);
            add(resultLabel);

            // 버튼 클릭 이벤트 리스너 설정
            searchButton.addActionListener(e -> onSearchButtonClicked());
        }

        /**
         * '사용자 정보 조회' 버튼이 클릭되었을 때 호출되는 메서드입니다.
         */
        private void onSearchButtonClicked() {
            String userIdText = userIdField.getText();
            if (userIdText.isBlank()) {
                resultLabel.setText("ID를 입력하세요.");
                resultLabel.setForeground(Color.RED);
                return;
            }

            try {
                int userId = Integer.parseInt(userIdText);
                
                // 비동기 작업을 위한 헬퍼 메서드 사용
                // 백그라운드 스레드에서 Manager 호출, UI 업데이트는 EDT에서 수행
                runAsyncTask(
                    () -> {
                        // 백그라운드 작업: UserManager에서 사용자 정보 조회
                        UserManager userManager = context.get(UserManager.class);
                        return userManager.getUser(userId);
                    },
                    user -> {
                        // 성공 콜백 (EDT에서 실행됨): UI 업데이트
                        if (user != null) {
                            resultLabel.setText("사용자 찾음: ID=" + user.id);
                            resultLabel.setForeground(UITheme.LABEL_FG_COLOR_DEFAULT);
                        } else {
                            resultLabel.setText("사용자를 찾을 수 없습니다: ID=" + userId);
                            resultLabel.setForeground(UITheme.LABEL_FG_COLOR_MUTED);
                        }
                    },
                    error -> {
                        // 실패 콜백 (EDT에서 실행됨): 에러 처리
                        resultLabel.setText("오류 발생: " + error.getMessage());
                        resultLabel.setForeground(Color.RED);
                        error.printStackTrace();
                    }
                );

            } catch (NumberFormatException ex) {
                resultLabel.setText("유효한 숫자 ID를 입력하세요.");
                resultLabel.setForeground(Color.RED);
            }
        }

        @Override
        public String getPageId() {
            return "integration-test-panel";
        }
    }
}
