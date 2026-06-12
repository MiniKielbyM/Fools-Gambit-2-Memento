import java.util.*;

public class Main {

    public static final boolean TRAINING_MODE = false;
    private static final int TRAINING_BATTLES = 10000;

    public static void main(String[] args) {

        if (TRAINING_MODE) {
            runTraining();
        } else {
            System.out.println(
                    "Welcome to Fools Gambit: Memento!\nIn this game you have a deck of cards with abilities, each cost action points.\nYou get 3 action points per turn. This cards with either damage your enemy or gain you block.\n\nBLOCK will prevent damage equal to your block when you are hit,\nat the start of your next turn you loose all unused block.\n\nSome abilities may cost PARA (or paralization), if you gain 100 para you lose the game.\nWhenever you use an ability that gains you para you gain tolerence equal to half that amount.\nYou only gain para equal to the para cost-your tolerence.\n\nYour goal is to kill as many enemies as possible without killing youself. \n\nHave fun!");
            runPlayerGame();
        }
    }

    private static void runTraining() {

        System.out.println("Training started...");

        for (int i = 1; i <= TRAINING_BATTLES; i++) {

            Entity ai1 = new Entity(
                    100,
                    new ArrayList<Action>(Arrays.asList(Action_List.ALL_ACTIONS)),
                    false);

            Entity ai2 = new Entity(
                    100,
                    new ArrayList<Action>(Arrays.asList(Action_List.ALL_ACTIONS)),
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
                new ArrayList<>(Arrays.asList(Action_List.pistol, Action_List.cover, Action_List.pistol,
                        Action_List.cover, Action_List.pistol, Action_List.cover, Action_List.pistol, Action_List.cover,
                        Action_List.pistol, Action_List.cover, Action_List.fireball)),
                true);
        Entity enemy = new Entity(
                100,
                new ArrayList<Action>(Arrays.asList(Action_List.pistol, Action_List.cover, Action_List.fireball,
                        Action_List.pistol, Action_List.rifle, Action_List.cover, Action_List.shotgun,
                        Action_List.fireball, Action_List.pistol, Action_List.rifle, Action_List.cover,
                        Action_List.shotgun, Action_List.fireball, Action_List.pistol, Action_List.rifle,
                        Action_List.cover, Action_List.shotgun)),
                false);

        enemy.temperature = 2.4;

        Combat_Engine engine = new Combat_Engine(player, enemy);
        engine.run();
    }
}