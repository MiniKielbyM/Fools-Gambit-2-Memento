import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main implements Callback {
    final static Main main = new Main();

    //Creates actions the player can take during their turn
    final static Gen_Attack pistol = new Gen_Attack(16, 10, 1, "Pistol",
            "A standard firearm for self defense, dealing 16-26 damage");
    final static Gen_Attack rife = new Gen_Attack(22, 3, 1,"Rifle", "A standard issue millitary firearm, dealing 22-25 damage");
    final static Gen_Attack shotgun = new Gen_Attack(10, 25, 1, "Shotgun", "A dusty double barrel shotgun, highly unpredictible, deals 10-35 damage");
    final static Gen_Attack fireball = new Gen_Attack(30, 30, 2, 40, "FIREBALL", "Launch a ball of fire, dealing 30-60 damage and gaining 40 para");
    final static Gen_Block cover = new Gen_Block(15, 0, 1, "Take Cover","Hide behind an object or fortification, gaining 15 block");
    final static Gen_Block ice_barrier = new Gen_Block(30, 0, 1, 15, "Ice Barrier","Create a wall of ice, gaining 30 block, 15 para");
    //Creates the entities (players and enemies)
    static Entity player = new Entity(100, new ArrayList<>(Arrays.asList(new Lucky(), pistol, shotgun, rife, cover)), true, main);
    static Entity enemy = new Entity(100, new ArrayList<>(Arrays.asList(new Lucky(), pistol, shotgun, rife, cover)), false, main);
    static final Reward str = new Reward("Strength +3", "Raise your strength stat by 3 (rasing your non-special damage by 3)",
    0, 3);
    static final Reward dex = new Reward("Dexterity +2", "Raise your dexterity stat by 2 (rasing your block gained by 2)",
    1, 2);
    static final Reward cha = new Reward("Charisma +3", "Raise your charisma stat by 3 (raising your special block gained and damage by 3)",
    2, 3);
    static final Reward tol = new Reward("Tolerence +5", "Raise your tolerence by 5, (lowering paralization gained by 5)",
    3, 5);
    static final Reward[] rewards = {str, dex, cha, tol};
    static Scanner input = new Scanner(System.in);

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
        while(player.hp > 0)
        {
            while (player.hp > 0 && player.para <100 && enemy.hp > 0 && enemy.para <100) {
                player.doTurn();
                if (enemy.hp > 0) {
                    enemy.doTurn();
                }
                else{
                    break;
                }
            }
            System.out.println("\n\n");
            for(int i = 0; i<3; i++)
            {
                int rand = (int) (Math.random()*rewards.length);
                System.out.println(i+ ": " + rewards[rand]);
            }
            System.out.print("Enter the number of the reward you want: ");
            rewards[input.nextInt()].gain(player);
            enemy.hp = 100;
        }

    }
}