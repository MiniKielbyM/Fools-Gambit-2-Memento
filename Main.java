import java.util.*;

public class Main {

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

    static Entity player = new Entity(100, new ArrayList<>(baseDeck), true);
    static Entity enemy = new Entity(100, new ArrayList<>(baseDeck), false);

    public static void main(String[] args) {

        player.setTarget(enemy);
        enemy.setTarget(player);

        enemy.temperature = 1.2;
        player.temperature = 1.0;

        Combat_Engine engine = new Combat_Engine(player, enemy);
        engine.run();
    }
}