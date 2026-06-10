
import java.util.*;

public class Decision_Tree_Matrix {

    private final Decision_Tree_Training_Data data;
    private Node root;

    public Decision_Tree_Matrix(Decision_Tree_Training_Data data) {
        this.data = data;
        train();
    }

    public void train() {
        Object[][] f = data.getFeatures();
        Object[][] o = data.getOutputs();

        if (f.length == 0) {
            return;
        }

        List<Integer> rows = new ArrayList<>();
        for (int i = 0; i < f.length; i++) {
            rows.add(i);
        }

        List<Integer> features = new ArrayList<>();
        for (int i = 0; i < f[0].length; i++) {
            features.add(i);
        }

        root = build(features, rows, f, o);
    }

    public Object[] predict(Object[] input) {
        if (root == null) {
            return null;
        }
        return root.classify(input);
    }

    public Object[] safePredict(Object[] input) {
        return root == null ? null : predict(input);
    }

    public boolean isTrained() {
        return root != null;
    }

    private Node build(List<Integer> features, List<Integer> rows, Object[][] f, Object[][] o) {

        if (rows.isEmpty()) {
            return new Node(majority(o, rows));
        }
        if (pure(rows, o)) {
            return new Node(o[rows.get(0)]);
        }

        if (features.isEmpty()) {
            return new Node(majority(o, rows));
        }

        int best = bestFeature(features, rows, f, o);

        Node node = new Node(best);

        Map<Object, List<Integer>> split = new HashMap<>();

        for (int r : rows) {
            Object key = f[r][best];
            split.computeIfAbsent(key, k -> new ArrayList<>()).add(r);
        }

        List<Integer> nextFeatures = new ArrayList<>(features);
        nextFeatures.remove((Integer) best);

        for (var e : split.entrySet()) {
            node.children.put(e.getKey(),
                    build(nextFeatures, e.getValue(), f, o));
        }

        node.defaultChild = new Node(majority(o, rows));
        return node;
    }

    private boolean pure(List<Integer> rows, Object[][] o) {
        Object first = o[rows.get(0)][0];
        for (int r : rows) {
            if (!o[r][0].equals(first)) {
                return false;
            }
        }
        return true;
    }

    private int bestFeature(List<Integer> features, List<Integer> rows,
            Object[][] f, Object[][] o) {

        double bestScore = -1;
        int best = -1;

        for (int feat : features) {
            double score = score(feat, rows, f, o);
            if (score > bestScore) {
                bestScore = score;
                best = feat;
            }
        }

        return best;
    }

    private double score(int feature, List<Integer> rows,
            Object[][] f, Object[][] o) {

        Map<Object, List<Integer>> split = new HashMap<>();

        for (int r : rows) {
            split.computeIfAbsent(f[r][feature], k -> new ArrayList<>()).add(r);
        }

        double total = rows.size();
        double gain = entropy(rows, o);

        for (var e : split.values()) {
            gain -= (e.size() / total) * entropy(e, o);
        }

        return gain;
    }

    private double entropy(List<Integer> rows, Object[][] o) {
        Map<String, Integer> map = new HashMap<>();

        for (int r : rows) {
            String k = String.valueOf(o[r][0]);
            map.put(k, map.getOrDefault(k, 0) + 1);
        }

        double ent = 0;
        double total = rows.size();

        for (int v : map.values()) {
            double p = v / total;
            ent -= p * (Math.log(p) / Math.log(2));
        }

        return ent;
    }

    private Object[] majority(Object[][] o, List<Integer> rows) {
        Map<String, Object[]> map = new HashMap<>();
        Map<String, Integer> count = new HashMap<>();

        for (int r : rows) {
            String k = String.valueOf(o[r][0]);
            map.putIfAbsent(k, o[r]);
            count.put(k, count.getOrDefault(k, 0) + 1);
        }

        String best = null;
        int bestC = -1;

        for (var e : count.entrySet()) {
            if (e.getValue() > bestC) {
                best = e.getKey();
                bestC = e.getValue();
            }
        }

        return map.get(best);
    }

    public void observeAndUpdate(Object[] input, Object[] output, boolean retrain) {
        data.appendRow(input, output);
        if (!retrain) {
            return;
        }
        // safer but expensive
        train();
    }

    static class Node {

        int feature;
        Map<Object, Node> children = new HashMap<>();
        Node defaultChild;
        Object[] value;

        Node(int feature) {
            this.feature = feature;
        }

        Node(Object[] value) {
            this.value = value;
            this.feature = -1;
        }

        Object[] classify(Object[] input) {
            if (value != null) {
                return value;
            }

            Object key = input[feature];

            Node n = children.get(key);
            if (n == null) {
                return defaultChild.classify(input);
            }

            return n.classify(input);
        }
    }
}
