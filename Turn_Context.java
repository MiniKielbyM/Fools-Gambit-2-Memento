public class Turn_Context {

    public final int hp;
    public final int enemyHp;
    public final int block;
    public final int enemyBlock;
    public final int para;
    public final int enemyPara;
    public final int energy;

    public Turn_Context(Entity e) {
        this.hp = e.hp;
        this.enemyHp = e.target.hp;
        this.block = e.block;
        this.enemyBlock = e.target.block;
        this.para = e.para;
        this.enemyPara = e.target.para;
        this.energy = e.stats[4];
    }

    public static Turn_Context from(Entity e) {
        return new Turn_Context(e);
    }
}