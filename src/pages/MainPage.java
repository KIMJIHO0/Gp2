package pages;

import ui_kit.*;
import manager.*;

public class MainPage extends AppPage {
    public static final String PAGE_ID = "mainpage";
    public String getPageId(){ return PAGE_ID; }

    public MainPage(ServiceContext ctx){
        super(ctx);
    }

    @Override
    public void onPageShown(Object contextData) {
        if(!context.get(SessionManager.class).isLoggedIn()){
            navigateTo(LoginPage.PAGE_ID);
            return;
        }
    }
}
