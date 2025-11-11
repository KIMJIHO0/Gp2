/**
 * 등록된 페이지가 없을 때 기본으로 표시할 페이지
 */

package pages;

import ui_kit.AppPage;
import ui_kit.ServiceContext;
import ui_kit.AppLabel;
import ui_kit.AppLabel.LabelType;

import javax.swing.SwingConstants;
import java.awt.BorderLayout;


public class DefaultPage extends AppPage {

    public DefaultPage(ServiceContext context) {
        super(context);
        
        setLayout(new BorderLayout());
        AppLabel label = new AppLabel("페이지가 등록되지 않았습니다. App.java에서 페이지를 추가해주세요.", LabelType.TITLE);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label, BorderLayout.CENTER);
    }

    @Override
    public String getPageId() {
        return "default";
    }
}