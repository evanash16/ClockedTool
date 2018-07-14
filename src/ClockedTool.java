import ui.UIComponent;
import ui.UIComponentListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class ClockedTool extends JFrame implements MouseListener, UIComponentListener {

    private static Toolkit tk = Toolkit.getDefaultToolkit();
    public static Dimension SCREENSIZE = new Dimension(tk.getScreenResolution() * 3, tk.getScreenResolution() * 3);
    private Timer updateTimer;

    private DialPanel dial;
    private Scheduler scheduler;
    private HistoryPanel history;

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
        addMouseListener(this);
        try {
            setIconImage(ImageIO.read(getClass().getResource("/images/ClockedToolIconV2.png")));
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }

        dial = new DialPanel(this, this);
        dial.setVisible(true);
        this.add(dial);

        history = new HistoryPanel(this, this);
        history.setVisible(false);
        this.add(history);

        scheduler = new Scheduler();

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
            if (dial.clicked(e)) {
                history.log();
                if (!dial.isDown()) {
                    if(!updateTimer.isRunning())
                        updateTimer.start();
                    scheduler.schedule(10);
                } else {
                    if(updateTimer.isRunning())
                        updateTimer.stop();
                    scheduler.clearEvents();
                    setLocation(tk.getScreenSize().width - getWidth(), tk.getScreenSize().height - getHeight());
                }
            }
        } else if(history.isVisible()) {
            history.click(e);
        }
    }

    @Override
    public void onUIComponentEvent(UIComponent ui) {
        if(ui.getId().equals("back")) {
            history.setVisible(false);
            dial.setVisible(true);
        } else if(ui.getId().equals("history")) {
            history.setVisible(true);
            dial.setVisible(false);
            history.update();
        }  else if(ui.getId().equals("minimize")) {
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
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
