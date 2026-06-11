import java.util.Random;

public class AI_Trainer {

    private static final Random rand = new Random();

    public static void observe(Entity e, Turn_Context ctx, Action_Result r) {

        boolean success =
                r.type == Action_Result.Type.DAMAGE &&
                r.a > 0 &&
                rand.nextDouble() > 0.1;

        e.AI.observeAndUpdate(
                AI_Selector.buildFeatures(e, ctx),
                new Object[]{r.actionName},
                success
        );
    }
}