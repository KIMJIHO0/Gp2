package pages;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.BorderFactory;

import manager.UserManager2;
import manager.SessionManager;

import ui_kit.*;
import pages.component.AppNameLabel;


public class LoginPage extends AppPage {
    public static final String PAGE_ID = "login";
    @Override
    public String getPageId() { return PAGE_ID; }

    public LoginPage(ServiceContext ctx){
        super(ctx);
        initComponents();
    }

    @Override
    public void onPageShown(Object contextData) {
        if(context.get(SessionManager.class).isLoggedIn())
            navigateTo(MainPage.PAGE_ID);

        // 필드 비우기
        idField.setText("");
        pwField.setText("");
    }

    private void initComponents(){
        // 0. 배경색, 레이아웃, 패딩
        setBackground(UITheme.LOGIN_BG_COLOR);
        setBorder(UITheme.LOGIN_PADDING);
        setLayout(new BorderLayout(UITheme.LOGIN_HGAP, 0));

        // 1. 레이아웃 설정
        // 1-1. CENTER : 로그인 영역 (왼쪽)
        AppPanel center = new AppPanel();
        initCenter(center);
        add(center, BorderLayout.CENTER);

        // 1-2. EAST : 그림 영역 (오른쪽)
        AppPanel east = new AppPanel();
        initEast(east);
        add(east, BorderLayout.EAST);
    }

    AppTextField idField, pwField;
    private void initCenter(AppPanel center){
        // 0. 수직 배치 레이아웃 설정
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(UITheme.TRANSPARENT);
        center.add(Box.createVerticalGlue()); // 상단 여백(glue)

        // 1. 제목
        AppNameLabel titleLabel = new AppNameLabel();
        // titleLabel.setHorizontalPadding(100);
        try {
            titleLabel.scale(UITheme.LOGIN_TITLE_RATE);
        } catch(Exception e){ e.printStackTrace(); }
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(titleLabel);
        // 1-1. 제목-입력창 사이 간격
        center.add(Box.createVerticalStrut(UITheme.LOGIN_TITLE_GAP_BOTTOM));

        // 2. ID, PW 입력창
        // 2-1. ID
        idField = new AppTextField();
        idField.setPlaceholder("ID");
        setupInputStyle(idField);
        center.add(idField);
        center.add(Box.createVerticalStrut(UITheme.LOGIN_INPUT_GAP_BETWEEN)); // 비번 칸과의 갭
        // 2-2. PW
        pwField = new AppTextField();
        pwField.setPlaceholder("PASSWORD");
        setupInputStyle(pwField);
        center.add(pwField);
        
        // 3. 버튼 영역
        // 3-0. TOP 갭
        center.add(Box.createVerticalStrut(UITheme.LOGIN_BTN_GAP_TOP));
        // 3-1. login, signup 영역
        // 3-1-0. 레이아웃용 컨테이너
        AppPanel btnPanel = new AppPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        btnPanel.setBackground(UITheme.TRANSPARENT);
        btnPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPanel.setMaximumSize(new Dimension(UITheme.LOGIN_INPUT_WIDTH + 50, 40));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(0,48,0,0));
        // 3-1-1. 로그인
        AppButton loginBtn = new AppButton("login");
        setupButtonStyle(loginBtn);
        setupLoginCallback(loginBtn);
        btnPanel.add(loginBtn);
        // 3-1-2. 회원가입
        AppButton signupBtn = new AppButton("sign up");
        setupButtonStyle(signupBtn);
        setupSignupCallback(signupBtn);
        btnPanel.add(signupBtn);
        // 3-2. 버튼 등록
        center.add(btnPanel);

        // 4. 하단 여백
        center.add(Box.createVerticalGlue());
    }

    
    private void initEast(AppPanel east){
        east.setLayout(new BorderLayout());
        east.setBackground(UITheme.TRANSPARENT);

        // 중앙에 gif 이미지 배치
        try {
            ImageIcon gifIcon = new ImageIcon(getClass().getResource("/res/gif/login_intro_unoptimized.gif"));
            // AppLabel에 icon 받는 생성자 없음
            JLabel imgLabel = new JLabel(gifIcon);
            east.add(imgLabel, BorderLayout.CENTER);
        } catch(Exception e){
            AppLabel errorMsg = new AppLabel("img load failed");
            errorMsg.setHorizontalAlignment(AppLabel.CENTER);
            east.add(errorMsg, BorderLayout.CENTER);
        }
    }


    private void setupInputStyle(AppTextField field){
        // 1. 왼쪽 정렬
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        // 2. 크기 고정
        field.setPreferredSize(UITheme.LOGIN_INPUT_SIZE);
        field.setMaximumSize(UITheme.LOGIN_INPUT_SIZE);
        field.setMinimumSize(UITheme.LOGIN_INPUT_SIZE);
    }


    private void setupButtonStyle(AppButton btn){
        btn.setBackground(UITheme.TRANSPARENT);
        btn.setBorder(BorderFactory.createEmptyBorder(0,0,0,48));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);

        btn.setForeground(UITheme.LOGIN_BTN_FG_COLOR);
        btn.setFont(UITheme.LOGIN_BTN_FONT);
    }


    private void setupLoginCallback(AppButton btn){
        btn.addActionListener(e -> {
            var sm = context.get(SessionManager.class);
            var um = context.get(UserManager2.class);

            // 0. 이미 로그인 상태인데 현재 페이지일 경우
            if(sm.isLoggedIn()){
                System.err.println("Uncaught Error! the user " + sm.getCurrentUserId() + " is logged in.");
                return;
            }

            // id는 해싱하여 정수로 변환
            int id = idField.getText().hashCode();
            String pw = pwField.getText();

            // 로그인 검사(버튼)
            // 1-1. 없는 계정일 경우
            if(!um.exists(id)){
                AppOptionPane.showMessageDialog(this, "존재하지 않는 계정입니다.");
            }
            // 1-2. 비번 틀림
            else if(!um.checkPassword(id, pw)){
                AppOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다.");
            }
            // 비번 맞아서 로그인 성공
            else {
                sm.login((long)id);
                navigateTo(CatalogPage.ID, 0);
            }
        });
    }

    private void setupSignupCallback(AppButton btn){
        btn.addActionListener(e -> {
            navigateTo(SignupPage.PAGE_ID);
        });
    }
}