public class AI_Selector {

    public static Action choose(Entity e, Turn_Context ctx) {

        Object[] features = buildFeatures(e, ctx);

        Object[] pred = e.AI.safePredict(features);

        if (pred == null || pred.length == 0) {
            return fallback(e);
        }

        String name = pred[0].toString();

        for (Action a : e.hand) {
            if (a.name.equalsIgnoreCase(name)) return a;
        }

        return fallback(e);
    }

    private static Action fallback(Entity e) {
        return e.hand.isEmpty() ? null : e.hand.get(0);
    }

    public static Object[] buildFeatures(Entity e, Turn_Context c) {
        return new Object[]{
                c.hp,
                c.enemyHp,
                c.block,
                c.enemyBlock,
                c.para,
                c.enemyPara,
                c.energy
        };
    }
}