import java.util.*;

public class Entity {

    public ArrayList<Action> deck;
    public ArrayList<Action> hand = new ArrayList<>();
    public ArrayList<Action> discard = new ArrayList<>();

    public int hp;
    public int block;
    public int para;
    public int[] stats = {0, 0, 0, 0, 3};

    public boolean isPlayer;
    public Entity target;

    public double temperature = 1.0;

    public Decision_Tree_Matrix AI =
            new Decision_Tree_Matrix(
                    new Decision_Tree_Training_Data(
                            "combat_training_actions.csv",
                            Game_Types.TYPES
                    )
            );

    public Entity(int hp, ArrayList<Action> deck, boolean isPlayer) {
        this.hp = hp;
        this.deck = deck;
        this.isPlayer = isPlayer;
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

    public Action_Result takeTurn(Turn_Context ctx) {

        draw(5);
        stats[4] = 3;

        Action chosen = AI_Selector.choose(this, ctx);

        if (chosen == null || chosen.cost > stats[4]) {
            hand.clear();
            return Action_Result.EMPTY;
        }

        stats[4] -= chosen.cost;

        int[] res;

        // Lucky decision injection
        if (chosen.name.equalsIgnoreCase("Lucky")) {
            res = chosen.Activate();

        } else {
            res = chosen.Activate();
        }

        Action_Result result = Action_Result.from(res, chosen);

        apply(result);

        hand.remove(chosen);
        discard.add(chosen);
        hand.clear();

        return result;
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