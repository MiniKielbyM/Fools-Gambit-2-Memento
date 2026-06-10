public class Reward {

    public String name;
    public String desc;

    public enum Type {
        STAT,
        ACTION
    }

    public Type type;

    // stat reward
    public int statIndex;
    public int amount;

    // action reward
    public Action actionReward;

    // STAT reward constructor
    public Reward(String name, String desc, int statIndex, int amount) {
        this.name = name;
        this.desc = desc;
        this.type = Type.STAT;
        this.statIndex = statIndex;
        this.amount = amount;
    }

    // ACTION reward constructor
    public Reward(String name, String desc, Action action) {
        this.name = name;
        this.desc = desc;
        this.type = Type.ACTION;
        this.actionReward = action;
    }

    public void apply(Entity e) {

        switch (type) {

            case STAT -> {
                if (statIndex >= 0 && statIndex < e.stats.length) {
                    e.stats[statIndex] += amount;
                }
            }

            case ACTION -> {
                if (actionReward != null) {
                    e.deck.add(actionReward);
                }
            }
        }
    }

    @Override
    public String toString() {
        return name + ": " + desc;
    }
}