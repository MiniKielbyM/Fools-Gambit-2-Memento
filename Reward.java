public class Reward {
    // This class acts as the prent to all rewards

    // Name and description of reward
    public String name = "";
    public String desc = "";
    public Action re;
    public int stat = 0;
    public int amt = 0;
    private Callback callback;

    // Constructor
    public Reward(String n, String d, int s, int a) {
        name = n;
        desc = d;
        stat = s;
        amt = a;
    }

    // Constructor
    public Reward(String n, String d, Action a) {
        name = n;
        desc = d;
        re = a;
    }

    // Allows the benifits of the reward to be gained
    public void gain(Entity e) {
        callback = e;
        if (re != null) {
            callback.call(new Object[] {"reward.re", re});
        }
        else
        {
            callback.call(new Object[] {"reward.stat", stat, amt});
        }
    }

    @Override
    public String toString() {
        return name + ":" + desc;
    }
}