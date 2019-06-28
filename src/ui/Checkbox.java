package ui;

import java.awt.*;

public class Checkbox extends UIComponent {

    private boolean checked;

    public Checkbox(int x, int y, int width, int height, UIComponentListener ul) {
        super(x, y, width, height, ul);
    }

    public Checkbox(int x, int y, int width, int height, String label, UIComponentListener ul) {
        super(x, y, width, height, label, ul);
    }

    public Checkbox(int x, int y, int width, int height, String label, String id, UIComponentListener ul) {
        super(x, y, width, height, label, id, ul);
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(getX(), getY(), getWidth(), getHeight());

        g.setFont(new Font(null, Font.BOLD, getHeight()));
        int stringWidth = g.getFontMetrics().stringWidth(getLabel());
        g.drawString(getLabel(), getX() - stringWidth - 5, getY() + getHeight());

        g.setColor(Color.BLACK);
        g.drawRect(getX(), getY(), getWidth(), getHeight());

        if(checked) {
            g.drawLine(getX(), getY(), getX() + getWidth(), getY() + getHeight());
            g.drawLine(getX() + getWidth(), getY(), getX(), getY() + getHeight());
        }
    }

    public void click(Point point) {
        if (point.x > getX() && point.x < getX() + getWidth() && point.y > getY() && point.y < getY() + getHeight()) {
            checked = !checked;
            triggerEvent();
        }
    }

    public boolean isChecked() {
        return checked;
    }
    public void setDefault(boolean checked) {
        this.checked = checked;
    }
}
