import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class Decision_Tree_Matrix {
    private Object[][] trainingFeatures;
    private Object[][] trainingOutputs;
    private Node root;
    private final Decision_Tree_Training_Data trainingData;

    public Decision_Tree_Matrix(Decision_Tree_Training_Data data) {
        this.trainingData = data;
        reloadTrainingData();
        if (trainingFeatures == null || trainingOutputs == null) {
            throw new IllegalArgumentException("Training features and outputs cannot be null");
        }
        if (trainingFeatures.length != trainingOutputs.length) {
            throw new IllegalArgumentException("Training features and outputs must have the same number of rows");
        }
        // empty training data is allowed initially; the tree can learn as games are simulated
    }

    public synchronized void reloadTrainingData() {
        Object[][] f = trainingData.getFeatures();
        Object[][] o = trainingData.getOutputs();
        this.trainingFeatures = f;
        this.trainingOutputs = o;
    }

    /**
     * Append a single observed example to the underlying training CSV and optionally retrain.
     */
    public synchronized void observeAndUpdate(Object[] features, Object[] outputs, boolean retrain) {
        trainingData.appendRow(features, outputs);
        reloadTrainingData();
        if (retrain) {
            train();
        }
    }

    public synchronized void observeAndUpdate(Object[] features, Object[] outputs) {
        observeAndUpdate(features, outputs, true);
    }


    public void train() {
        if (trainingFeatures.length == 0) {
            this.root = null;
            return;
        }
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

    public boolean isTrained() {
        return root != null;
    }

    public Object[] safePredict(Object[] input) {
        if (!isTrained()) {
            return null;
        }
        return predict(input);
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
        int numGames = readGameCount();
        String trainingFile = "combat_training_actions.csv";
        Object[] types = new Object[] {
                "int", // selfHp
                "int", // opponentHp
                "int", // selfBlock
                "int", // opponentBlock
                "int", // selfPara
                "int", // opponentPara
                "int", // selfAP
                "int", // opponentAP
                "int", // selfOptionCount
                "int", // opponentOptionCount
                "int", // turn
                "string", // selfOptions
                "string", // opponentOptions
                "string", // phase
                "string"  // output action
        };
        Decision_Tree_Training_Data data = new Decision_Tree_Training_Data(trainingFile, types);
        Decision_Tree_Matrix tree = new Decision_Tree_Matrix(data);
        tree.train();

        int initialSize = data.getFeatures().length;
        System.out.println("Starting self-play simulation with " + numGames + " games.");
        int winnerCount = 0;
        String[] allowedActions = new String[] { "Pistol", "Rifle", "Shotgun", "Take Cover", "Lucky" };
        for (int i = 1; i <= numGames; i++) {
            boolean playerOneWins = simulateSelfPlayGame(tree, allowedActions, i);
            if (playerOneWins) {
                winnerCount++;
            }
            if (i % 10 == 0) {
                System.out.println("Completed " + i + " games, current winning games for player one: " + winnerCount);
            }
        }
        System.out.println("Self-play finished. Player one won " + winnerCount + " / " + numGames + " games.");
        System.out.println("Training data rows before: " + initialSize + ", after: " + data.getFeatures().length);
        tree.reloadTrainingData();
        tree.train();
        System.out.println("Trained decision tree from self-play data:");
        tree.printTree();
    }

    private static int readGameCount() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter number of self-play games to simulate: ");
        while (!scanner.hasNextInt()) {
            System.out.print("Please enter a valid integer: ");
            scanner.next();
        }
        int value = scanner.nextInt();
        return Math.max(1, value);
    }

    private static boolean simulateSelfPlayGame(Decision_Tree_Matrix tree, String[] actionPool, int gameId) {
        Random random = new Random(gameId);
        String[] playerOneActions = chooseRandomActionSubset(actionPool, random);
        String[] playerTwoActions = chooseRandomActionSubset(actionPool, random);
        List<Action> playerOneActionList = createActionsFromNames(playerOneActions, random);
        List<Action> playerTwoActionList = createActionsFromNames(playerTwoActions, random);

        Entity playerOne = new Entity(100, new ArrayList<>(playerOneActionList), true, null);
        Entity playerTwo = new Entity(100, new ArrayList<>(playerTwoActionList), false, null);
        BattleCallback callback = new BattleCallback(playerOne, playerTwo);
        playerOne.callback = callback;
        playerTwo.callback = callback;

        int turn = 1;
        boolean currentPlayerOne = true;
        List<TrainingExample> p1Moves = new ArrayList<>();
        List<TrainingExample> p2Moves = new ArrayList<>();

        while (playerOne.hp > 0 && playerOne.para < 100 && playerTwo.hp > 0 && playerTwo.para < 100 && turn <= 100) {
            Entity current = currentPlayerOne ? playerOne : playerTwo;
            Entity opponent = currentPlayerOne ? playerTwo : playerOne;
            current.block = 0;
            current.stats[4] = 3;

            while (current.stats[4] > 0 && current.hp > 0 && current.para < 100 && opponent.hp > 0 && opponent.para < 100) {
                String[] currentNames = actionNames(current.actions);
                String[] opponentNames = actionNames(opponent.actions);
                Object[] features = buildFeatures(current.hp, opponent.hp, current.block, opponent.block,
                        current.para, opponent.para, current.stats[4], opponent.stats[4], currentNames,
                        opponentNames, turn);
                int actionIndex = chooseAction(tree, features, current.actions, current.stats[4], random);
                if (actionIndex < 0) {
                    break;
                }
                Action chosen = current.actions.get(actionIndex);
                Object[] output = new Object[] { chosen.name };
                if (currentPlayerOne) {
                    p1Moves.add(new TrainingExample(features, output));
                } else {
                    p2Moves.add(new TrainingExample(features, output));
                }

                int[] result = chosen.Activate();
                current.stats[4] -= chosen.cost;
                applyActionResult(current, opponent, result);

                current.hp = Math.min(current.hp, 100);
                if (current.para >= 100) {
                    current.hp = 0;
                    break;
                }
            }

            currentPlayerOne = !currentPlayerOne;
            turn++;
        }

        boolean playerOneWon = playerOne.hp > 0 && playerTwo.hp <= 0;
//        List<TrainingExample> winnerExamples = playerOneWon ? p1Moves : p2Moves;
  //      for (TrainingExample example : winnerExamples) {
    //        tree.observeAndUpdate(example.features, example.output, false);
      //  }
        if (!playerOneWon){
            try {
                for(TrainingExample example : p2Moves){
                    tree.observeAndUpdate(example.features, example.output, false);
                }
            } catch (Exception e) {
                
            }
        }
        tree.reloadTrainingData();
        tree.train();
        return playerOneWon;
    }

    private static Object[] buildFeatures(int selfHp, int opponentHp, int selfBlock, int opponentBlock,
            int selfPara, int enemyPara, int selfAp, int enemyAp,
            String[] selfOptions, String[] enemyOptions, int turn) {
        return new Object[] {
                selfHp,
                opponentHp,
                selfBlock,
                opponentBlock,
                selfPara,
                enemyPara,
                selfAp,
                enemyAp,
                selfOptions.length,
                enemyOptions.length,
                turn,
                String.join("|", selfOptions),
                String.join("|", enemyOptions),
                turn % 10 == 0 ? "late" : "early"
        };
    }

    private static int chooseAction(Decision_Tree_Matrix tree, Object[] features, List<Action> currentOptions,
            int currentAp, Random random) {
        List<Integer> affordable = new ArrayList<>();
        for (int i = 0; i < currentOptions.size(); i++) {
            if (currentOptions.get(i).cost <= currentAp) {
                affordable.add(i);
            }
        }
        if (affordable.isEmpty()) {
            return -1;
        }
        if (tree.isTrained()) {
            Object[] prediction = tree.safePredict(features);
            if (prediction != null && prediction.length > 0) {
                String label = prediction[0].toString();
                for (int index : affordable) {
                    if (currentOptions.get(index).name.equals(label)) {
                        return index;
                    }
                }
            }
        }
        return affordable.get(random.nextInt(affordable.size()));
    }

    private static String[] chooseRandomActionSubset(String[] pool, Random random) {
        if (pool == null || pool.length == 0) {
            return new String[] { "Pistol" };
        }
        int next = random.nextInt(pool.length) + 1;
        int count = next > 1? next : 2;
        List<String> shuffled = new ArrayList<>(Arrays.asList(pool));
        Collections.shuffle(shuffled, random);
        return shuffled.subList(0, count).toArray(new String[0]);
    }

    private static List<Action> createActionsFromNames(String[] names, Random random) {
        List<Action> actions = new ArrayList<>();
        for (String name : names) {
            actions.add(createActionByName(name, random));
        }
        return actions;
    }

    private static Action createActionByName(String name, Random random) {
        return switch (name.toLowerCase()) {
            case "pistol" -> new Gen_Attack(16, 10, 1, "Pistol",
                    "A standard firearm for self defense, dealing 16-26 damage");
            case "rifle" -> new Gen_Attack(22, 3, 1, "Rifle",
                    "A standard issue military firearm, dealing 22-25 damage");
            case "shotgun" -> new Gen_Attack(10, 25, 1, "Shotgun",
                    "A dusty double barrel shotgun, highly unpredictable, deals 10-35 damage");
            case "take cover" -> new Gen_Block(15, 0, 1, "Take Cover",
                    "Hide behind an object or fortification, gaining 15 block.");
            case "lucky" -> new Action() {
                {
                    this.name = "Lucky";
                    this.desc = "A risky gamble that can deal huge damage or generate paralization.";
                    this.cost = 2;
                }

                @Override
                public int[] Activate() {
                    int damage = 5;
                    int para = 0;
                    boolean coin = true;
                    while (coin) {
                        damage *= 2;
                        coin = random.nextBoolean();
                        if (!coin) {
                            if (random.nextBoolean()) {
                                para += 10;
                                coin = true;
                            }
                        }
                    }
                    int type = para > 0 ? 3 : 1;
                    return new int[] { type, damage, para, cost };
                }
            };
            default -> new Gen_Attack(8, 8, 1, "Attack", "A generic attack.");
        };
    }

    private static void applyActionResult(Entity current, Entity opponent, int[] result) {
        int type = result[0];
        int value = result[1];
        int para = result[2];
        switch (type) {
            case 1 -> opponent.damage(value);
            case 2 -> current.block += value;
            case 3 -> {
                opponent.damage(value);
                current.para += Math.max(0, para - current.stats[3]);
                current.stats[2] += Math.max(0, para);
            }
            case 4 -> {
                current.block += value;
                current.para += Math.max(0, para - current.stats[3]);
                current.stats[2] += Math.max(0, para);
            }
            default -> opponent.damage(value);
        }
    }

    private static String[] actionNames(List<Action> actions) {
        String[] names = new String[actions.size()];
        for (int i = 0; i < actions.size(); i++) {
            names[i] = actions.get(i).name;
        }
        return names;
    }

    private static class BattleCallback implements Callback {
        private final Entity playerOne;
        private final Entity playerTwo;

        BattleCallback(Entity playerOne, Entity playerTwo) {
            this.playerOne = playerOne;
            this.playerTwo = playerTwo;
        }

        @Override
        public void call(Object... args) {
            String[] type = ((String) args[0]).split("\\.");
            if (type[0].equals("player")) {
                int damage = (int) args[1];
                playerOne.damage(damage);
            }
            if (type[0].equals("enemy")) {
                int damage = (int) args[1];
                playerTwo.damage(damage);
            }
        }
    }

    private static class TrainingExample {
        final Object[] features;
        final Object[] output;

        TrainingExample(Object[] features, Object[] output) {
            this.features = features;
            this.output = output;
        }
    }
}
