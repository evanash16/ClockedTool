package input;

import java.util.HashMap;
import java.util.Map;

public class KeyboardUtility {
    private static Map<Integer, Boolean> keys = new HashMap<>();

    public static void keyPressed(int keyCode) {
        keys.put(keyCode, true);
    }

    public static void keyReleased(int keyCode) {
        keys.put(keyCode, false);
    }

    public static boolean isPressed(int keyCode) {
        return (keys.containsKey(keyCode) && keys.get(keyCode));
    }
}
