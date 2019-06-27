import ui.ColorPicker;
import ui.CornerButton;
import ui.Button;
import ui.UIComponent;
import ui.UIComponentListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Scanner;

public class SettingsPanel extends JPanel implements UIComponentListener {

    private CornerButton back;
    private ColorPicker picker;
    private Button setIn, setOut, reset;
    public static Color IN_COLOR = Color.GREEN, OUT_COLOR = Color.RED;

    public SettingsPanel(Component parent, UIComponentListener ul) {
        setSize(parent.getSize());
        back = new CornerButton(getWidth() / 32, getHeight() / 32, getWidth() / 8, getHeight() / 8, CornerButton.NORTH_WEST, "X", "back", ul);
        picker = new ColorPicker(getWidth() / 2 - 5 * getWidth() / 16, getHeight() / 16, 5 * getWidth() / 8, 5 * getHeight() / 8, this);

        try {
            File colorConfig = new File("ClockedTool.config");
            // color config doesn't exist yet, creating a new file
            if(colorConfig.createNewFile()) {
                writeColors();
            } else {
                readColors();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }


        setIn = new Button(getWidth() / 2 - getWidth() / 8, getHeight() - getHeight() / 4, getWidth() / 4, getHeight() / 16, "Set IN", this);
        setOut = new Button(getWidth() / 2 - getWidth() / 8, setIn.getY() + setIn.getHeight() + getHeight() / 64, getWidth() / 4, getHeight() / 16, "Set OUT", this);
        reset = new Button(getWidth() / 2 - getWidth() / 8, setOut.getY() + setOut.getHeight() + getHeight() / 64, getWidth() / 4, getHeight() / 16, "Reset", this);
    }

    private void writeColors() throws FileNotFoundException {
        File colorConfig = new File("ClockedTool.config");
        PrintStream outputStream = new PrintStream(colorConfig);
        outputStream.println(String.format("IN:(%d,%d,%d)", IN_COLOR.getRed(), IN_COLOR.getGreen(), IN_COLOR.getBlue()));
        outputStream.println(String.format("OUT:(%d,%d,%d)", OUT_COLOR.getRed(), OUT_COLOR.getGreen(), OUT_COLOR.getBlue()));
    }

    private void readColors() throws FileNotFoundException {
        File colorConfig = new File("ClockedTool.config");
        Scanner scanner = new Scanner(colorConfig);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().replaceAll("\\s","");
            String[] parsedLine = line.split(":");
            if (parsedLine[0].equals("IN")) {
                String rgb = parsedLine[1].substring(1, parsedLine[1].length() - 1);
                String[] parsedRGB = rgb.split(",");
                IN_COLOR = new Color(Integer.parseInt(parsedRGB[0]), Integer.parseInt(parsedRGB[1]), Integer.parseInt(parsedRGB[2]));
            } else if (parsedLine[0].equals("OUT")) {
                String rgb = parsedLine[1].substring(1, parsedLine[1].length() - 1);
                String[] parsedRGB = rgb.split(",");
                OUT_COLOR = new Color(Integer.parseInt(parsedRGB[0]), Integer.parseInt(parsedRGB[1]), Integer.parseInt(parsedRGB[2]));
            }
        }
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
        try {
            if (ui.equals(setIn)) {
                IN_COLOR = picker.getColor();
            } else if (ui.equals(setOut)) {
                OUT_COLOR = picker.getColor();
            } else if (ui.equals(reset)) {
                IN_COLOR = Color.GREEN;
                OUT_COLOR = Color.RED;
            }
            writeColors();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
