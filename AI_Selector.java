import java.util.HashMap;
import java.util.Map;

public class AI_Selector {

    public static Action choose(Entity e, TurnContext ctx) {

        Object[] features = buildFeatures(e, ctx);

        Map<Action, Double> scores = new HashMap<>();

        Object[] pred = e.AI.safePredict(features);
        String predicted = pred == null ? null : pred[0].toString();

        for (Action a : e.hand) {

            double score = 0.0;

            if (predicted != null && a.name.equalsIgnoreCase(predicted)) {
                score += 2.0;
            }

            if (a.cost <= e.stats[4]) {
                score += 0.5;
            }

            scores.put(a, score);
        }

        return Softmax.pick(e.hand, scores, e.temperature);
    }

    private static Object[] buildFeatures(Entity e, TurnContext c) {
        return new Object[]{
                c.hp,
                c.enemyHp,
                c.block,
                c.enemyBlock,
                c.para,
                c.enemyPara,
                c.energy,
                hasLucky(e)
        };
    }

    private static boolean hasLucky(Entity e) {
        for (Action a : e.hand) {
            if (a.name.equalsIgnoreCase("Lucky")) return true;
        }
        return false;
    }
}