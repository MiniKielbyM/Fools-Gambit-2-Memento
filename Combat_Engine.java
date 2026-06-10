import java.util.*;

public class Combat_Engine {

    private final Entity a;
    private final Entity b;
    private final Callback callback;

    public Combat_Engine(Entity a, Entity b, Callback cb) {
        this.a = a;
        this.b = b;
        this.callback = cb;

        a.setTarget(b);
        b.setTarget(a);
    }

    public void run() {

        System.out.println("Battle start");

        while (a.hp > 0 && b.hp > 0) {

            resetRound();

            while (canContinue(a) && canContinue(b)) {

                takeTurn(a);
                if (b.hp <= 0) break;

                takeTurn(b);
            }

            if (a.hp <= 0 || b.hp <= 0) break;

            endRound();

            reward(a);
        }

        System.out.println(a.hp > 0 ? "A wins" : "B wins");
    }

    private void takeTurn(Entity e) {
        Turn_Context ctx = Turn_Context.from(e);

        Action_Result result = e.takeTurn(ctx);

        AI_Trainer.observe(e, ctx, result);
    }

    private boolean canContinue(Entity e) {
        return e.hp > 0 && e.para < 100;
    }

    private void resetRound() {
        a.shuffle();
        b.shuffle();
    }

    private void endRound() {
        a.endBattle();
        b.endBattle();
    }

    private void reward(Entity e) {
        if (e != a) return;

        System.out.println("\nReward phase");
        System.out.println("0: Strength +3");
        System.out.println("1: Dexterity +2");
        System.out.println("2: Charisma +3");
        System.out.println("3: Tolerance +5");

        Scanner sc = new Scanner(System.in);
        int choice = sc.nextInt();

        switch (choice) {
            case 0 -> e.stats[0] += 3;
            case 1 -> e.stats[1] += 2;
            case 2 -> e.stats[2] += 3;
            case 3 -> e.stats[3] += 5;
        }
    }
}