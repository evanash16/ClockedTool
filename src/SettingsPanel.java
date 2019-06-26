import ui.ColorPicker;
import ui.CornerButton;
import ui.Button;
import ui.UIComponent;
import ui.UIComponentListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class SettingsPanel extends JPanel implements UIComponentListener {

    private CornerButton back;
    private ColorPicker picker;
    private Button setIn, setOut, reset;
    public static Color IN_COLOR = Color.GREEN, OUT_COLOR = Color.RED;

    public SettingsPanel(Component parent, UIComponentListener ul) {
        setSize(parent.getSize());
        back = new CornerButton(getWidth() / 32, getHeight() / 32, getWidth() / 16, getHeight() / 16, CornerButton.NORTH_WEST, "X", "back", ul);
        picker = new ColorPicker(getWidth() / 2 - 5 * getWidth() / 16, getHeight() / 16, 5 * getWidth() / 8, 5 * getHeight() / 8, this);
        setIn = new Button(getWidth() / 2 - getWidth() / 8, getHeight() - getHeight() / 4, getWidth() / 4, getHeight() / 16, "Set IN", this);
        setOut = new Button(getWidth() / 2 - getWidth() / 8, setIn.getY() + setIn.getHeight() + getHeight() / 64, getWidth() / 4, getHeight() / 16, "Set OUT", this);
        reset = new Button(getWidth() / 2 - getWidth() / 8, setOut.getY() + setOut.getHeight() + getHeight() / 64, getWidth() / 4, getHeight() / 16, "Reset", this);
    }

    public void paint(Graphics g) {
        BufferedImage buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g2 = buffer.getGraphics();

        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(IN_COLOR);
        g2.fillRect(0, setIn.getY(), getWidth(), setIn.getHeight());

        g2.setColor(OUT_COLOR);
        g2.fillRect(0, setOut.getY(), getWidth(), setOut.getHeight());

        back.draw(g2);
        picker.draw(g2);
        setIn.draw(g2);
        setOut.draw(g2);
        reset.draw(g2);

        g.drawImage(buffer, 0, 0, null);
    }

    public void click(MouseEvent e) {
        back.click(e.getPoint());
        picker.click(e.getPoint());
        setIn.click(e.getPoint());
        setOut.click(e.getPoint());
        reset.click(e.getPoint());
        repaint();
    }

    @Override
    public void onUIComponentEvent(UIComponent ui) {
        if (ui.equals(setIn)) {
            IN_COLOR = picker.getColor();
        } else if (ui.equals(setOut)) {
            OUT_COLOR = picker.getColor();
        } else if (ui.equals(reset)) {
            IN_COLOR = Color.GREEN;
            OUT_COLOR = Color.RED;
        }
    }
}
