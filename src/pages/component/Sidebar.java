// 사이드바 컴포넌트
/*
 * 구성:
 * 상단 OO항공(AppNameLabel)
 * 중단 메뉴 버튼들
 * 하단 로그아웃 버튼
 */

package pages.component;

import ui_kit.AppPanel;
import ui_kit.UITheme;
import ui_kit.AppButton;

import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.BorderFactory;

import java.util.ArrayList;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.Color;



public class Sidebar extends AppPanel {
    /**
     * 적절한 레이아웃을 위해 BoxLayout으로 고정
     */
    public Sidebar(){
        super();
        init();
    }

    // 메뉴 버튼들 관리용
    protected List<AppButton> menuItems = new ArrayList<AppButton>(); // 편집이 거의 없으니 동시성 문제 고려X
    private AppPanel menuPanel; // 메뉴 버튼들 컨테이너
    // 로그아웃 버튼
    private AppButton logoutButton = null;

    private void init(){
        // 0. 레이아웃 설정(위에서 아래로 쌓이도록)
        setLayout(new BorderLayout());
        // 0-1. 본인 스타일
        setPreferredSize(new Dimension(UITheme.SIDEBAR_WIDTH, 0));
        setBackground(UITheme.SIDEBAR_BG_COLOR);
        
        // 1. 프로그램 제목
        AppNameLabel titleLabel = new AppNameLabel();
        titleLabel.setBackground(UITheme.SIDEBAR_BG_COLOR);
        titleLabel.setVerticalPadding(10);
        titleLabel.setPreferredSize(new Dimension(UITheme.SIDEBAR_WIDTH, 180)); // 가로세로 수동 설정
        add(titleLabel, BorderLayout.NORTH); // 상단 배치

        // 2. 메뉴바(컨테이너)
        menuPanel = new AppPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS)); // 세로 쌓기
        menuPanel.setBackground(new Color(0,0,0,0));
        add(menuPanel, BorderLayout.CENTER);

        // 3. 로그아웃 버튼
        logoutButton = new AppButton("로그아웃");
        styleMenuButton(logoutButton, false);
        logoutButton.setForeground(UITheme.ACCOUNT_BUTTON_FG_COLOR);
        logoutButton.setPreferredSize(new Dimension(UITheme.SIDEBAR_WIDTH, 90));
        add(logoutButton, BorderLayout.SOUTH);
    }

    /**
     * 메뉴 버튼 추가
     * @param name 버튼 이름
     * @param method 버튼 눌렸을 때 기능(콜백)
     */
    public void addMenu(String name, ActionListener method){
        AppButton item = new AppButton(name);
        styleMenuButton(item, true);

        // 기본 마우스 리스너 제거(스타일에 방해됨)
        item.removeMouseListener(item.getMouseListeners()[item.getMouseListeners().length-1]);

        item.addActionListener(e -> {
            setSelected(item);
            // 사이드바 밖에서 일어나는 처리(페이지 단위 이동이나 우측 card 교체 등) 위임
            if(method != null)
                method.actionPerformed(e);
        });

        menuItems.add(item);
        menuPanel.add(item);

        if(menuItems.size() == 1)
            setSelected(item);
    }

    /**
     * 선택 상태로 만들기; 스타일만 변경!
     * @param item 선택할 메뉴
     */
    private void setSelected(AppButton item){
        for(AppButton button : menuItems){
            if(button == item){
                button.setOpaque(true); // 불투명도 활성화
                button.setBackground(UITheme.SIDEBAR_MENU_SELECTED_COLOR);
            } else {
                styleTransparentButton(button);
            }
        }
    }

    /**
     * 메뉴버튼 기본 스타일 설정
     */
    private void styleMenuButton(AppButton button, boolean manageSelection){
        button.setFont(UITheme.SIDEBAR_MENU_FONT);
        button.setForeground(UITheme.SIDEBAR_MENU_FONT_COLOR);
        button.setBackground(UITheme.SIDEBAR_MENU_DEFAULT_COLOR);
        button.setHorizontalAlignment(AppButton.CENTER);

        // padding
        int[] pad = UITheme.SIDEBAR_MENU_ITEM_PADDING;
        button.setBorder(BorderFactory.createEmptyBorder(pad[0], pad[1], pad[2], pad[2]));

        // width=100%
        int prefHeight = button.getPreferredSize().height;
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, prefHeight)); // 가로로 꽉 채우기

        // swing 기본 스타일 덮어쓰기 문제 방지
        styleTransparentButton(button);
    }

    /**
     * 버튼 배경 완전 투명화(사실상 로그아웃용)
     */
    private void styleTransparentButton(AppButton button){
        button.setOpaque(false);
        button.setBackground(UITheme.SIDEBAR_MENU_DEFAULT_COLOR);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false); // 윤곽선 제거
    }

    /**
     * 로그아웃 시 콜백 설정
     */
    private ActionListener logoutCallback = null;
    public void setLogoutCallback(ActionListener onLogout){
        if(logoutCallback != null)
            logoutButton.removeActionListener(logoutCallback);
        logoutCallback = onLogout;
        logoutButton.addActionListener(onLogout);
    }
}
