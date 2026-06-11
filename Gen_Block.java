public class Gen_Block extends Action {

    public int min = 0;
    public int range = 0;
    public int para = 0;
    public int type = 2;

    public Gen_Block(int m, int r, int c, String n, String d) {
        min = m;
        range = r;
        cost = c;
        name = n;
        desc = d;
    }

    public Gen_Block(int m, int r, int c, int p, String n, String d) {
        min = m;
        range = r;
        cost = c;
        para = p;
        type = 4;
        name = n;
        desc = d;
    }

    public void setDesc(String d) {
        this.desc = d;
    }

    public void setName(String n) {
        this.name = n;
    }

    @Override
    public int[] Activate() {
        return new int[] {
                type,
                (int)(Math.random() * range) + min,
                para,
                cost
        };
    }
}