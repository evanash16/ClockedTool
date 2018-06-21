package ui;

import java.awt.*;

public abstract class UIComponent {

    private int x, y, width, height;
    private String label = "", id = "";
    private UIComponentListener ul;

    public UIComponent(int x, int y, int width, int height, UIComponentListener ul) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.ul = ul;
    }

    public UIComponent(int x, int y, int width, int height, String label, UIComponentListener ul) {
        this(x, y, width, height, ul);
        this.label = label;
    }

    public UIComponent(int x, int y, int width, int height, String label, String id, UIComponentListener ul) {
        this(x, y, width, height, label, ul);
        this.id = id;
    }

    public abstract void draw(Graphics g);
    public abstract void click(Point point);

    public String getLabel() {return this.label;}
    public void setLabel(String label) {this.label = label;}

    public String getId() {return this.id;}
    public void setId(String id) {this.id = id;}

    public int getX() {return this.x;}
    public void setX(int x) {this.x = x;}

    public int getY() {return this.y;}
    public void setY(int y) {this.y = y;}

    public int getWidth() {return this.width;}
    public void setWidth(int width) {this.width = width;}

    public int getHeight() {return this.height;}
    public void setHeight(int height) {this.height = height;}

    public Point getPosition() {return new Point(this.x, this.y);}
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Dimension getSize() {return new Dimension(this.width, this.height);}
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void triggerEvent() {
        this.ul.onUIComponentEvent(this);
    }
}
