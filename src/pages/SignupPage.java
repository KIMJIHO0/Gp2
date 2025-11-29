// 최초 회원가입 시 이름, 성별, 나이 등 추가 상세정보를 입력받기 위한 페이지
/** Layout
 * BorderLayout
 *  - CENTER(상단) : BoxLayout (Vertical=Y_AXIS)
 *    - 제목 
 *    - ID 필드
 *    - PW 필드
 *    - 상세정보 Label
 *    - 이름 필드
 *    - 성별 필드
 *    - 나이 필드
 *    - signup 버튼 -> 여행 패키지 목록으로 이벤트 처리
 *  - BOTTOM(하단) : FlowLayout Panel -> Left
 *    - 로그인 페이지로 돌아가기(버튼) : flow start
 */

package pages;

import pages.component.AccountTextField;
import pages.component.AppNameLabel;
import ui_kit.*;

import model.User2;
import manager.UserManager2;
import manager.SessionManager;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;


import java.util.regex.Pattern;


public class SignupPage extends AppPage {
    public static String PAGE_ID = "signup";
    @Override
    public String getPageId() { return PAGE_ID; }

    // 비번 형식 조건
    // 숫자, 영어, *^_로만 구성된 8~12글자
    public static Pattern pw_pattern = Pattern.compile("^[0-9A-Za-z*^_]{8,12}$");


    // 입력 필드들
    private AccountTextField idField, pwField, nameField, sexField, ageField;


    public SignupPage(ServiceContext ctx){
        super(ctx);
    
        init();
    }

    private void init(){
        // 0. 기본 레이아웃 = Border
        setLayout(new BorderLayout());
        
        // 1. CENTER : 입력 핃르
        initCenter();

        // 2. BOTTOM : 로그인 페이지로 돌아가기
        initBottom();
    }

    private void initCenter(){
        // 0. 컨테이너
        var container = new AppPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        // 1. 제목
        container.add(Box.createVerticalStrut(106)); // 마진
        var title = new AppNameLabel();
        title.setAlignmentX(CENTER_ALIGNMENT);
        container.add(title);

        // 2. ID필드
        container.add(Box.createVerticalStrut(30)); // 마진
        idField = new AccountTextField();
        idField.setPlaceholder("ID");
        idField.setAlignmentX(CENTER_ALIGNMENT);
        container.add(idField);

        // 3. PW필드
        container.add(Box.createVerticalStrut(15)); // 마진
        pwField = new AccountTextField();
        pwField.setPlaceholder("Password");
        pwField.setAlignmentX(CENTER_ALIGNMENT);
        container.add(pwField);
        
        // 4. 상세정보 레이블
        container.add(Box.createVerticalStrut(45)); // 마진
        var detail_label = new AppLabel("상세정보");
        detail_label.setForeground(UITheme.LOGIN_BTN_FG_COLOR);
        detail_label.setFont(
            new Font("Sagoe UI", Font.BOLD, 20)
        );
        // 왼쪽에 붙도록
        detail_label.setAlignmentX(CENTER_ALIGNMENT);
        detail_label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 183));
        container.add(detail_label);
        
        // 5. 이름 필드
        container.add(Box.createVerticalStrut(5)); // 마진
        nameField = new AccountTextField();
        nameField.setPlaceholder("이름");
        nameField.setAlignmentX(CENTER_ALIGNMENT);
        container.add(nameField);

        // 6. 성별 필드
        container.add(Box.createVerticalStrut(7)); // 마진
        sexField = new AccountTextField();
        sexField.setPlaceholder("성별(남/여)");
        sexField.setAlignmentX(CENTER_ALIGNMENT);
        container.add(sexField);

        // 7. 나이 필드
        container.add(Box.createVerticalStrut(7)); // 마진
        ageField = new AccountTextField();
        ageField.setPlaceholder("나이");
        ageField.setAlignmentX(CENTER_ALIGNMENT);
        container.add(ageField);
        
        // 8. 가입 버튼
        container.add(Box.createVerticalStrut(23)); // 마진
        var signup_btn = new AppButton("sign up");
        setupButtonStyle(signup_btn);
        signup_btn.addActionListener(signup_listner);
        signup_btn.setAlignmentX(CENTER_ALIGNMENT);
        container.add(signup_btn);

        add(container, BorderLayout.CENTER);
    }

    
    // BoxLayout에 padding 넣고 로그인 페이지로 버튼 삽입.
    private void initBottom(){
        // 0. 레이아웃용 패널
        var container = new AppPanel();
        container.setLayout(new FlowLayout(FlowLayout.LEFT));
        container.setBorder(BorderFactory.createEmptyBorder(33, 28, 33, 28));

        // 1. 로그인 페이지로 복귀 버튼
        var return_login_btn = new AppButton("←로그인 페이지로");
        setupButtonStyle(return_login_btn);
        return_login_btn.setFont(
            new Font("Sagoe UI", Font.PLAIN, 20)
        );
        return_login_btn.addActionListener(e -> {
            // 기존 입력 전부 초기화
            if(idField != null)
                idField.setText("");
            if(pwField != null)
                pwField.setText("");
            if(nameField != null)
                nameField.setText("");
            if(sexField != null)
                sexField.setText("");
            if(ageField != null)
                ageField.setText("");

            navigateTo(LoginPage.PAGE_ID);
        });
        container.add(return_login_btn);

        // 2. 밑에 추가
        add(container, BorderLayout.SOUTH);
    }


    private void setupButtonStyle(AppButton btn){
        btn.setBackground(UITheme.TRANSPARENT);
        btn.setBorder(BorderFactory.createEmptyBorder(0,0,0,48));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);

        btn.setForeground(UITheme.LOGIN_BTN_FG_COLOR);
        btn.setFont(UITheme.LOGIN_BTN_FONT);
    }


    // 가입 시 리스너
    private ActionListener signup_listner = _ -> {
        var sm = context.get(SessionManager.class);
        // var um = context.get(UserManager2.class);
        var um = context.get(UserManager2.class);
    
        // 0. 안전상 로그인 여부 다시 한 번 체크
        if(sm.isLoggedIn()){
            System.err.println("Unexpected Exception! " + sm.getCurrentUserId() + "가 로그인되어있습니다.");
            return;
        }

        // 1. 정보 가져오기
        int uid = idField.getText().hashCode();
        String pw = pwField.getText();
        String name = nameField.getText();
        String sex = sexField.getText();
        int age = 0; // 밑에서 유효성 검사하면서 입력 받기(try문 필요)
        
        // 2. ID 유효성 및 계정 존재 여부 확인
        if(idField.getText().isBlank()){
            AppOptionPane.showMessageDialog(this, "ID를 입력해주세요!");
            return;
        }
        if(um.exists(uid)){
            AppOptionPane.showMessageDialog(this, "이미 가입된 ID입니다!");
            return;
        }

        // 3. 비밀번호 유효성 검사
        if(!pw_pattern.matcher(pw).matches()){
            AppOptionPane.showMessageDialog(this, "비밀번호는 숫자/영문/*^_로만 8~12로 구성해주세요!");
            return;
        }

        // 4. 상세정보 유효성 검사
        // 4-1. 이름
        if(name == null || name.isBlank()){
            AppOptionPane.showMessageDialog(this, "이름은 공백으로 둘 수 없습니다!");
            return;
        }
        // 4-2. 성별
        User2.Sex sex_code = null;
        if(!(sex.equals("남") || sex.equals("여"))){
            AppOptionPane.showMessageDialog(this, "성별은 남/여로 입력해주세요!");
            return;
        }
        sex_code = sex.equals("남")? User2.Sex.MALE: User2.Sex.FEMALE;
        // 4-3. 연령
        try{
            age = Integer.parseInt(ageField.getText());
        } catch(NumberFormatException e){
            System.err.println(e);
            AppOptionPane.showMessageDialog(this, "나이는 정수로 입력해주세요!");
            return;
        }
        if(age < 19){
            AppOptionPane.showMessageDialog(this, "미성년자는 가입할 수 없습니다.");
            return;
        }

        // 5. 가입 요청
        if(!um.register(uid, pw, name, age, sex_code)){
        // if(!um.register(uid, pw)){
            AppOptionPane.showMessageDialog(this, "어라, 이게 뜨면 안되는데......");
            return;
        }
        // 5-1. 존재 여부 철저히 확인
        assert um.exists(uid);

        // 6. 필드 비우기
        clearFields();

        // 7. 세션 등록하고 메인페이지로 이동
        sm.login((long)uid);
        navigateTo(CatalogPage.ID, 1);
    };

    private void clearFields(){
        idField.setText("");
        pwField.setText("");
        nameField.setText("");
        sexField.setText("");
        ageField.setText("");
    }
}
