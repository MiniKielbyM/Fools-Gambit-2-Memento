import java.util.*;

public class Softmax {

    private static final Random rand = new Random();

    public static Action pick(List<Action> actions,
                              Map<Action, Double> scores,
                              double temperature) {

        double[] probs = new double[actions.size()];
        double sum = 0;

        for (int i = 0; i < actions.size(); i++) {
            double s = scores.getOrDefault(actions.get(i), 0.0);
            double exp = Math.exp(s / temperature);
            probs[i] = exp;
            sum += exp;
        }

        for (int i = 0; i < probs.length; i++) {
            probs[i] /= sum;
        }

        double r = rand.nextDouble();
        double acc = 0;

        for (int i = 0; i < probs.length; i++) {
            acc += probs[i];
            if (r <= acc) return actions.get(i);
        }

        return actions.get(actions.size() - 1);
    }
}