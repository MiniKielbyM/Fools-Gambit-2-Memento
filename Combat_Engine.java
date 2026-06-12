import java.util.Scanner;

public class Combat_Engine {

    private final Entity a;
    private final Entity b;
    private final Scanner input = new Scanner(System.in);

    public Combat_Engine(Entity a, Entity b) {
        this.a = a;
        this.b = b;

        a.setTarget(b);
        b.setTarget(a);
    }

    public void run() {

        System.out.println("Battle start");

        while (a.hp > 0 && b.hp > 0) {
            a.shuffle();
            b.shuffle();

            while (a.hp > 0 && b.hp > 0
                    && a.para < 100 && b.para < 100) {

                takeTurn(a);
                if (b.hp <= 0)
                    break;

                takeTurn(b);
            }

            if (a.hp <= 0 || b.hp <= 0)
                break;

            endRound();
        }

        System.out.println(a.hp > 0 ? "A wins" : "B wins");
    }

    private void takeTurn(Entity e) {
        Turn_Context ctx = Turn_Context.from(e);
        Action_Result result = e.takeTurn(ctx);
        AI_Trainer.observe(e, ctx, result);
    }

    private void endRound() {
        a.endBattle();
        b.endBattle();
        if (a.hp > 0 && a.para < 100){
            reward(a);
        }
    }

    private void reward(Entity e) {

        System.out.println("\nReward phase");
        System.out.println("0: Strength +3");
        System.out.println("1: Dexterity +2");
        System.out.println("2: Charisma +3");
        System.out.println("3: Tolerance +5");

        int choice = input.nextInt();

        switch (choice) {
            case 0 -> e.stats[0] += 3;
            case 1 -> e.stats[1] += 2;
            case 2 -> e.stats[2] += 3;
            case 3 -> e.stats[3] += 5;
        }
    }
}