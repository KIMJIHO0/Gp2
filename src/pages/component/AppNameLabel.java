package pages.component;

import ui_kit.AppLabel;
import ui_kit.UITheme;

import java.awt.Font;
import javax.swing.BorderFactory;

public class AppNameLabel extends AppLabel {
    public AppNameLabel(String text){
        super(text);
    
        // 앱 제목의 고유한 기본 스타일 적용
        initStyle();
    }
    // 텍스트 미입력 시 기본 제목 띄움
    public AppNameLabel(){
        this(UITheme.APPNAME_TEXT);
    }


    // 크기, 폰트, 스타일(bold)등이 기본값으로 결정된다.
    public void initStyle(){
        setFont(new Font(UITheme.APPNAME_FONT, Font.BOLD, UITheme.APPNAME_SIZE));
        setForeground(UITheme.APPNAME_FG_COLOR);
        setBackground(UITheme.APPNAME_BG_COLOR);
        setHorizontalAlignment(AppLabel.CENTER);
        updateBorder();
    }

    /**
     * 글씨 배율 조절.
     * @param rate 확대할 배율. 1보다 작으면 작아진다.
     */
    public void scale(double rate) throws Exception {
        if(rate < 0.1)
            throw new IllegalArgumentException("rate 값은 최소 0.1 이상이어야 합니다.");
        Font current = getFont();
        setFont(new Font(
            current.getFontName(),
            current.getStyle(),
            (int)(current.getSize() * rate)
        ));
    }


    
    // 패딩
    private int pad_vertical = 0, pad_horizontal = 0;
    /**
     * 위아래 padding 조절
     * @param px 위아래에 동시에 삽입할 패딩 크기
     */
    public void setVerticalPadding(int px){
        if(px < 0)
            throw new IllegalArgumentException("패딩값은 0 또는 자연수여야 합니다.");
        pad_vertical = px;
        updateBorder();
    }
    /**
     * 좌우 padding 조절
     * @param px 양쪽에 동시에 삽입할 패딩 크기
     */
    public void setHorizontalPadding(int px){
        if(px < 0)
            throw new IllegalArgumentException("패딩값은 0 또는 자연수여야 합니다.");
        pad_horizontal = px;
        updateBorder();
    }
    private void updateBorder(){
        setBorder(BorderFactory.createEmptyBorder(
            pad_vertical,   // top
            pad_horizontal, // left
            pad_vertical,   // bottom
            pad_horizontal  // right
        ));
    }
}
