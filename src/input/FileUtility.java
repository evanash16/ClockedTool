package input;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class FileUtility {
    private static Map<String, File> files = new HashMap<>();

    public static File openFile(String path) throws IOException {
        if (files.containsKey(path)) {
            return files.get(path);
        } else {
            throw new IOException("File hasn't been loaded yet.");
        }
    }

    public static File openOrCreateFile(String path) throws IOException {
        File newFile = new File(path);
        newFile.createNewFile();
        files.put(path, newFile);
        return newFile;
    }

    public static void writeToFile(String path, String data) throws IOException {
        File file = openFile(path);
        PrintStream outputStream = new PrintStream(file);
        outputStream.print(data);
    }
}
