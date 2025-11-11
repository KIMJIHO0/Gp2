// 컴포넌트 통합 테스트(By.Gemini)


package test;

// 이 파일은 default package에 있다고 가정합니다. (예: src/UIComponentTest.java)
// common.ui 패키지(src/common/ui/*.java)가 올바른 위치에 있어야 합니다.
import ui_kit.*; 

import javax.swing.*;
import java.awt.*;

/**
 * common.ui 패키지의 커스텀 컴포넌트들을 테스트하기 위한 갤러리 프레임입니다.
 * 이 클래스를 실행하면 모든 커스텀 컴포넌트를 시각적으로 확인할 수 있습니다.
 */
public class UIComponentTest extends JFrame {

    private AppProgressBar progressBar;
    private AppComboBox<String> comboBox;
    private AppTextField textField;
    private AppTextArea textArea;

    public UIComponentTest() {
        setTitle("ui_kit 컴포넌트 테스트");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 메인 패널 (전체 여백)
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- 1. 상단: Label, Button, TextField ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("기본 컴포넌트 (FlowLayout)"));
        
        // AppLabel 테스트 (타입별)
        topPanel.add(new AppLabel("일반(Normal) 라벨"));
        topPanel.add(new AppLabel("굵은(Bold) 라벨", AppLabel.LabelType.BOLD));
        
        // AppButton 테스트
        AppButton testButton = new AppButton("테스트 버튼");
        topPanel.add(testButton);

        // AppTextField 테스트
        topPanel.add(new AppLabel("텍스트 필드:", AppLabel.LabelType.BOLD));
        textField = new AppTextField(20);
        topPanel.add(textField);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // --- 2. 중앙: TitledPanel, TextArea, ComboBox ---
        JPanel centerPanel = new JPanel(new GridLayout(0, 2, 10, 10)); // 2열 그리드
        
        // AppTitledPanel 테스트 (내부에 AppLabel 배치)
        AppTitledPanel titledPanel = new AppTitledPanel("제목 패널 (AppTitledPanel)");
        titledPanel.setLayout(new BoxLayout(titledPanel, BoxLayout.Y_AXIS));
        titledPanel.add(new AppLabel("이 패널은 AppTitledPanel입니다."));
        titledPanel.add(new AppLabel("공통 폰트와 여백이 적용됩니다."));
        titledPanel.add(new AppLabel("타이틀 폰트: BOLD", AppLabel.LabelType.SMALL));
        
        centerPanel.add(titledPanel);

        // AppTextArea 테스트 (JScrollPane 내장)
        // AppTextArea는 JScrollPane을 상속받으므로 그 자체로 컴포넌트입니다.
        AppTitledPanel textPanel = new AppTitledPanel("텍스트 영역 (AppTextArea)");
        textPanel.setLayout(new BorderLayout());
        textArea = new AppTextArea(5, 20); // 5행 20열
        textArea.setText("AppTextArea는 JScrollPane을 상속받아\n" +
                         "자동으로 스크롤바를 포함합니다.\n" +
                         "setLineWrap(true)도 기본 적용됩니다.");
        textPanel.add(textArea, BorderLayout.CENTER);
        centerPanel.add(textPanel);

        // AppComboBox 테스트
        AppTitledPanel comboPanel = new AppTitledPanel("콤보 박스 (AppComboBox)");
        comboPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        String[] initialItems = {"아이템 1", "아이템 2", "아이템 3"};
        comboBox = new AppComboBox<>(initialItems);
        comboPanel.add(comboBox);

        AppButton addItemButton = new AppButton("항목 추가");
        comboPanel.add(addItemButton);
        centerPanel.add(comboPanel);

        // AppTitledPanel의 제목 변경 테스트
        centerPanel.add(new AppLabel("AppTitledPanel 제목 변경 테스트 ->"));
        testButton.addActionListener(e -> titledPanel.setTitle("제목 변경됨!"));
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // --- 3. 하단: ProgressBar 및 상호작용 버튼 ---
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        
        bottomPanel.add(new AppLabel("프로그레스 바 (AppProgressBar)", AppLabel.LabelType.BOLD));
        
        progressBar = new AppProgressBar(0, 100); // 0-100
        progressBar.setValue(10); // 초기값
        bottomPanel.add(progressBar);
        bottomPanel.add(Box.createVerticalStrut(10));

        AppButton fillButton = new AppButton("값 채우기 (TextField, TextArea)");
        bottomPanel.add(fillButton);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // --- 4. 이벤트 리스너 및 타이머 설정 ---
        
        // 콤보박스 아이템 추가 버튼
        addItemButton.addActionListener(e -> {
            int itemCount = comboBox.getItemCount();
            String newItem = "아이템 " + (itemCount + 1);
            comboBox.addItem(newItem);
            comboBox.setSelectedItem(newItem);
        });

        // 값 채우기 버튼
        fillButton.addActionListener(e -> {
            textField.setText("테스트 값!");
            textArea.append("\n[버튼 클릭] 텍스트 추가됨!");
        });

        // 프로그레스바 자동 증가 타이머 (2초마다 10%씩)
        Timer timer = new Timer(100, e -> {
            int currentValue = progressBar.getValue();
            int nextValue = currentValue + 1;
            if (nextValue > 100) {
                nextValue = 0; // 100% 도달 시 0으로 리셋
            }
            progressBar.setValue(nextValue);
        });
        timer.start();

        // --- 최종 프레임 설정 ---
        add(mainPanel);
        pack(); // 컴포넌트 크기에 맞게 창 크기 자동 조절
        setMinimumSize(new Dimension(600, 500));
    }

    /**
     * 메인 메서드
     */
    public static void main(String[] args) {
        // Look & Feel을 설정하면 더 좋습니다 (예: FlatLaf)
        // FlatLightLaf.setup(); 
        
        // EDT에서 GUI를 생성하고 표시합니다.
        SwingUtilities.invokeLater(() -> {
            UIComponentTest gallery = new UIComponentTest();
            gallery.setVisible(true);
        });
    }
}