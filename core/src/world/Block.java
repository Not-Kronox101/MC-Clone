package world;

public enum Block {
    AIR(0, false),
    GRASS(1, true),
    DIRT(2, true),
    STONE(3, true),
    BEDROCK(4, true),
    WATER(5, false),
    LOG(6, true),
    LEAVES(7, false);

    private final int id;
    private final boolean solid;

    Block(int id, boolean solid) {
        this.id = id;
        this.solid = solid;
    }

    public int getId() {
        return id;
    }

    public boolean isSolid() {
        return solid;
    }

    public static Block fromId(int id) {
        for (Block block : values()) {
            if (block.id == id) {
                return block;
            }
        }
        return AIR;
    }
}
