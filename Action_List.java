
public class Action_List {

    //Creates actions the player can take during their turn
    public final static Gen_Attack pistol = new Gen_Attack(16, 10, 1, "Pistol","A standard firearm for self defense, dealing 16-26 damage");
    public final static Gen_Attack rifle = new Gen_Attack(22, 3, 1, "Rifle", "A standard issue millitary firearm, dealing 22-25 damage");
    public final static Gen_Attack shotgun = new Gen_Attack(10, 25, 1, "Shotgun", "A dusty double barrel shotgun, highly unpredictible, deals 10-35 damage");
    public final static Gen_Attack fireball = new Gen_Attack(30, 30, 2, 40, "FIREBALL", "Launch a ball of fire, dealing 30-60 damage and gaining 40 para");
    public final static Gen_Block cover = new Gen_Block(15, 0, 1, "Take Cover", "Hide behind an object or fortification, gaining 15 block");
    public final static Gen_Block ice_barrier = new Gen_Block(30, 0, 1, 15, "Ice Barrier", "Create a wall of ice, gaining 30 block, 15 para");
    public final static Gen_Block diamond_shield = new Gen_Block(50, 0, 2, 40, "Shimmering Sheild", "Manifest a barrier of diamonds, gaining 60 block and 40 para");
    public final static Gen_Attack diamond_cutter = new Gen_Attack(40, 0, 2, 40, "Diamond Cutter", "Cut with a blade of diamonds, dealing 40 damage and gaining 40 para");
    public final static Gen_Block ashen_veil = new Gen_Block(8, 0, 0, 10, "Ashen Veil", "Create a barrier of ashes gaining 8 block and 10 para");
    public final static Action[] ALL_ACTIONS = {pistol,rifle,shotgun,fireball,cover,ice_barrier,new Lucky(), diamond_shield, diamond_cutter, ashen_veil};
}
