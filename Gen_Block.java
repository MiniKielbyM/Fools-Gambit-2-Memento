public class Gen_Block extends Action {
    public int min = 0;
    public int range = 0;
    public int para = 0;
    public int type = 2;

    //Constructor for the player blocking
    public Gen_Block(int m, int r, int c)
	{
		min = m;
		range = r;
        this.cost = c;
	}

    //Constructor for the player blocking if it us
    public Gen_Block(int m, int r, int c, int p)
	{
		min = m;
		range = r;
        this.cost = c;
		para = p;
        type=4;
	}

    public void setDesc(String d)
    {
        this.desc = d;
    }

    public void setName(String n)
    {
        this.name = n;
    }

    @Override
    public int[] Activate()
    {
	    return new int[] {type, (int) (Math.random()*range) + min,  para, cost};
    }
}
