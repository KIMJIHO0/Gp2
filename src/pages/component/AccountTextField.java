// 로그인/회원가입 페이지용 공통 입력 필드
// 스타일만 통일
package pages.component;

import java.awt.Component;
import java.awt.Font;

import ui_kit.AppTextField;
import ui_kit.UITheme;

public class AccountTextField extends AppTextField {
    public AccountTextField(){
        super();
        initStyle();
    }

    private void initStyle(){
        // 1. 왼쪽 정렬
        setAlignmentX(Component.CENTER_ALIGNMENT);
        // 2. 크기 고정
        setPreferredSize(UITheme.LOGIN_INPUT_SIZE);
        setMaximumSize(UITheme.LOGIN_INPUT_SIZE);
        setMinimumSize(UITheme.LOGIN_INPUT_SIZE);

        // 3. 글자 크기 설정
        setFont(new Font("Sagoe UI", Font.PLAIN, 20));
    }
}
