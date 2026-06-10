public class AI_Trainer {

    public static void observe(Entity e, Turn_Context ctx, Action_Result r) {

        boolean success =
                r.type == Action_Result.Type.DAMAGE &&
                r.a > 0;

        e.AI.observeAndUpdate(
                AI_Selector.buildFeatures(e, ctx),
                new Object[]{r.actionName},
                success
        );
    }
}