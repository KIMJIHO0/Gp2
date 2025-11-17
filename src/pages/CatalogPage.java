/**
 * 여행 패키지 정보들을 목록화하여 보여주고, 좌측 메뉴도 함께 제공하는 페이지.
 * 좌측 메뉴는 BorderLayout.WEST로, 
 * 나머지는 CENTER로 분할하고 각각은 복잡하게 구성되어있기에 별도의 컴포넌트로 구현함.
 */


package pages;

import ui_kit.*;
import pages.component.Sidebar;

import java.awt.BorderLayout;


public class CatalogPage extends AppPage {
    public CatalogPage(ServiceContext ctx){
        super(new BorderLayout(), ctx);
        init();
    }

    @Override
    public String getPageId(){
        return "catalog";
    }

    private Sidebar sideNav;

    public void init(){
        // 사이드바 왼쪽에 추가
        sideNav = new Sidebar();
        sideNav.addMenu("여행 패키지 목록", null);
        sideNav.addMenu("예약 내역 확인", null);
        sideNav.addMenu("추천 패키지", null);
        sideNav.setLogoutCallback(null);

        add(sideNav, BorderLayout.WEST);
    }
}
