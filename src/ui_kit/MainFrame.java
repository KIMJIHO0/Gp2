package ui_kit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.BorderLayout;
import javax.swing.JComponent;
import java.util.HashMap;
import java.util.Map;

import config.Constants;

/**
 * 애플리케이션의 메인 셸(Shell)
 * 주로 CardLayout을 사용하여 AppPage들을 교체하는 페이지 교체 및 관련 이벤트 관리 담당
 */
public class MainFrame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel mainPanel; // AppPage들이 올라갈 컨테이너
    private final Map<String, AppPage> pages = new HashMap<>();
    private AppPage currentPage = null;

    public MainFrame() {
        setTitle(Constants.UI_TITLE);                     // 화면 이름
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   // 닫기 시 작업
        // setSize(Constants.UI_WIDTH, Constants.UI_HEIGHT); // 화면 크기
        setLocationRelativeTo(null);                    // 화면 초기 위치(null=가운데)

        // 레이아웃 설정
        cardLayout = new CardLayout();                    // 페이지 전환용 CardLayout
        mainPanel = new JPanel(cardLayout);               // CardLayout 적용하기 위한 패널
        mainPanel.setBackground(UITheme.PANEL_BACKGROUND_COLOR);
        add(mainPanel, BorderLayout.CENTER);              // 프레임에 메인패널 추가
        // 화면 크기를 content 영역에 맞추기
        mainPanel.setPreferredSize(new Dimension(UITheme.WINDOW_WIDTH, UITheme.WINDOW_HEIGHT));
        pack(); // setSize대신 호출하여 맞춤 설정
        // 기본 Border 제거
        var contentPane = getContentPane();
        if(contentPane instanceof JComponent)
            ((JComponent) contentPane).setBorder(null);
        
        // JMenuBar, JToolBar 등 공통 컴포넌트 초기화 가능
        // JMenuBar menuBar = createGlobalMenuBar();
        // setJMenuBar(menuBar);

        // [Important] 페이지 전환 이벤트 구독
        AppEventBus.getInstance()
            .subscribe(PageChangeEvent.class,
                event -> showPage(event.getPageId(), event.getContextData())
            );
    }

    /**
     * Main.java 호출용
     * 각 페이지(패널)을 메인 프레임에 등록
     * @param panel 등록할 AppPage 인스턴스
     */
    public void addPage(AppPage panel) {
        String pageId = panel.getPageId();
        if (pageId == null || pageId.isEmpty())
            throw new IllegalArgumentException("page id는 null 또는 빈 문자열이 아니어야 합니다.");
        if (pages.containsKey(pageId))
            throw new IllegalArgumentException("이미 등록된 page id입니다. 확인해주세요.");
        pages.put(pageId, panel);
        mainPanel.add(panel, pageId);
    }

    /**
     * 실제 CardLayout을 전환하고, Panel의 생명주기 메서드(onPageShown, onPageHidden) 호출
     * @param pageId 표시할 페이지 ID
     * @param contextData 새 페이지로 전달할 데이터
     */
    public void showPage(String pageId, Object contextData) {
        if (!pages.containsKey(pageId)) {
            System.err.println("Page not found: " + pageId);
            return;
        }

        // 1. 기존 페이지 숨김처리
        if (currentPage != null)
            currentPage.onPageHidden();

        // 2. CardLayout을 사용하여 페이지 전환(기존 페이지 무관)
        // 이때, 단순히 display 여부의 변경이 아닌 실제 GUI 객체 렌더링 및 관련 프레임 처리가 이뤄지기에
        // show 먼저 하고 나서 onPageShown과 같은 처리해주는 게 안전
        cardLayout.show(mainPanel, pageId);

        // 3. 새 페이지를 현재 페이지로 설정
        currentPage = pages.get(pageId);

        // 4. 새 페이지 로드 (데이터 로드 트리거)
        currentPage.onPageShown(contextData);
    }
}