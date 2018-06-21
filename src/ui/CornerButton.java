package ui;

import java.awt.*;

public class CornerButton extends UIComponent {

    public static final int NORTH_EAST = 0, SOUTH_EAST = 1, SOUTH_WEST = 2, NORTH_WEST = 3;
    private int orientation;
    private Polygon collision;

    public CornerButton(int x, int y, int width, int height, UIComponentListener ul) {
        super(x, y, width, height, ul);
        this.orientation = NORTH_WEST;
        collision = getCollision();
    }

    public CornerButton(int x, int y, int width, int height, int orientation, UIComponentListener ul) {
        super(x, y, width, height, ul);
        this.orientation = orientation;
        collision = getCollision();
    }

    public CornerButton(int x, int y, int width, int height, int orientation, String label, UIComponentListener ul) {
        super(x, y, width, height, label, ul);
        this.orientation = orientation;
        collision = getCollision();
    }

    public CornerButton(int x, int y, int width, int height, int orientation, String label, String id, UIComponentListener ul) {
        super(x, y, width, height, label, id, ul);
        this.orientation = orientation;
        collision = getCollision();
    }

    public Polygon getCollision() {
        switch (orientation) {
            case NORTH_EAST:
                return new Polygon(new int[]{getX(), getX(), getX() - getWidth()},
                                   new int[]{getY() + getHeight(), getY(), getY()}, 3);
            case SOUTH_EAST:
                return new Polygon(new int[]{getX(), getX(), getX() - getWidth()},
                                   new int[]{getY() - getHeight(), getY(), getY()}, 3);
            case SOUTH_WEST:
                return new Polygon(new int[]{getX(), getX(), getX() + getWidth()},
                                   new int[]{getY() - getHeight(), getY(), getY()}, 3);
            default:
                return new Polygon(new int[]{getX(), getX(), getX() + getWidth()},
                                   new int[]{getY() + getHeight(), getY(), getY()}, 3);
        }
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillPolygon(collision);

        g.setColor(Color.BLACK);
        g.drawPolygon(collision);

        g.setFont(new Font(null, Font.BOLD, getHeight() / 2));
        int stringWidth = g.getFontMetrics().stringWidth(getLabel());
        g.drawString(getLabel(), getX() + (getWidth() - stringWidth) / 4, getY() + getHeight() / 2 - 5);
    }

    @Override
    public void click(Point point) {
        if(collision.contains(point)) {
            triggerEvent();
        }
    }
}
