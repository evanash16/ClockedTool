package ui;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Punch extends UIComponent {

    private String punchTime;
    private boolean deleteMode, delete;

    public Punch(String punchTime, int x, int y, int height, UIComponentListener ul) {
        super(x, y, 0, height, ul);
        this.punchTime = punchTime;

        Graphics fontGraphics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).getGraphics();
        fontGraphics.setFont(new Font(null, Font.BOLD, getHeight()));
        FontMetrics metrics = fontGraphics.getFontMetrics();
        setWidth(metrics.stringWidth(punchTime));
    }

    @Override
    public void draw(Graphics g) {
        if(!deleteMode) {
            g.setColor(Color.WHITE);
            g.setFont(new Font(null, Font.BOLD, getHeight()));
            g.drawString(punchTime, getX(), getY() + getHeight() - 5);
        } else {
            g.setColor(Color.WHITE);
            g.fillRect(getX(), getY(), getWidth(), getHeight());

            g.setColor(Color.RED);
            g.setFont(new Font(null, Font.BOLD, getHeight()));
            g.drawRect(getX(), getY(), getWidth(), getHeight());
            g.drawString(punchTime, getX() + getHeight() / 2, getY() + getHeight() - 5);

            g.setColor(Color.RED);
            g.fillRect(getX(), getY(), getHeight() / 2, getHeight());

            g.setColor(Color.BLACK);
            g.drawLine(getX() + getHeight() / 8, getY() + getHeight() / 4 + getHeight() / 8, getX() + getHeight() / 2 - getHeight() / 8, getY() + 3 * getHeight() / 4 - getHeight() / 8);
            g.drawLine(getX() + getHeight() / 2 - getHeight() / 8, getY() + getHeight() / 4 + getHeight() / 8, getX() + getHeight() / 8, getY() + 3 * getHeight() / 4 - getHeight() / 8);
        }
    }

    @Override
    public void click(Point point) {
        if (point.x > getX() && point.x < getX() + getWidth() &&
                point.y > getY() && point.y < getY() + getHeight()) {
            if(!deleteMode) {
                deleteMode = true;
            } else if(point.x < getX() + getHeight() / 2) {
                delete = true;
            } else {
                deleteMode = false;
            }
        } else {
            deleteMode = false;
        }
    }

    public String getPunchTime() {return this.punchTime;}

    public int getWidth() {
        if(!deleteMode) {
            return super.getWidth();
        } else {
            return super.getWidth() + getHeight() / 2;
        }
    }

    public boolean isDeleted() {return this.delete;}
}
