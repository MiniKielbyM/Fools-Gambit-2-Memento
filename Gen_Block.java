public class Gen_Block extends Action {

    public int min = 0;
    public int range = 0;
    public int para = 0;
    public int type = 2;
//Creates a template for an simple action that protects the player
    public Gen_Block(int m, int r, int c, String n, String d) {
        min = m;
        range = r;
        cost = c;
        name = n;
        desc = d;
    }
//Overloaded constructor for if it is a special block action that costs paralization
    public Gen_Block(int m, int r, int c, int p, String n, String d) {
        min = m;
        range = r;
        cost = c;
        para = p;
        type = 4;
        name = n;
        desc = d;
    }
//Helper methods
    public void setDesc(String d) {
        this.desc = d;
    }

    public void setName(String n) {
        this.name = n;
    }
//Method to activate the action
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