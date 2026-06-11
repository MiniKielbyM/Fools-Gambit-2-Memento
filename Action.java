public abstract class Action {
    public String name;
    public String desc;
    public int cost;

    public abstract int[] Activate();
}