public class Action_Result {

    enum Type {
        DAMAGE,
        BLOCK,
        DAMAGE_PARA,
        BLOCK_PARA,
        EMPTY
    }

    public final Type type;
    public final int a;
    public final int b;
    public final String name;

    public static final Action_Result EMPTY =
            new Action_Result(Type.EMPTY, 0, 0, "none");

    public Action_Result(Type type, int a, int b, String name) {
        this.type = type;
        this.a = a;
        this.b = b;
        this.name = name;
    }

    public static Action_Result from(int[] res, Action a) {
        return new Action_Result(
                switch (res[0]) {
                    case 1 -> Type.DAMAGE;
                    case 2 -> Type.BLOCK;
                    case 3 -> Type.DAMAGE_PARA;
                    case 4 -> Type.BLOCK_PARA;
                    default -> Type.EMPTY;
                },
                res.length > 1 ? res[1] : 0,
                res.length > 2 ? res[2] : 0,
                a.name
        );
    }
}