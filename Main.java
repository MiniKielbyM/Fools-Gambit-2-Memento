import java.util.ArrayList;
import java.util.Arrays;

public class Main implements Callback {
    final static Main main = new Main();
    final static Gen_Attack pistol = new Gen_Attack(16, 10, 1, "Pistol",
            "A standard firearm for self defense, dealing 16-26 damage.");
    static Entity player = new Entity(100, new ArrayList<>(Arrays.asList(new Lucky(), pistol)), true, main);
    static Entity enemy = new Entity(100, new ArrayList<>(Arrays.asList(pistol)), false, main);

    @Override
    public void call(Object... args) {
        String[] type = ((String) args[0]).split("\\.");
        if (type[0].equals("player")) {
            if (type[1].equals("damage")) {
                int damage = (int) args[1];
                player.hp -= damage;
                System.out.println("Player takes " + damage + " damage!");
            }
        }
        if (type[0].equals("enemy")) {
            if (type[1].equals("damage")) {
                int damage = (int) args[1];
                enemy.hp -= damage;
                System.out.println("Enemy takes " + damage + " damage!");
            }
        }
    }

    public static void main(String[] args) {
        while (player.hp > 0 && enemy.hp > 0) {
            player.doTurn();
            if (enemy.hp > 0) {
                enemy.doTurn();
            }
            else{
                break;
            }
        }
    }
}