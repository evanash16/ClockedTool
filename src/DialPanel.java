import ui.CornerButton;
import ui.Checkbox;
import ui.UIComponent;
import ui.UIComponentListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class DialPanel extends JPanel {

    private double angle = Math.PI, delAngle = 0.1, destination = Math.PI;
    private int radius;
    private String upString = "IN", downString = "OUT", text = "clocked...";
    private CornerButton viewHistory, minimize;
    private Checkbox freeSpin;

    private Timer updateTimer;

    public DialPanel(Container parent, UIComponentListener ul) {
        setSize(parent.getSize());

        radius = Math.min(getWidth(), getHeight()) / 2;
        viewHistory = new CornerButton(radius / 16, radius / 16, radius / 4, radius / 4, CornerButton.NORTH_WEST, "...", "history", ul);
        minimize = new CornerButton(getWidth() - radius / 16, getHeight() - radius / 16, radius / 4, radius / 4, CornerButton.SOUTH_EAST, "", "minimize", ul);
        freeSpin = new Checkbox(radius / 2 - radius / 5,  getHeight() - radius / 10 - radius / 32, radius / 10, radius / 10, "Spin!", ul);

        updateTimer = new Timer(10,  e -> repaint());
    }

    public void paint(Graphics g) {
        BufferedImage buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g2 = buffer.getGraphics();

        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(0, 0, getWidth(), getHeight());

        viewHistory.draw(g2);
        minimize.draw(g2);
        freeSpin.draw(g2);

        Graphics2D g2D = (Graphics2D) g2;
        AffineTransform transform = g2D.getTransform();
        Stroke stroke = g2D.getStroke();
        g2D.translate(getWidth() / 2, getHeight() / 2);

        moveTo(destination);
        g2D.rotate(angle);

        int strokeWeight = 10;
        g2D.setStroke(new BasicStroke(strokeWeight));

        g2.setColor(Color.GREEN);
        g2.fillArc(-radius, -radius, 2 * radius, 2 * radius, 0, 180);
        g2.setColor(g2.getColor().darker());
        g2.drawArc(-radius, -radius, (2 * radius), (2 * radius), 0, 180);

        g2.setColor(Color.RED);
        g2.fillArc(-radius, -radius, 2 * radius, 2 * radius, 180, 180);
        g2.setColor(g2.getColor().darker());
        g2.drawArc(-radius, -radius, (2 * radius), (2 * radius), 180, 180);

        g2.setFont(new Font(null, Font.BOLD, radius / 4));

        int textWidth = g2.getFontMetrics().stringWidth(text);
        g2.setColor(Color.WHITE);
        g2.drawString(text, -textWidth / 2, (g2.getFont().getSize()) / 2);

        g2.setFont(new Font(null, Font.BOLD, radius / 2));

        g2.setColor(Color.WHITE);
        int upWidth = g2.getFontMetrics().stringWidth(upString);
        g2.drawString(upString, -upWidth / 2, (-radius + g2.getFont().getSize()) / 2);

        g2D.rotate(Math.PI);

        g2.setColor(Color.WHITE);
        int downWidth = g2.getFontMetrics().stringWidth(downString);
        g2.drawString(downString, -downWidth / 2, (-radius + g2.getFont().getSize()) / 2);

        g2D.setTransform(transform);
        g2D.setStroke(stroke);

        g.drawImage(buffer, 0, 0, null);
    }

    public void moveTo(double destination) {
        if(Math.abs(destination - angle) > delAngle) {
            if(!updateTimer.isRunning())
                updateTimer.start();
            angle += delAngle;
        } else{
            if(updateTimer.isRunning())
                updateTimer.stop();
            angle = destination;
        }
    }

    public void flip() {
        destination = destination + Math.PI;
        moveTo(destination);
    }

    public boolean isDown() {
        return !isFreeSpinning() && angle % (2 * Math.PI) >= Math.PI;
    }
    public boolean isFreeSpinning() {
        return freeSpin.isChecked();
    }

    public void click(MouseEvent e) {
        minimize.click(e.getPoint());
        viewHistory.click(e.getPoint());
        freeSpin.click(e.getPoint());
    }

    public boolean clicked(MouseEvent e) {
        if(isVisible() && Math.sqrt(Math.pow((getWidth() / 2) - e.getX(), 2) + Math.pow((getHeight() / 2) - e.getY(), 2)) <= radius) {
            flip();
            if(!isFreeSpinning()) {
                return true;
            }
        }
        return false;
    }
}
