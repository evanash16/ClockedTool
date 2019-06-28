package input;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SettingsUtility {
    private static Map<String, String> settings = new HashMap<>();
    private static String previousPath;

    public static String getSetting(String setting) {
        return settings.getOrDefault(setting, null);
    }

    public static void setSetting(String setting, String config) {
        settings.put(setting, config);
    }

    public static void writeSettingsToFile() throws IOException {
        StringBuilder builder = new StringBuilder();
        for(Map.Entry<String, String> entry : settings.entrySet()) {
            builder.append(String.format("%s:%s\n", entry.getKey(), entry.getValue()));
        }
        FileUtility.writeToFile(previousPath, builder.toString());
    }

    public static void loadSettingsFromFile(String path) throws IOException {
        File settingsFile = FileUtility.openOrCreateFile(path);
        Scanner scanner = new Scanner(settingsFile);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parsedLine = line.split(":");
            settings.put(parsedLine[0], parsedLine[1]);
        }
        previousPath = path;
    }
}
