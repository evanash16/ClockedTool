import ui.Button;
import ui.Checkbox;
import ui.UIComponent;
import ui.UIComponentListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

public class HistoryPanel extends JPanel implements UIComponentListener {

    private Map<String, ArrayList<String>> log;
    private String date;
    private int buttonWidth;
    private boolean viewingHistory;
    private Checkbox verbose, fullTime;
    private Button left, right, back;

    public HistoryPanel(Container parent, UIComponentListener ul) {
        log = new HashMap<>();
        date = getDate();
        setSize(parent.getSize());
        buttonWidth = getWidth() / 10;

        verbose = new Checkbox(getWidth() - getWidth() / 50 - getWidth() / 20, getHeight() / 50, getWidth() / 20, getHeight() / 20, "Verbose", this);
        fullTime = new Checkbox(getWidth() - getWidth() / 50 - getWidth() / 20, verbose.getY() + verbose.getHeight() + getHeight() / 50, getWidth() / 20, getHeight() / 20, "Full Time", this);
        left = new Button(buttonWidth, getHeight() - buttonWidth, ((getWidth() + buttonWidth) / 2) - buttonWidth, buttonWidth, "<", this);
        right = new Button((getWidth() + buttonWidth) / 2, getHeight() - buttonWidth, (getWidth() + buttonWidth) / 2 - buttonWidth, buttonWidth, ">", this);
        back = new Button(0, 0, buttonWidth, getHeight(), "Back", "back", Button.BOTTOM_TO_TOP, ul);
        try{
            File historyLog = new File("history.log");
            historyLog.createNewFile();

            Scanner scanner = new Scanner(historyLog);
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] components = line.split(",");
                String date = components[0];
                if(!log.containsKey(date)) {
                    log.put(date, new ArrayList<>());
                }
                ArrayList<String> timestamps = log.get(date);
                for(int i = 1; i < components.length; i++) {
                    timestamps.add(components[i]);
                }
                log.put(date, timestamps);
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void paint(Graphics g) {
        BufferedImage buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g2 = buffer.getGraphics();

        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setFont(new Font(null, Font.BOLD, getHeight() / 12));

        if(log.containsKey(date)) {
            g2.setColor(Color.WHITE);
            g2.drawString(date, buttonWidth, g2.getFont().getSize());
            ArrayList<String> today = log.get(date);
            String todayString;
            for(int i = 0; i < today.size(); i++) {
                todayString = today.get(i);
                if(verbose.isChecked() && i - 1 >= 0 && (i - 1) % 2 == 0) {
                    todayString += String.format(" (+%.1f hours)", timeDifference(today.get(i - 1), today.get(i)));
                }
                g2.drawString(todayString, buttonWidth, (i + 3) * g2.getFont().getSize());
            }
            if(fullTime.isChecked() && (today.size() - 1) % 2 == 0) {
                double additionalTime = 8.0 - loggedTime();
                g2.setColor(Color.RED);
                if (additionalTime < 0)
                    g2.setColor(Color.GREEN);
                String anticipatedTime = getTime(additionalTime);
                g2.drawString(anticipatedTime, buttonWidth, (today.size() + 3) * g2.getFont().getSize());
            } else if(date.equals(getDate()) && loggedTime(previousWeek()) > 0 && (today.size() - 1) % 2 == 0) {
                double additionalTime = loggedTime(previousWeek()) - loggedTime();
                g2.setColor(Color.RED);
                if (additionalTime < 0)
                        g2.setColor(Color.GREEN);
                String anticipatedTime = getTime(additionalTime);
                g2.drawString(anticipatedTime, buttonWidth, (today.size() + 3) * g2.getFont().getSize());
            }
        }

        g2.setColor(Color.WHITE);
        String timeString = String.format("%.1f hours", loggedTime());
        g2.drawString(timeString, back.getWidth(), left.getY() - 5);

        back.draw(g2);
        left.draw(g2);
        right.draw(g2);
        verbose.draw(g2);
        fullTime.draw(g2);

        g.drawImage(buffer, 0, 0, null);
    }

    public String getDate() {
        Calendar calendar = Calendar.getInstance();
        return String.format("%02d/%02d/%02d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE), calendar.get(Calendar.YEAR));
    }

    public String getDate(int direction) {
        Comparator<String> dateComparator = Comparator.comparing((String k) -> k.split("/")[2]) //sort by year
                                                      .thenComparing((String k) -> k.split("/")[0]) //sort by month
                                                      .thenComparing((String k) -> k.split("/")[1]); //sort by day
        ArrayList<String> orderedKeys = (ArrayList<String>) log.keySet().stream().sorted(dateComparator).collect(Collectors.toList());
        int currentIndex = orderedKeys.indexOf(date);
        int nextIndex = currentIndex + (int) Math.signum(direction);
        if(nextIndex >= 0 && nextIndex <= orderedKeys.size() - 1) {
            return orderedKeys.get(nextIndex);
        }
        return date;
    }

    public String getTime(double additionalHours) {
        int hours = (int) (Math.floor(additionalHours));
        int minutes = (int) (additionalHours * 60 - hours * 60);

        Calendar calendar = Calendar.getInstance();
        int currentMinute = calendar.get(Calendar.MINUTE);
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

        int newMinute = (currentMinute + minutes) % 60;
        int newHour = (currentHour + hours + Math.floorDiv(minutes + currentMinute, 60));

        return String.format("%d:%02d", newHour, newMinute);
    }

    public String getTime() {
        return getTime(0);
    }

    public void update() {
        if(!viewingHistory) {
            date = getDate();
        }
    }

    public String previousWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setWeekDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.WEEK_OF_YEAR) - 1 % 52, calendar.get(Calendar.DAY_OF_WEEK));
        return String.format("%02d/%02d/%02d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE), calendar.get(Calendar.YEAR));
    }

    public double loggedTime(String date) {
        double hours = 0;
        if(log.containsKey(date)) {
            ArrayList<String> timestamps = log.get(date);
            for(int i = 0; i < timestamps.size() - ((timestamps.size() % 2) + 1); i += 2) {
                hours += timeDifference(timestamps.get(i), timestamps.get(i + 1));
            }
            if(timestamps.size() % 2 != 0) { //haven't clocked out yet
                hours += timeDifference(timestamps.get(timestamps.size() - 1), getTime());
            }
        }
        return hours;
    }

    public double loggedTime() {
        return loggedTime(date);
    }

    private double timeDifference(String inTime, String outTime) {
        double hourDiff = Double.parseDouble(outTime.split(":")[0]) - Double.parseDouble(inTime.split(":")[0]);
        double minuteDiff = Double.parseDouble(outTime.split(":")[1]) - Double.parseDouble(inTime.split(":")[1]);
        return hourDiff + (minuteDiff / 60.0);
    }

    public void log() {
        date = getDate();
        if(!log.containsKey(date)) {
            log.put(date, new ArrayList<>());
        }
        ArrayList<String> timestamps = log.get(date);
        String timestamp = getTime();
        timestamps.add(timestamp);
        log.put(date, timestamps);
        writeLog();
    }

    public void writeLog() {
        try {
            PrintStream out = new PrintStream(new File("history.log"));
            for(String date : log.keySet().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList())) {
                out.print(date + ",");
                ArrayList<String> today = log.get(date);
                for(int i = 0; i < today.size(); i++) {
                    out.print(today.get(i));
                    if(i < today.size() - 1) {
                        out.print(",");
                    } else{
                        out.println();
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public void click(MouseEvent e) {
        left.click(e.getPoint());
        right.click(e.getPoint());
        verbose.click(e.getPoint());
        fullTime.click(e.getPoint());
        back.click(e.getPoint());
    }

    @Override
    public void onUIComponentEvent(UIComponent ui) {
        if(ui.equals(left)) {
            viewingHistory = true;
            date = getDate(-1);
        } else if(ui.equals(right)) {
            viewingHistory = true;
            date = getDate(1);
        }
    }
}
