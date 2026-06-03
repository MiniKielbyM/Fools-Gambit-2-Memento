import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

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
    private final Object[][] features;
    private final Object[][] outputs;
    private final Object[] types;

    public Decision_Tree_Training_Data(String filePath) {
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
                        @SuppressWarnings("unchecked")
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