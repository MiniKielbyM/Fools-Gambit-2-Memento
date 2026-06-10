import java.io.*;
import java.util.*;

class loader {
    public static Object[][] loadCSV(String filePath) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            ArrayList<Object[]> data = new ArrayList<>();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] values = line.split(",", -1);
                data.add(values);
            }

            return data.toArray(Object[][]::new);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

public class Decision_Tree_Training_Data {
    private Object[][] features;
    private Object[][] outputs;
    private final Object[] types;
    private final String filePath;

    public Decision_Tree_Training_Data(String filePath, Object[] types) {
        this.filePath = filePath;
        this.types = types;

        ensureFile(filePath, types);
        reload();
    }

    private void ensureFile(String filePath, Object[] types) {
        File f = new File(filePath);
        if (f.exists()) return;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
            for (int i = 0; i < types.length; i++) {
                bw.write(types[i].toString());
                if (i < types.length - 1) bw.write(",");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void appendRow(Object[] featureRow, Object[] outputRow) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.newLine();

            for (Object f : featureRow) {
                bw.write(String.valueOf(f));
                bw.write(",");
            }

            bw.write(String.valueOf(outputRow[0]));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        reload();
    }

    public synchronized void reload() {
        Object[][] data = loader.loadCSV(filePath);

        if (data.length <= 1) {
            features = new Object[0][0];
            outputs = new Object[0][0];
            return;
        }

        int rows = data.length - 1;
        int cols = data[0].length - 1;

        features = new Object[rows][cols];
        outputs = new Object[rows][1];

        for (int i = 1; i < data.length; i++) {
            for (int j = 0; j < cols; j++) {
                features[i - 1][j] = parse((String) data[i][j]);
            }
            outputs[i - 1][0] = data[i][cols];
        }
    }

    private Object parse(String s) {
        if (s.equalsIgnoreCase("true")) return true;
        if (s.equalsIgnoreCase("false")) return false;

        try { return Integer.parseInt(s); } catch (Exception ignored) {}
        return s;
    }

    public Object[][] getFeatures() { return features; }
    public Object[][] getOutputs() { return outputs; }
}