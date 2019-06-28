import input.FileUtility;
import input.KeyboardUtility;
import input.SettingsUtility;
import ui.Button;
import ui.Checkbox;
import ui.*;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

public class HistoryPanel extends JPanel implements UIComponentListener {

    private Map<String, ArrayList<Punch>> log;
    private String date;
    private int buttonWidth, updateTicks;
    private static final int UPDATE_TIME = 2 /*in seconds*/, FRAMERATE = 30 /*in frames per second*/;
    private static final String UPDATE_STRING = "Updated: ";

    private boolean viewingHistory, updating;
    private Checkbox verbose, fullTime, weekHours;
    private Button left, right, back;
    private Timer animationTimer;

    public HistoryPanel(Container parent, UIComponentListener ul) {
        log = new HashMap<>();
        date = getDate();
        setSize(parent.getSize());
        buttonWidth = getWidth() / 10;

        verbose = new Checkbox(getWidth() - getWidth() / 50 - getWidth() / 20, getHeight() / 50, getWidth() / 20, getHeight() / 20, "Verbose", this);
        fullTime = new Checkbox(getWidth() - getWidth() / 50 - getWidth() / 20, verbose.getY() + verbose.getHeight() + getHeight() / 50, getWidth() / 20, getHeight() / 20, "Full Time", this);
        weekHours = new Checkbox(getWidth() - getWidth() / 50 - getWidth() / 20, fullTime.getY() + fullTime.getHeight() + getHeight() / 50, getWidth() / 20, getHeight() / 20, "Week Hours", this);

        verbose.setDefault(Boolean.parseBoolean(SettingsUtility.getSetting(verbose.getLabel())));
        fullTime.setDefault(Boolean.parseBoolean(SettingsUtility.getSetting(fullTime.getLabel())));
        weekHours.setDefault(Boolean.parseBoolean(SettingsUtility.getSetting(weekHours.getLabel())));

        left = new Button(buttonWidth, getHeight() - buttonWidth, ((getWidth() + buttonWidth) / 2) - buttonWidth, buttonWidth, "<", this);
        right = new Button((getWidth() + buttonWidth) / 2, getHeight() - buttonWidth, (getWidth() + buttonWidth) / 2 - buttonWidth, buttonWidth, ">", this);
        back = new Button(0, 0, buttonWidth, getHeight(), "Back", "back", Button.BOTTOM_TO_TOP, ul);

        new Timer(30000, (e) -> update()).start(); //update work time estimate every thirty seconds
        animationTimer = new Timer(1000 / FRAMERATE, (e) -> update()); //fades out "Updated text"

        try{
            // open history.log, create if it doesn't exist
            File historyLog = FileUtility.openOrCreateFile("history.log");

            Scanner scanner = new Scanner(historyLog);
            // parse punches out of history.log, add to timestamps ArrayList
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] components = line.split(",");
                String date = components[0];
                if(!log.containsKey(date)) {
                    log.put(date, new ArrayList<>());
                }
                ArrayList<Punch> timestamps = log.get(date);
                for(int i = 1; i < components.length; i++) {
                    timestamps.add(new Punch(components[i], back.getX() + back.getWidth(), (i + 1) * getHeight() / 12, getHeight() / 12, this));
                }
                log.put(date, timestamps);
            }
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

        // if there are log entries for the current date
        if(log.containsKey(date)) {

            // draw the current date
            g2.setColor(Color.WHITE);
            g2.drawString(date, back.getWidth(), g2.getFont().getSize());

            // for every punch in today's date
            ArrayList<Punch> today = log.get(date);
            for(int i = 0; i < today.size(); i++) {
                // between every pair of punches, display the time between the punches
                if(verbose.isChecked() && i - 1 >= 0 && (i - 1) % 2 == 0) {
                    g2.setFont(new Font(null, Font.BOLD, getHeight() / 16));
                    final String todayString = String.format(" (+%.1f)", timeDifference(today.get(i - 1).getPunchTime(), today.get(i).getPunchTime()));
                    g2.setColor(Color.WHITE);
                    g2.drawString(todayString, today.get(i).getX() + Math.max(today.get(i - 1).getWidth(), today.get(i).getWidth()) + 5, (today.get(i - 1).getY() + today.get(i).getY()) / 2 + 3 * today.get(i).getHeight() / 4);
                }
                today.get(i).draw(g2);
            }

            // if full time is selected and the last punch isn't completed,
            // display clock out time to have worked 8 hours
            if(fullTime.isChecked() && (today.size() - 1) % 2 == 0) {
                double additionalTime = 8.0 - loggedTime();
                g2.setColor(Color.RED);
                if (additionalTime < 0)
                    g2.setColor(Color.GREEN);
                String anticipatedTime = getTime(additionalTime);
                g2.drawString(anticipatedTime, back.getWidth(), (today.size() + 3) * g2.getFont().getSize());
            }
            // if full time isn't checked, display the clock out time that
            // matches the total work time on the same day in the previous week
            else if(date.equals(getDate()) && loggedTime(previousWeek()) > 0 && (today.size() - 1) % 2 == 0) {
                double additionalTime = loggedTime(previousWeek()) - loggedTime();
                g2.setColor(Color.RED);
                if (additionalTime < 0)
                        g2.setColor(Color.GREEN);
                String anticipatedTime = getTime(additionalTime);
                g2.drawString(anticipatedTime, back.getWidth(), (today.size() + 3) * g2.getFont().getSize());
            }

            // if week hours is selected, display the hours worked and the total hours
            // worked in the week
            if(weekHours.isChecked()) {
                g2.setColor(Color.WHITE);
                g2.setFont(new Font(null, Font.BOLD, getHeight() / 20));
                String timeString = String.format("%.1f hours", loggedTime());
                String weekString = String.format("Week: %.1f hours", weekHours(date));
                g2.drawString(timeString, back.getWidth(), left.getY() - g2.getFont().getSize() - 5);
                g2.drawString(weekString, back.getWidth(), left.getY() - 5);
            } else {
                g2.setColor(Color.WHITE);
                String timeString = String.format("%.1f hours", loggedTime());
                g2.drawString(timeString, back.getWidth(), left.getY() - 5);
            }
        }

        if(KeyboardUtility.isPressed(KeyEvent.VK_CONTROL)) {
            left.setLabel("<<");
            right.setLabel(">>");
        } else {
            left.setLabel("<");
            right.setLabel(">");
        }

        back.draw(g2);
        left.draw(g2);
        right.draw(g2);
        verbose.draw(g2);
        fullTime.draw(g2);
        weekHours.draw(g2);

        // when the log updates, show the timestamp
        if(updating) {
            g2.setColor(new Color(255, 255, 255, (int) (255 * (1 - (double) updateTicks / (double) (FRAMERATE * UPDATE_TIME)))));
            g2.setFont(new Font(null, Font.PLAIN, getHeight() / 20));

            int stringWidth = g2.getFontMetrics().stringWidth(UPDATE_STRING + getTime());
            g2.drawString(UPDATE_STRING + getTime(), getWidth() - stringWidth - 5, right.getY() - 5);
        }

        g.drawImage(buffer, 0, 0, null);
    }

    // pull month, day, and year out of a Calendar instance and put into a nice date format
    public String getDate() {
        Calendar calendar = Calendar.getInstance();
        return String.format("%02d/%02d/%02d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE), calendar.get(Calendar.YEAR));
    }

    // return the String for an entry in the log at the date
    // 'direction' days in the future or past
    public String getDate(int direction) {
        return getDate(date, direction);
    }

    // return the String for an entry in the log at the date
    // one forward or backwards (indicated by 'direction') from
    // the currentDate
    public String getDate(String currentDate, int direction) {
        Comparator<String> dateComparator = Comparator.comparing((String k) -> k.split("/")[2]) //sort by year
                .thenComparing((String k) -> k.split("/")[0]) //sort by month
                .thenComparing((String k) -> k.split("/")[1]); //sort by day
        ArrayList<String> orderedKeys = (ArrayList<String>) log.keySet().stream().sorted(dateComparator).collect(Collectors.toList());
        int currentIndex = orderedKeys.indexOf(currentDate);
        int nextIndex = currentIndex + direction;
        if(nextIndex >= 0 && nextIndex <= orderedKeys.size() - 1) {
            return orderedKeys.get(nextIndex);
        } else if(nextIndex < 0) {
            return orderedKeys.get(0);
        } else {
            return orderedKeys.get(orderedKeys.size() - 1);
        }
    }

    // pull the minute and hour (in 24 hour format) from a
    // Calendar instance and put in a nice time format
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
        if(!updating) {
            updating = true;
            animationTimer.start();
        } else if(updateTicks == UPDATE_TIME * FRAMERATE) {
            updateTicks = 0;
            updating = false;
            animationTimer.stop();
        } else {
            updateTicks++;
        }
        if(!viewingHistory) {
            date = getDate();
        }
        repaint();
    }

    // return the date exactly one week before the current date
    public String previousWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setWeekDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.WEEK_OF_YEAR) - 1 % 52, calendar.get(Calendar.DAY_OF_WEEK));
        return String.format("%02d/%02d/%02d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE), calendar.get(Calendar.YEAR));
    }

    public int weekOfYear(String date) {
        Calendar calendar = Calendar.getInstance();
        int year = Integer.parseInt(date.split("/")[2]);
        int month = Integer.parseInt(date.split("/")[0]) - 1;
        int day = Integer.parseInt(date.split("/")[1]);
        calendar.set(year, month, day);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    public double weekHours(String date) {
        double hours = 0;
        for(String weekDate : weekDates(date)) {
            hours += loggedTime(weekDate);
        }
        return hours;
    }

    // returns an ArrayList<String> of the strings for entries in
    // log for all punches in the previous week
    public ArrayList<String> weekDates(String date) {
        ArrayList<String> dates = new ArrayList<>();
        String currentDate = date;
        dates.add(currentDate);

        // move backwards from the current date until the week changes
        int weekOfYear = weekOfYear(currentDate);
        while(weekOfYear(currentDate) == weekOfYear) {
            String previousDate = getDate(currentDate, -1);
            // if the next date isn't the same date, and the week hasn't changed
            // set the current date to the next date
            if(!currentDate.equals(previousDate) && weekOfYear(previousDate) == weekOfYear) {
                currentDate = getDate(currentDate, -1);
            } else {
                break;
            }

            // add the date to the ArrayList
            if(!dates.contains(currentDate)) {
                dates.add(currentDate);
            }
        }

        // move forwards from the current date until the week changes
        currentDate = date;
        while(weekOfYear(currentDate) == weekOfYear) {
            String nextDate = getDate(currentDate, 1);
            // if the next date isn't the same date, and the week hasn't changed
            // set the current date to the next date
            if(!currentDate.equals(nextDate) && weekOfYear(nextDate) == weekOfYear) {
                currentDate = getDate(currentDate, 1);
            } else {
                break;
            }

            // add the date to the ArrayList
            if(!dates.contains(currentDate)) {
                dates.add(currentDate);
            }
        }
        return dates;
    }

    // compute the total time logged for a particular date
    public double loggedTime(String date) {
        double hours = 0;
        if(log.containsKey(date)) {
            ArrayList<Punch> timestamps = log.get(date);
            for(int i = 0; i < timestamps.size() - ((timestamps.size() % 2) + 1); i += 2) {
                hours += timeDifference(timestamps.get(i).getPunchTime(), timestamps.get(i + 1).getPunchTime());
            }
            if(timestamps.size() % 2 != 0) { //haven't clocked out yet
                hours += timeDifference(timestamps.get(timestamps.size() - 1).getPunchTime(), getTime());
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

    // add a punch to the HashTable for the current date
    // and write the updated HashTable to the log file
    public void log() {
        date = getDate();
        if(!log.containsKey(date)) {
            log.put(date, new ArrayList<>());
        }
        ArrayList<Punch> timestamps = log.get(date);
        String timestamp = getTime();
        timestamps.add(new Punch(timestamp, back.getX() + back.getWidth(), (timestamps.size() + 2) * getHeight() / 12, getHeight() / 12, this));
        log.put(date, timestamps);
        writeLog();
    }

    public void writeLog() {
        try {
            StringBuilder builder = new StringBuilder();
            // for every date in the log sorted from December to January (12 -> 1),
            // output the punches to the log
            for(String date : log.keySet().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList())) {
                builder.append(date + ",");
                ArrayList<Punch> today = log.get(date);
                for(int i = 0; i < today.size(); i++) {
                    builder.append(today.get(i).getPunchTime());
                    if(i < today.size() - 1) {
                        builder.append(",");
                    } else{
                        builder.append("\n");
                    }
                }
            }
            FileUtility.writeToFile("history.log", builder.toString());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void removeLog(Punch removedPunch) {
        ArrayList<Punch> today = log.get(date);
        int indexOfP = today.indexOf(removedPunch);
        today.remove(removedPunch);
        for (Punch p : today.subList(indexOfP, today.size())) {
            p.setY(p.getY() - p.getHeight());
        }

        // if there are no entries left under today's date,
        // remove the log entry for today's date
        if(today.isEmpty()) {
            log.remove(date);
            date = getDate();
        }
        writeLog();
    }

    public void click(MouseEvent e) {
        back.click(e.getPoint());
        left.click(e.getPoint());
        right.click(e.getPoint());
        verbose.click(e.getPoint());
        fullTime.click(e.getPoint());
        weekHours.click(e.getPoint());
        if(log.containsKey(date)) {
            for(Punch p : log.get(date)) {
                p.click(e.getPoint());
                if (p.isDeleted()) {
                    removeLog(p);
                    break;
                }
            }
        }
        repaint();
    }

    @Override
    public void onUIComponentEvent(UIComponent ui) {
        if(ui.equals(left)) {
            viewingHistory = true;
            date = getDate(KeyboardUtility.isPressed(KeyEvent.VK_CONTROL) ? -2 : -1);
        } else if(ui.equals(right)) {
            viewingHistory = true;
            date = getDate(KeyboardUtility.isPressed(KeyEvent.VK_CONTROL) ? 2 : 1);
        } else if (ui instanceof Checkbox){
            SettingsUtility.setSetting(ui.getLabel(), Boolean.toString(((Checkbox) ui).isChecked()));
            try {
                SettingsUtility.writeSettingsToFile();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
