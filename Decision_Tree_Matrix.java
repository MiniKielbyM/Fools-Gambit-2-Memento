import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Decision_Tree_Matrix {
    private final Object[][] trainingFeatures;
    private final Object[][] trainingOutputs;
    private Node root;

    public Decision_Tree_Matrix(Decision_Tree_Training_Data data) {
        this.trainingFeatures = data.getFeatures();
        this.trainingOutputs = data.getOutputs();
        if (trainingFeatures == null || trainingOutputs == null) {
            throw new IllegalArgumentException("Training features and outputs cannot be null");
        }
        if (trainingFeatures.length != trainingOutputs.length) {
            throw new IllegalArgumentException("Training features and outputs must have the same number of rows");
        }
        if (trainingFeatures.length == 0) {
            throw new IllegalArgumentException("Training data cannot be empty");
        }
    }


    public void train() {
        List<Integer> allRows = new ArrayList<>();
        for (int i = 0; i < trainingFeatures.length; i++) {
            allRows.add(i);
        }
        List<Integer> featureIndexes = new ArrayList<>();
        for (int i = 0; i < trainingFeatures[0].length; i++) {
            featureIndexes.add(i);
        }
        this.root = buildNode(featureIndexes, allRows);
    }

    public Object[] predict(Object[] input) {
        if (input == null) {
            throw new IllegalArgumentException("Input row cannot be null");
        }
        if (root == null) {
            throw new IllegalStateException("Model has not been trained yet");
        }
        return root.classify(input);
    }

    public Object[][] predict(Object[][] inputs) {
        if (inputs == null) {
            throw new IllegalArgumentException("Input matrix cannot be null");
        }
        Object[][] results = new Object[inputs.length][];
        for (int i = 0; i < inputs.length; i++) {
            results[i] = predict(inputs[i]);
        }
        return results;
    }

    public void printTree() {
        if (root == null) {
            System.out.println("Decision tree is not trained yet.");
            return;
        }
        root.print("");
    }

    private Node buildNode(List<Integer> availableFeatures, List<Integer> rows) {
        if (rows.isEmpty()) {
            List<Integer> allRows = new ArrayList<>();
            for (int i = 0; i < trainingOutputs.length; i++) {
                allRows.add(i);
            }
            return new Node(majorityOutput(allRows));
        }
        if (isPure(rows)) {
            return new Node(trainingOutputs[rows.get(0)]);
        }
        if (availableFeatures.isEmpty()) {
            return new Node(majorityOutput(rows));
        }

        int bestFeature = chooseBestFeature(availableFeatures, rows);
        if (bestFeature < 0) {
            return new Node(majorityOutput(rows));
        }

        Node node = new Node(bestFeature);
        Map<Object, List<Integer>> partitions = partitionRows(rows, bestFeature);
        for (Map.Entry<Object, List<Integer>> entry : partitions.entrySet()) {
            Object featureValue = entry.getKey();
            List<Integer> partitionRows = entry.getValue();
            if (partitionRows.isEmpty()) {
                node.addChild(featureValue, new Node(majorityOutput(rows)));
            } else {
                List<Integer> nextFeatures = new ArrayList<>(availableFeatures);
                nextFeatures.remove((Integer) bestFeature);
                node.addChild(featureValue, buildNode(nextFeatures, partitionRows));
            }
        }
        node.setDefaultChild(new Node(majorityOutput(rows)));
        return node;
    }

    private boolean isPure(List<Integer> rows) {
        if (rows.isEmpty()) {
            return true;
        }
        Object[] first = trainingOutputs[rows.get(0)];
        for (int row : rows) {
            if (!Arrays.deepEquals(first, trainingOutputs[row])) {
                return false;
            }
        }
        return true;
    }

    private int chooseBestFeature(List<Integer> availableFeatures, List<Integer> rows) {
        double currentEntropy = entropy(rows);
        double bestGain = 0.0;
        int bestFeature = -1;
        for (int feature : availableFeatures) {
            double gain = currentEntropy - conditionalEntropy(rows, feature);
            if (gain > bestGain) {
                bestGain = gain;
                bestFeature = feature;
            }
        }
        return bestFeature;
    }

    private double conditionalEntropy(List<Integer> rows, int feature) {
        Map<Object, List<Integer>> partitions = partitionRows(rows, feature);
        double total = rows.size();
        double sum = 0.0;
        for (List<Integer> partitionRows : partitions.values()) {
            if (partitionRows.isEmpty()) {
                continue;
            }
            sum += ((double) partitionRows.size() / total) * entropy(partitionRows);
        }
        return sum;
    }

    private Map<Object, List<Integer>> partitionRows(List<Integer> rows, int feature) {
        Map<Object, List<Integer>> partitions = new HashMap<>();
        for (int row : rows) {
            Object key = featureValue(trainingFeatures[row][feature]);
            partitions.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
        }
        return partitions;
    }

    private Object featureValue(Object raw) {
        if (raw == null) {
            return "<null>";
        }
        if (raw instanceof Number) {
            return raw;
        }
        return raw.toString();
    }

    private double entropy(List<Integer> rows) {
        Map<String, Integer> counts = new HashMap<>();
        for (int row : rows) {
            String label = labelKey(trainingOutputs[row]);
            counts.put(label, counts.getOrDefault(label, 0) + 1);
        }
        double total = rows.size();
        double entropy = 0.0;
        for (int count : counts.values()) {
            double probability = count / total;
            entropy -= probability * log2(probability);
        }
        return entropy;
    }

    private String labelKey(Object[] outputRow) {
        return Arrays.deepToString(outputRow);
    }

    private double log2(double value) {
        return Math.log(value) / Math.log(2);
    }

    private Object[] majorityOutput(List<Integer> rows) {
        Map<String, Integer> counts = new HashMap<>();
        Map<String, Object[]> representative = new HashMap<>();
        for (int row : rows) {
            Object[] outputRow = trainingOutputs[row];
            String key = labelKey(outputRow);
            counts.put(key, counts.getOrDefault(key, 0) + 1);
            representative.putIfAbsent(key, outputRow);
        }
        String bestLabel = null;
        int bestCount = -1;
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            if (entry.getValue() > bestCount) {
                bestCount = entry.getValue();
                bestLabel = entry.getKey();
            }
        }
        return representative.get(bestLabel);
    }

    private Object[] majorityOutput(Object[][] outputMatrix) {
        List<Integer> rows = new ArrayList<>();
        for (int i = 0; i < outputMatrix.length; i++) {
            rows.add(i);
        }
        return majorityOutput(rows);
    }

    static class Node {
        private final int featureIndex;
        private final Object[] output;
        private final Map<Object, Node> children = new HashMap<>();
        private Node defaultChild;
        private final boolean leaf;

        Node(int featureIndex) {
            this.featureIndex = featureIndex;
            this.output = null;
            this.leaf = false;
        }

        Node(Object[] output) {
            this.featureIndex = -1;
            this.output = output;
            this.leaf = true;
        }

        void addChild(Object featureValue, Node child) {
            children.put(featureValue, child);
        }

        void setDefaultChild(Node defaultChild) {
            this.defaultChild = defaultChild;
        }

        Object[] classify(Object[] input) {
            if (leaf) {
                return output;
            }
            Object key = input == null || featureIndex >= input.length ? null : input[featureIndex];
            Object value = key == null ? "<null>" : key;
            Node child = children.get(value);
            if (child == null) {
                return defaultChild != null ? defaultChild.classify(input) : output;
            }
            return child.classify(input);
        }

        void print(String prefix) {
            if (leaf) {
                System.out.println(prefix + "=> " + Arrays.deepToString(output));
                return;
            }
            for (Map.Entry<Object, Node> entry : children.entrySet()) {
                System.out.println(prefix + "[feature " + featureIndex + " == " + entry.getKey() + "]");
                entry.getValue().print(prefix + "  ");
            }
            if (defaultChild != null) {
                System.out.println(prefix + "[feature " + featureIndex + " == default]");
                defaultChild.print(prefix + "  ");
            }
        }
    }

    public static void main(String[] args) {

        Object[][] inputRows = {
            {1, 1, 1}
        };

        Decision_Tree_Matrix tree = new Decision_Tree_Matrix(new Decision_Tree_Training_Data("Test.csv"));
        tree.train();

        System.out.println("Trained decision tree:");
        tree.printTree();

        Object[][] predictions = tree.predict(inputRows);
        System.out.println("\nPredictions:");
        for (int i = 0; i < inputRows.length; i++) {
            System.out.println(Arrays.deepToString(inputRows[i]) + " => " + Arrays.deepToString(predictions[i]));
        }
    }
}
