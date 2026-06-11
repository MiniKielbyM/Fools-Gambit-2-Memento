import java.util.*;

public class Entity {

    private static final Scanner input = new Scanner(System.in);

    public ArrayList<Action> deck;
    public ArrayList<Action> hand = new ArrayList<>();
    public ArrayList<Action> discard = new ArrayList<>();

    public int hp;
    public int block;
    public int para;
    public int[] stats = { 0, 0, 0, 0, 3 };

    public boolean isPlayer;
    public Entity target;

    public double temperature = 1.0;

    public Decision_Tree_Matrix AI;

    public Entity(int hp, ArrayList<Action> deck, boolean isPlayer) {
        this.hp = hp;
        this.deck = deck;
        this.isPlayer = isPlayer;

        this.AI = new Decision_Tree_Matrix(
                new Decision_Tree_Training_Data(
                        "combat_training_actions.csv",
                        Game_Types.TYPES));
    }

    public void setTarget(Entity t) {
        this.target = t;
    }

    public void shuffle() {
        Collections.shuffle(deck);
    }

    public void draw(int n) {
        for (int i = 0; i < n; i++) {
            if (deck.isEmpty()) {
                deck.addAll(discard);
                discard.clear();
            }
            if (!deck.isEmpty()) {
                hand.add(deck.remove(0));
            }
        }
    }

    public void endBattle() {
        discard.addAll(hand);
        hand.clear();
        block = 0;
        para = 0;
        stats[4] = 3;
    }

    private Action choosePlayerAction() {

        while (true) {

            System.out.println("\n====================");
            System.out.println("Your HP: " + hp);
            System.out.println("Enemy HP: " + target.hp);
            System.out.println("Block: " + block);
            System.out.println("Energy: " + stats[4]);
            System.out.println("====================");

            for (int i = 0; i < hand.size(); i++) {

                Action a = hand.get(i);

                System.out.println(
                        i + ": "
                                + a.name
                                + " | Cost: "
                                + a.cost
                                + " | "
                                + a.desc);
            }

            System.out.print("\nChoose card: ");

            if (!input.hasNextInt()) {
                input.nextLine();
                System.out.println("Invalid input.");
                continue;
            }

            int choice = input.nextInt();
            input.nextLine();

            if (choice < 0 || choice >= hand.size()) {
                System.out.println("Invalid card.");
                continue;
            }

            Action selected = hand.get(choice);

            if (selected.cost > stats[4]) {
                System.out.println("Not enough energy.");
                continue;
            }

            return selected;
        }
    }

    public Action_Result takeTurn(Turn_Context ctx) {

        draw(5);
        stats[4] = 3;

        Action_Result lastResult = Action_Result.EMPTY;

        while (stats[4] > 0 && !hand.isEmpty()) {

            Action chosen;

            if (isPlayer) {
                chosen = choosePlayerAction();
            } else {
                chosen = AI_Selector.choose(this, ctx);
            }

            if (chosen == null) {
                break;
            }

            if (chosen.cost > stats[4]) {
                if (isPlayer) {
                    System.out.println("Not enough energy.");
                    continue;
                }
                break;
            }

            stats[4] -= chosen.cost;

            int[] res = null;

            if (!this.isPlayer) {
                if (chosen instanceof Lucky l) {

                    boolean keepFlipping = Math.random() < (1.0 / Math.max(0.1, temperature));

                    res = l.Activate(keepFlipping);

                } else {

                    res = chosen.Activate();
                }
            } else {
                if (chosen instanceof Lucky l) {
                    boolean KeepFlipping = true;
                    while (KeepFlipping) {
                        res = l.Activate(false);
                        System.out.println("Keep Flipping? (Y/N)");
                        KeepFlipping = input.nextLine().equalsIgnoreCase("y")? true : false;
                    }

                } else {

                    res = chosen.Activate();
                }
            }

            lastResult = Action_Result.from(res, chosen);

            apply(lastResult);

            System.out.println(
                    (isPlayer ? "Player" : "Enemy")
                            + " used "
                            + chosen.name);

            hand.remove(chosen);
            discard.add(chosen);

            if (target.hp <= 0) {
                break;
            }
        }

        discard.addAll(hand);
        hand.clear();

        return lastResult;
    }

    private void apply(Action_Result r) {

        switch (r.type) {
            case DAMAGE -> target.hp -= r.a;
            case BLOCK -> block += r.a;
            case DAMAGE_PARA -> {
                target.hp -= r.a;
                para += r.b;
            }
            case BLOCK_PARA -> {
                block += r.a;
                para += r.b;
            }
        }
    }
}