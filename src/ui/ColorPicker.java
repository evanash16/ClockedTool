package ui;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ColorPicker extends UIComponent {

    private int radius, selX, selY;
    private BufferedImage colorWheel;

    public ColorPicker(int x, int y, int width, int height, UIComponentListener ul) {
        super(x, y, width, height, ul);
        this.radius = Math.min(getWidth(), getHeight()) / 2;
        this.selX = radius;
        this.selY = radius;
        colorWheel = new BufferedImage(2 * radius, 2 * radius, BufferedImage.TYPE_INT_ARGB);
        generateColorWheel();
    }

    private void generateColorWheel() {
        Graphics g2 = colorWheel.getGraphics();
        for (double hue = 0; hue < 360; hue += 0.01) { //for every angle
            for (double sat = 0; sat < 1; sat += 0.01) { //for every saturation value
                g2.setColor(convertHSVToRGB(hue, sat, 0.5));
                g2.drawOval((int) (radius + (radius * sat * Math.cos(Math.toRadians(hue))) - 1),  (int) (radius - (radius * sat * Math.sin(Math.toRadians(hue))) - 1), 2, 2);
            }
        }
    }

    private Color convertHSVToRGB(double hue, double saturation, double lightness) {
        double c = (1 - Math.abs(2 * lightness - 1)) * saturation;
        double x = c * (1 - Math.abs((hue / 60) % 2 - 1));
        double m = lightness - c / 2;

        if (hue >= 0 && hue < 60) {
            return new Color((int) ((c + m) * 255), (int) ((x + m) * 255), (int) (m * 255));
        } else if (hue >= 60 && hue < 120) {
            return new Color((int) ((x + m) * 255), (int) ((c + m) * 255), (int) (m * 255));
        } else if (hue >= 120 && hue < 180) {
            return new Color((int) (m * 255), (int) ((c + m) * 255), (int) ((x + m) * 255));
        } else if (hue >= 180 && hue < 240) {
            return new Color((int) (m * 255), (int) ((x + m) * 255), (int) ((c + m) * 255));
        } else if (hue >= 240 && hue < 300) {
            return new Color((int) ((x + m) * 255), (int) (m * 255), (int) ((c + m) * 255));
        } else if (hue >= 300 && hue < 360) {
            return new Color((int) ((c + m) * 255), (int) (m * 255), (int) ((x + m) * 255));
        } else {
            return Color.WHITE;
        }
    }

    public Color getColor() {
       return new Color(colorWheel.getRGB(this.selX, this.selY));
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(colorWheel, getX(), getY(), null);
        g.setColor(getColor());
        g.fillOval(this.getX() + selX - this.radius / 32, this.getY() + selY - this.radius / 32, this.radius / 16, this.radius / 16);
        g.setColor(Color.WHITE);
        g.drawOval(this.getX() + selX - this.radius / 32, this.getY() + selY - this.radius / 32, this.radius / 16, this.radius / 16);
    }

    @Override
    public void click(Point point) {
        int x = point.x - getX();
        int y = point.y - getY();
        if (Math.sqrt(Math.pow(x - radius, 2) + Math.pow(y - radius, 2)) <= radius) {
            this.selX = x;
            this.selY = y;
        }
    }
}
