package ui;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Button extends UIComponent{

    public static final int LEFT_TO_RIGHT = 0, BOTTOM_TO_TOP = 1;
    private int textDirection = LEFT_TO_RIGHT;

    public Button(int x, int y, int width, int height, UIComponentListener ul) {
        super(x, y, width, height, ul);
    }

    public Button(int x, int y, int width, int height, String label, UIComponentListener ul) {
        super(x, y, width, height, label, ul);
    }

    public Button(int x, int y, int width, int height, String label, String id, UIComponentListener ul) {
        super(x, y, width, height, label, id, ul);
    }

    public Button(int x, int y, int width, int height, String label, int textDirection, UIComponentListener ul) {
        super(x, y, width, height, label, ul);
        this.textDirection = textDirection;
    }

    public Button(int x, int y, int width, int height, String label, String id, int textDirection, UIComponentListener ul) {
        super(x, y, width, height, label, id, ul);
        this.textDirection = textDirection;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(getX(), getY(), getWidth(), getHeight());

        g.setColor(Color.BLACK);
        g.drawRect(getX(), getY(), getWidth(), getHeight());

        g.setFont(new Font(null, Font.BOLD, (textDirection == LEFT_TO_RIGHT) ? 3 * getHeight() / 4 : 3 * getWidth() / 4));
        int stringWidth = g.getFontMetrics().stringWidth(getLabel());

        Graphics2D g2D = (Graphics2D) g;
        AffineTransform at = g2D.getTransform();
        if(textDirection == BOTTOM_TO_TOP) {
            g2D.rotate(-Math.PI / 2, getX() + (getWidth() / 2), getY() + (getHeight() / 2));
            stringWidth = g.getFontMetrics().stringWidth(getLabel());
            g2D.drawString(getLabel(), (getWidth() - stringWidth) / 2, (getHeight() + g2D.getFont().getSize()) / 2);
        } else {
            g2D.drawString(getLabel(), getX() + (getWidth() - stringWidth) / 2, getY() + getHeight() - 5);
        }
        g2D.setTransform(at);
    }

    @Override
    public void click(Point point) {
        if (point.x > getX() && point.x < getX() + getWidth() &&
                point.y > getY() && point.y < getY() + getHeight()) {
            triggerEvent();
        }
    }
}
