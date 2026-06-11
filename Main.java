import java.util.*;

public class Main {

    private static final boolean TRAINING_MODE = false;
    private static final int TRAINING_BATTLES = 10000;

    public static ArrayList<Action> createDeck() {
        return new ArrayList<>(Arrays.asList(
                new Lucky(),
                Action_List.pistol,
                Action_List.shotgun,
                Action_List.rifle,
                Action_List.cover,
                Action_List.fireball,
                Action_List.ice_barrier
        ));
    }

    public static void main(String[] args) {

        if (TRAINING_MODE) {
            runTraining();
        } else {
            runPlayerGame();
        }
    }

    private static void runTraining() {

        System.out.println("Training started...");

        for (int i = 1; i <= TRAINING_BATTLES; i++) {

            Entity ai1 = new Entity(
                    100,
                    createDeck(),
                    false);

            Entity ai2 = new Entity(
                    100,
                    createDeck(),
                    false);

            ai1.temperature = 2.4;
            ai2.temperature = 2.4;

            Combat_Engine engine = new Combat_Engine(ai1, ai2);
            engine.run();

            if (i % 100 == 0) {
                System.out.println(
                        "Completed " + i + "/" + TRAINING_BATTLES + " battles");
            }
        }

        System.out.println("Training complete.");
    }

    private static void runPlayerGame() {

        Entity player = new Entity(
                100,
                createDeck(),
                true);

        Entity enemy = new Entity(
                100,
                createDeck(),
                false);

        enemy.temperature = 2.4;

        Combat_Engine engine = new Combat_Engine(player, enemy);
        engine.run();
    }
}