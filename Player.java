
import java.util.ArrayList;
import java.util.Arrays;

public class Player {

    private ArrayList<Action> actions;
    private int hp = 0;

    public Player(int h, Action[] a) {
        this.actions.addAll(Arrays.asList(a));
        this.hp = h;
    }

    public boolean damage(int d) {
        hp -= d;
        return hp <= 0;
    }

    public void addAction(Action a) {
        this.actions.add(a);
    }

    public void removeAction(Action a) {
        this.actions.remove(a);
    }

    public Action doTurn(int i) {
        if (i < actions.size()) {
            return this.actions.get(i);
        }
        return null;
    }
}
