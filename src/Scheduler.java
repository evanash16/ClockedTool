import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Scheduler extends JPanel {

    private ArrayList<Long> events;
    private boolean alarming;

    public Scheduler() {
        events = new ArrayList<>();
    }

    public void schedule(long delay) {events.add(delay);}

    // decrement all events, alarm if any of the events hit 0
    public void decrement(long value) {
        for(int i = 0; i < events.size(); i++) {
            Long eventTime = events.get(i);
            events.set(i, eventTime - value);
            if(eventTime <= 0) {
                alarming = true;
                events.remove(i);
                i--;
            }
        }
    }

    public boolean isAlarming() {return this.alarming;}

    public boolean isEmpty() {return events.isEmpty();}

    public void clearEvents() {
        events = new ArrayList<>();
        alarming = false;
    }
}
