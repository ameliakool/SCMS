package smart;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

class SaveData {
    private static final String DATA_DIR = "data/";

    static {
        new File(DATA_DIR).mkdirs();
    }

    public static <T> void saveData(String filename, List<T> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
             new FileOutputStream(DATA_DIR + filename))) {
            oos.writeObject(data);
        } catch (IOException e) {
            System.err.println("There has been an error while saving " + filename + ": " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> loadData(String filename) {
        File file = new File(DATA_DIR + filename);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
             new FileInputStream(file))) {
            return (List<T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("There is no existing " + filename + " found or a reading error has occured. Starting anew.");
            return new ArrayList<>();
        }
    }
}
