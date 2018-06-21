import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Scheduler extends JPanel {

    private ArrayList<Long> events;

    public Scheduler(Container parent) {
        events = new ArrayList<>();
    }

    public void schedule(long delay) {events.add(delay);}
    public void decrement(long value) {
        events = (ArrayList<Long>) events.stream().map(v -> v - value).collect(Collectors.toList());
    }
    public boolean isAlarming() {
        boolean alarming = false;
        for(int i = events.size() - 1; i >= 0; i--) {
            if(events.get(i) < 0) {
                alarming = true;
            }
        }
        return alarming;
    }
    public void clearEvents() {events = new ArrayList<>();}
}
