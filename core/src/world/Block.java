package world;

public enum Block {
    AIR(false, 0),
    GRASS(true, 1),
    DIRT(true, 2),
    STONE(true, 3);

    private final boolean solid;
    private final int id;

    Block(boolean solid, int id) {
        this.solid = solid;
        this.id = id;
    }

    public boolean isSolid() {
        return solid;
    }

    public int getId() {
        return id;
    }
}
