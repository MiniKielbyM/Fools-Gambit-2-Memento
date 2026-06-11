public class Gen_Attack extends Action {

    public int min;
    public int range;
    public int para;
    private int type = 1;

    public Gen_Attack(int m, int r, int c, String n, String d) {
        min = m;
        range = r;
        cost = c;
        name = n;
        desc = d;
    }

    public Gen_Attack(int m, int r, int c, int p, String n, String d) {
        min = m;
        range = r;
        cost = c;
        para = p;
        name = n;
        desc = d;
        type = 3;
    }

    @Override
    public int[] Activate() {
        int damage = min;

        if (range > 0) {
            damage += (int) (Math.random() * range);
        }

        return new int[] { type, damage, para, cost };
    }
}