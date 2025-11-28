package pages.component;

import java.awt.*;
import ui_kit.*;

/**
 * InfoRowPanel
 * "라벨: 값" 형태의 반복 UI를 캡슐화한 컴포넌트.
 *
 * 예:
 *   지역: 제주특별자치도
 *   기간: 3박 4일
 *   이동수단: -
 *
 * - labelText(좌측), valueText(우측) 둘 다 AppLabel 사용
 * - Layout: FlowLayout LEFT, 간격 6px 정도
 * - valueText는 나중에 setValue()로 갱신 가능
 */
public class InfoRowPanel extends AppPanel {

    private final AppLabel label;
    private final AppLabel value;

    public InfoRowPanel(String labelText, String valueText) {
        this(labelText, valueText, AppLabel.LabelType.BOLD, AppLabel.LabelType.NORMAL);
    }

    public InfoRowPanel(
            String labelText,
            String valueText,
            AppLabel.LabelType labelType,
            AppLabel.LabelType valueType
    ) {
        super(new FlowLayout(FlowLayout.LEFT, 6, 0));

        this.label = new AppLabel(labelText, labelType);
        this.value = new AppLabel(valueText, valueType);

        add(label);
        add(value);
    }

    /** 값만 변경할 때 */
    public void setValue(String newValue) {
        this.value.setText(newValue);
    }

    /** 라벨 텍스트를 바꿔야 할 경우 (잘 쓰진 않겠지만 필요하면 있음) */
    public void setLabelText(String newLabel) {
        this.label.setText(newLabel);
    }

    /** label과 value 컴포넌트를 가져올 필요가 있을 때 */
    public AppLabel getLabelComponent() { return label; }
    public AppLabel getValueComponent() { return value; }
}
