import java.util.Random;

public class Lucky extends Action {

    private static final Random rand = new Random();

    public Lucky() {
        this.name = "Lucky";
        this.desc = "Risk-based exponential damage or safety stop.";
        this.cost = 2;
    }

    public int[] Activate(boolean keepFlipping) {

        boolean coin = true;
        int damage = 5;
        int para = 0;
        int type = 1;

        while (coin) {

            damage *= 2;
            coin = rand.nextDouble() >= 0.5;

            if (!coin && keepFlipping && rand.nextDouble() < 0.5) {
                para += 10;
                type = 3;
                coin = true;
            }
        }
        System.out.println("Current Damage: " + damage);
        return new int[]{type, damage, para, cost};
    }

    @Override
    public int[] Activate() {
        return Activate(false);
    }
}