import input.FileUtility;
import input.KeyboardUtility;
import input.SettingsUtility;
import ui.UIComponent;
import ui.UIComponentListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

public class ClockedTool extends JFrame implements KeyListener, MouseListener, UIComponentListener {

    private static Toolkit tk = Toolkit.getDefaultToolkit();
    public static Dimension SCREENSIZE = new Dimension(tk.getScreenResolution() * 3, tk.getScreenResolution() * 3);
    private Timer updateTimer;

    private DialPanel dial;
    private Scheduler scheduler;
    private HistoryPanel history;
    private SettingsPanel settings;

    public static void main(String[] args) {
        new ClockedTool();
    }

    public ClockedTool() {
        super("Clocked Tool");
        init();
    }

    public void init() {
        setSize(SCREENSIZE);

        setLocation(tk.getScreenSize().width - getWidth(), tk.getScreenSize().height - getHeight());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        setUndecorated(true);
        setResizable(false);
        addKeyListener(this);
        addMouseListener(this);
        try {
            setIconImage(ImageIO.read(getClass().getResource("/images/ClockedToolIconV2.png")));
            FileUtility.openOrCreateFile("ClockedTool.config");
            SettingsUtility.loadSettingsFromFile("ClockedTool.config");
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }

        dial = new DialPanel(this, this);
        dial.setVisible(true);
        this.add(dial);

        history = new HistoryPanel(this, this);
        history.setVisible(false);
        this.add(history);

        settings = new SettingsPanel(this, this);
        settings.setVisible(false);
        this.add(settings);

        scheduler = new Scheduler();

        // call update every second
        updateTimer = new Timer(1000, e -> update());

        setVisible(true);
    }

    public void update() {
        scheduler.decrement(1);
        if(scheduler.isEmpty())
            if(updateTimer.isRunning())
                updateTimer.stop();
        if(scheduler.isAlarming()) {
            setState(NORMAL);
            setLocation((tk.getScreenSize().width - getWidth()) / 2, (tk.getScreenSize().height - getHeight()) / 2);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (dial.isVisible()) {
            dial.click(e);
            // if the dial was successfully clicked
            if (dial.clicked(e)) {
                // log the current time
                history.log();
                history.update();
                // if the dial is out, schedule an interruption in 10 seconds
                if (!dial.isDown()) {
                    if(!updateTimer.isRunning())
                        updateTimer.start();
                    scheduler.schedule(10);
                } else {
                    if(updateTimer.isRunning()) {
                        updateTimer.stop();
                    }
                    scheduler.clearEvents();
                    setLocation(tk.getScreenSize().width - getWidth(), tk.getScreenSize().height - getHeight());
                }
            }
        } else if (history.isVisible()) {
            history.click(e);
        } else if (settings.isVisible()) {
            settings.click(e);
        }
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if(!KeyboardUtility.isPressed(keyEvent.getKeyCode())) {
            KeyboardUtility.keyPressed(keyEvent.getKeyCode());
            history.repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        if(KeyboardUtility.isPressed(keyEvent.getKeyCode())) {
            KeyboardUtility.keyReleased(keyEvent.getKeyCode());
            history.repaint();
        }
    }

    @Override
    public void onUIComponentEvent(UIComponent ui) {
        if (ui.getId().equals("back")) {
            history.setVisible(false);
            settings.setVisible(false);
            dial.setVisible(true);
        } else if (ui.getId().equals("history")) {
            history.setVisible(true);
            dial.setVisible(false);
            history.update();
        } else if (ui.getId().equals("settings")) {
            settings.setVisible(true);
            dial.setVisible(false);
        }  else if (ui.getId().equals("minimize")) {
            if(scheduler.isAlarming()) {
                setLocation((tk.getScreenSize().width - getWidth()) / 2, (tk.getScreenSize().height - getHeight()) / 2);
            } else {
                setLocation(tk.getScreenSize().width - getWidth(), tk.getScreenSize().height - getHeight());
            }
        }
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void keyTyped(KeyEvent keyEvent) {}
}
