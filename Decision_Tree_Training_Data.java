import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

class loader {
    private static Scanner scanner;

    public static Object[][] loadCSV(String filePath) {
        try {
            scanner = new Scanner(new File(filePath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found: " + filePath, e);
        }
        ArrayList<Object[]> data = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] values = line.split(",");
            Object[] row = Arrays.copyOf(values, values.length, Object[].class);
            data.add(row);
        }
        return data.toArray(Object[][]::new);
    }
}

public class Decision_Tree_Training_Data {
    private Object[][] features;
    private Object[][] outputs;
    private final Object[] types;
    private final String filePath;

    public Decision_Tree_Training_Data(String filePath) {
        this.filePath = filePath;
        Object[][] data = loader.loadCSV(filePath);
        this.types = data[0];
        int numRows = data.length - 1;
        int numFeatures = data[0].length - 1;
        this.features = new Object[numRows][numFeatures];
        this.outputs = new Object[numRows][1];
        for (int i = 1; i < data.length; i++) {
            for (int j = 0; j < numFeatures; j++) {
                this.features[i - 1][j] = convertValue(data[i][j].toString(), this.types[j].toString());
            }
            this.outputs[i - 1][0] = convertValue(data[i][numFeatures].toString(), this.types[numFeatures].toString());
        }
    }
    public Decision_Tree_Training_Data(String filePath, Object[] types) {
        this.filePath = filePath;
        ensureFileWithHeader(filePath, types);
        Object[][] data = loader.loadCSV(filePath);
        this.types = data[0];
        int numRows = Math.max(0, data.length - 1);
        int numFeatures = data[0].length - 1;
        this.features = new Object[numRows][numFeatures];
        this.outputs = new Object[numRows][1];
        for (int i = 1; i < data.length; i++) {
            for (int j = 0; j < numFeatures; j++) {
                this.features[i - 1][j] = convertValue(data[i][j].toString(), this.types[j].toString());
            }
            this.outputs[i - 1][0] = convertValue(data[i][numFeatures].toString(), this.types[numFeatures].toString());
        }
    }

    private void ensureFileWithHeader(String filePath, Object[] types) {
        File file = new File(filePath);
        if (!file.exists()) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                for (int i = 0; i < types.length; i++) {
                    bw.write(types[i].toString());
                    if (i < types.length - 1) {
                        bw.write(',');
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to create training data file", e);
            }
        }
    }

    public synchronized void appendRow(Object[] featureRow, Object[] outputRow) {
        if (featureRow == null || outputRow == null) {
            throw new IllegalArgumentException("Rows cannot be null");
        }
        StringBuilder sb = new StringBuilder();
        int numFeatures = types.length - 1;
        for (int i = 0; i < numFeatures; i++) {
            if (i < featureRow.length) {
                sb.append(stringifyValue(featureRow[i]));
            }
            sb.append(',');
        }
        // output column
        sb.append(stringifyValue(outputRow[0]));
        // append to file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.filePath, true))) {
            bw.newLine();
            bw.write(sb.toString());
            bw.flush();
        } catch (IOException e) {
            throw new RuntimeException("Failed to append training data", e);
        }
        reload();
    }

    public synchronized void reload() {
        Object[][] data = loader.loadCSV(this.filePath);
        if (data.length < 1) {
            throw new IllegalStateException("Training data file has no header row");
        }
        Object[] header = data[0];
        if (header.length != types.length) {
            throw new IllegalStateException("Training data file does not match expected header length");
        }
        int numRows = Math.max(0, data.length - 1);
        int numFeatures = header.length - 1;
        Object[][] newFeatures = new Object[numRows][numFeatures];
        Object[][] newOutputs = new Object[numRows][1];
        for (int i = 1; i < data.length; i++) {
            for (int j = 0; j < numFeatures; j++) {
                newFeatures[i - 1][j] = convertValue(data[i][j].toString(), this.types[j].toString());
            }
            newOutputs[i - 1][0] = convertValue(data[i][numFeatures].toString(), this.types[numFeatures].toString());
        }
        this.features = newFeatures;
        this.outputs = newOutputs;
    }

    private String stringifyValue(Object o) {
        if (o == null) return "";
        // keep it simple: avoid commas in values
        String s = o.toString();
        return s.replace(",", "\\,");
    }

    public String getFilePath() {
        return this.filePath;
    }

    @SuppressWarnings("unchecked")
    private static Object convertValue(String raw, String type) {
        if (raw == null || raw.isEmpty()) {
            return null;
        }
        String normalizedType = type.trim().toLowerCase();
        switch (normalizedType) {
            case "int":
            case "integer":
                return Integer.valueOf(raw);
            case "long":
                return Long.valueOf(raw);
            case "double":
            case "float":
            case "number":
                return Double.valueOf(raw);
            case "boolean":
            case "bool":
                return Boolean.valueOf(raw);
            case "string":
            case "str":
                return raw;
            default:
                try {
                    Class<?> clazz = Class.forName(type.trim());
                    if (clazz == String.class) {
                        return raw;
                    }
                    if (clazz.isEnum()) {
                        Class<Enum> enumClass = (Class<Enum>) clazz;
                        return Enum.valueOf(enumClass, raw);
                    }
                    try {
                        return clazz.getMethod("valueOf", String.class).invoke(null, raw);
                    } catch (Exception ignored) {
                    }
                    try {
                        return clazz.getConstructor(String.class).newInstance(raw);
                    } catch (Exception ignored) {
                    }
                } catch (ClassNotFoundException ignored) {
                }
                return raw;
        }
    }

    public Object[][] getFeatures() {
        return features;
    }

    public Object[][] getOutputs() {
        return outputs;
    }
}