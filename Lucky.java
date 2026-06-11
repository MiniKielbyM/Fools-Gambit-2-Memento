import java.util.Random;

public class Lucky extends Action {

    private static final Random rand = new Random();

    public Lucky() {
        this.name = "Lucky";
        this.desc = "Flip a coin until you get tails, then deal 10 damage doubled for every heads!";
        this.cost = 2;
    }

    @Override
    public int[] Activate() {

        boolean coin = true;
        int damage = 5;
        int para = 0;
        int type = 1;

        // simulate risk decision internally (no input system)
        boolean keepFlipping = rand.nextDouble() < 0.5;

        while (coin) {

            damage *= 2;
            coin = rand.nextDouble() >= 0.5;

            if (!coin) {

                // probabilistic risk continuation (AI-like behavior)
                if (keepFlipping && rand.nextDouble() < 0.5) {
                    para += 10;
                    type = 3;
                    coin = true;
                }
            }
        }

        return new int[] {
                type,
                damage,
                para,
                cost
        };
    }
}