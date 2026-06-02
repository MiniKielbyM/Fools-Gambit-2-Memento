public class Gen_Attack extends Action {
    public int min = 0;
    public int range = 0;
    public int para = 0;
    private int type = 1;


    //Creates a generic attack with a range of damage it can do and a cost amount
    public Gen_Attack(int m, int r, int c, String n, String  d)
	{
		min = m;
		range = r;
        this.cost = c;
		this.name = n;
		this.desc = d;
	}

    //Overloaded to account for paralization if thats an additional cost
    public Gen_Attack(int m, int r, int c, int p, String n, String d)
	{
		min = m;
		range = r;
        this.cost = c;
		para = p;
		this.name = n;
		this.desc = d;
        type = 3;
	}

    //Sets the description
    public void setDesc(String d)
    {
        this.desc = d;
    }
    //Sets the name
    public void setName(String n)
    {
        this.name = n;
    }

    //Changes what happens when it is activated
    @Override
    public int[] Activate()
    {
	    return new int[] {type, (int) (Math.random()*range) + min,  para, cost};
    }
}
