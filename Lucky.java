import java.util.Random;

public class Lucky extends Action {

    private static final Random rand = new Random();
//Creates a special action that lets the player flip a coin, for every flip the base damage of 2 is doubled 
    public Lucky() {
        this.name = "Lucky";
        this.desc = "Flip a coin, for every flip the base damage of 2 is doubled.";
        this.cost = 2;
    }

    //Activates the action
    public int[] Activate(boolean keepFlipping) {

        boolean coin = true;
        int damage = 2;
        int para = 0;
        int type = 1;

        while (coin) {

            damage *= 2;
            coin = rand.nextDouble() >= 0.5;

            if (!coin && keepFlipping && rand.nextDouble() < 0.5) {
                para += 20;
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