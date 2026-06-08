import java.util.ArrayList;
import java.util.Scanner;

//Acts as a basis for all enemies and players
public class Entity implements Callback {

    @Override
    public void call(Object... args) {
        String[] type = ((String) args[0]).split("\\.");
        if (type[0].equals("reward")) {
            if (type[1].equals("re")) {
                Action a = (Action) args[1];
                actions.add(a);
            } else if (type[1].equals("stat")) {
                int stat = (int) args[1];
                int amt = (int) args[2];
                stats[stat] += amt;
            }
        }

    }

    // List of actions, stats that modify those skills and if the entity is a player
    public ArrayList<Action> actions;
    public ArrayList<Action> discard = new ArrayList<Action>();
     public ArrayList<Action> hand = new ArrayList<Action>();
    // Stats in order: str, dex, cha, tol, ap (action points)
    public int exDraws = 0;
    public int draws = 5;
    public int hp = 0;
    public int block = 0;
    public int para = 0;
    public int[] stats = { 0, 0, 0, 0, 3 };
    public boolean isPlayer = false;
    public Callback callback;
    private final int currentAction = 0; // for enemies to keep track of what action they are on
    Scanner input = new Scanner(System.in);
    private Decision_Tree_Matrix AI = new Decision_Tree_Matrix(
            new Decision_Tree_Training_Data("combat_training_actions.csv"));

    // Constructor
    public Entity(int h, ArrayList<Action> a, boolean p, Callback c) {
        this.actions = a;
        this.hp = h;
        this.isPlayer = p;
        this.callback = c;
    }

    //Places the cards in the deck in a random order
    public void shuffle()
    {
        ArrayList<Action> b = new ArrayList<>();
        for(int i = 0; i<actions.size(); i++)
        {
            b.add(actions.get(i));
            actions.remove(i);
            actions.add((int) (Math.random()*actions.size()-1), b.get(0));
            b.remove(0);
        }
    }

    //Puts all actions in main array after battle
    public void endBattle()
    {
        for(int i = 0; i<discard.size(); i++)
            {
                actions.add(discard.get(0));
                discard.remove(0);
            }
    }

    //Draws a action from the deck
    public void draw(int d)
    {
        for (int i = 0; i<(d); i++)
        {
            if(actions.size()==0)
            {
                for(int u = 0; u<discard.size(); u++)
                {
                    actions.add(discard.get(0));
                    discard.remove(discard.get(0));
                }
                this.shuffle();
            }
            if(!actions.isEmpty())
            {
                hand.add(actions.get(0));
                actions.remove(0);
            }
        }
    }

    // Allows the player or entity to do a turn
    public void doTurn() {
        System.out.println("Health: " + hp);
        block = 0;
        if (isPlayer) {
            this.draw(draws+exDraws);
            System.out.println("Paralization: " + para + "\nTolerence: " + stats[3]);
            stats[4] = 3;
            while (stats[4] > 0) {
                System.out.println("HP: " + hp);
                System.out.println("AP: " + stats[4]);
                // list available actions
                for (int i = 0; i < hand.size(); i++) {
                    System.out.println(i + ". " + hand.get(i));
                }
                // gets the player choice and activates
                int c = input.nextInt();
                if (c >= 0 && c < actions.size() && actions.get(c).cost <= stats[4]) {
                    int[] a = actions.get(c).Activate();
                    boolean actionEndedTurn = false;
                    switch (a[0]) {
                        case 1 -> callback.call(new Object[] { "enemy.damage", a[1] + stats[0] });
                        case 2 -> block += a[1] + stats[1];
                        case 3 -> {
                            callback.call(new Object[] { "enemy.damage", a[1] + stats[2] });
                            para += Math.floor((double) a[3] / 2.0) - stats[2] > 1
                                    ? Math.floor((double) a[3] / 2.0) - stats[2]
                                    : 0;
                            stats[2] += Math.floor((double) a[3] / 2.0);
                            actionEndedTurn = checkParaDeath();
                        }
                        case 4 -> {
                            block += a[1] + stats[1];
                            para += Math.floor((double) a[3] / 2.0) - stats[2] > 1
                                    ? Math.floor((double) a[3] / 2.0) - stats[2]
                                    : 0;
                            stats[2] += Math.floor((double) a[3] / 2.0);
                            actionEndedTurn = checkParaDeath();
                        }
                        default -> System.out.println("Invalid action type!");
                    }
                    stats[4] -= actions.get(c).cost;
                    if (actionEndedTurn || hp <= 0) {
                        break;
                    }
                }
            }
            for(int i = 0; i<hand.size(); i++)
            {
                discard.add(hand.get(0));
                hand.remove(0);
            }
            // Does enemy turn
        } else {
            System.out.println("Enemy HP: " + hp);
            System.out.println("Enemy uses " + actions.get(currentAction % (actions.size())).name + "!");
            callback.call(new Object[]{"get.gamedata"});
            Object[][] inputs = new Object[][] {{}};
            AI.predict(inputs);
        }
    }

    // Allows entities to take damage, returning true if they die

    public boolean damage(int d) {
        hp -= d;
        return hp <= 0;
    }

    private boolean checkParaDeath() {
        if (para >= 100) {
            hp = 0;
            System.out.println((isPlayer ? "Player" : "Enemy") + " has died from paralysis!");
            return true;
        }
        return false;
    }
    
    public Object[] toFeatures(){
        return new Object[]{""};
    }
}
