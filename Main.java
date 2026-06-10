
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main implements Callback {

    final static Main main = new Main();
    static Scanner input = new Scanner(System.in);

    static ArrayList<Action> baseDeck = new ArrayList<>(
            Arrays.asList(
                    new Lucky(),
                    Action_List.pistol,
                    Action_List.shotgun,
                    Action_List.rifle,
                    Action_List.cover
            )
    );

    static Entity player = new Entity(
            100,
            new ArrayList<>(baseDeck),
            true
    );

    static Entity enemy = new Entity(
            100,
            new ArrayList<>(baseDeck),
            false
    );

    @Override
    public void call(Object... args) {
        String[] type = args[0].toString().split("\\.");

        if (type[0].equals("player") && type[1].equals("damage")) {
            int damage = (int) args[1];
            player.hp -= damage;
            System.out.println("Player takes " + damage + " damage");
        }

        if (type[0].equals("enemy") && type[1].equals("damage")) {
            int damage = (int) args[1];
            enemy.hp -= damage;
            System.out.println("Enemy takes " + damage + " damage");
        }
    }

    public static void main(String[] args) {
        Combat_Engine engine = new Combat_Engine(player, enemy, main);
        engine.run();
    }
}
