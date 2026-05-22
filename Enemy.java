
public class Enemy {

    private Action[] actions;
    private int hp = 0;

    public Enemy(int h, Action[] a) {
        this.actions = a;
        this.hp = h;
    }

    public Action doTurn(int i) {
        if (i < actions.length && i >= 0) {
            return this.actions[i];
        }
        return this.actions[0];
    }

    public boolean damage(int d) {
        hp -= d;
        return hp <= 0;
    }
}
