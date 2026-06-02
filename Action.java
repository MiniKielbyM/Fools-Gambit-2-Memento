public class Action {
    public String name = "";
    public String desc = "";
    public int cost = 1;
    //The Action class will be the parent to all things the player can do on their turn
    public Action() {
    }

    // First int in the array is the action type (1= attack, 2=defend, 3=special)
        // Second is the value (damage or block)
    public int[] Activate() {
        return new int[] { 0,0,0,0 };
    }

    @Override
    //adds a tostring method
    public String toString() {
        return name + ": " + desc + " (Cost: " + cost + " AP)";
    }
}
