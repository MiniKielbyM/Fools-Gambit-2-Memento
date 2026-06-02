import java.util.Scanner;

public class Lucky extends Action {
    private Scanner input = new Scanner(System.in);

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
        while (coin) {
            damage *= 2;
            coin = Math.random() >= .5;
            if (!coin) {
                System.out.println("Tails! Would you like to deal " + damage
                        + " damage or gain 10 paralization to count this as heads and continue flipping?\n(Enter y or n): ");
                if(input.nextLine().equalsIgnoreCase("y"))
                {
                    para += 10;
                    type = 3;
                    coin = true;
                }
            }
            else
            {
                System.out.println("Heads! You are now dealing " +  damage + " damage!");
            }
        }
        return new int[] { type, damage, para, cost };
    }
}
